(ns snap.api.cart.db
  (:require [snap.utils.db :as db]
            [next.jdbc :as jdbc]))

(defn get-all
  [db]
  (db/query! db {:select [:*
                          [{:select [[[:sum [:* :product.price :cart-product.quantity]]]]
                            :from :cart-product
                            :join [:product
                                   [:= :product.id :cart-product.product-id]]
                            :where [:= :cart-product.cart-id :cart.id]}
                           :total-price]
                          [{:select [[[:sum :cart-product.quantity]]]
                            :from :cart-product
                            :where [:= :cart-product.cart-id :cart.id]}
                           :items-in-cart]
                          [{:select [[[:count :*]]]
                            :from :cart-product
                            :where [:= :cart-product.cart-id :cart.id]
                            :group-by [:cart.id]}
                           :products-in-cart]]
                 :from :cart}))

(defn get-by-id
  [db id]
  (let [cart (db/query-one! db {:select [:*
                                         [{:select [[[:sum [:* :product.price :cart-product.quantity]]]]
                                           :from :cart-product
                                           :join [:product
                                                  [:= :product.id :cart-product.product-id]]
                                           :where [:= :cart-product.cart-id :cart.id]}
                                          :total-price]]
                                :from :cart
                                :where [:= :cart.id id]})
        cart-products (db/query! db {:select [:*]
                                     :from :cart-product
                                     :join [:product
                                            [:= :product.id :cart-product.product-id]]
                                     :where [:= :cart-product.cart-id id]})]
    (when cart
      (assoc cart :cart/cart-products cart-products))))

(defn create
  [db]
  (db/query-one! db
                 {:insert-into :cart
                  :values :default}))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :cart
                  :where [:= :cart.id id]}))

(defn add-to-cart
  [db data]
  (db/query-one! db
                 {:insert-into :cart-product
                  :values [data]}))

(defn get-cart-product
  [db {:keys [cart-id
              product-id]}]
  (db/query-one! db
                 {:select [:*]
                  :from :cart-product
                  :where [:and
                          [:= :cart-product.cart-id cart-id]
                          [:= :cart-product.product-id product-id]]}))

(defn remove-from-cart
  [db {:keys [cart-id
              product-id]}]
  (db/query-one! db
                 {:delete-from :cart-product
                  :where [:and
                          [:= :cart-product.cart-id cart-id]
                          [:= :cart-product.product-id product-id]]}))

(defn check-cart-compatibility
  [db data]
  (let [{:keys [product-id
                cart-id
                quantity]} data]
    (when
     (db/query-one! db
                    {:select [:cart-id]
                     :from :cart-product
                     :where [:and
                             [:= :cart-product.product-id product-id]
                             [:= :cart-product.cart-id cart-id]]})
      (throw (ex-info "Conflict" {:cause "product already in cart"
                                  :data data})))
    (when
     (db/query-one! db
                    {:select [:id]
                     :from :product
                     :where [:and
                             [:< :product.quantity quantity]
                             [:= :product.id product-id]]})
      (throw (ex-info "Conflict" {:cause "quantity too large"
                                  :data data})))
    true))

(defn checkout-cart
  [db id]
  (jdbc/with-transaction [tx db]
    (let [new-quantities (db/query! tx
                                    {:select [:product-id
                                              [[[:- :product.quantity :cart-product.quantity]]
                                               :quantity]]
                                     :from :cart-product
                                     :join [:product
                                            [:= :product.id :cart-product.product-id]]
                                     :where [:and
                                             [:= :cart-product.cart-id id]]})
          _update-products (doseq [update-query (map (fn [p]
                                                       {:update :product
                                                        :set {:quantity (:quantity p)}
                                                        :where [:= :product.id (:cart-product/product-id p)]})
                                                     new-quantities)]
                             (db/query-one! tx update-query))]
      (db/query-one! tx
                     {:update :cart
                      :set {:checked-out true}
                      :where [:= :cart.id id]}))))

(defn checkout-cart-compatibility
  [db id]
  (let [cart (db/query-one! db
                            {:select [:checked-out]
                             :from :cart
                             :where [:= :cart.id id]})
        invalid-cart-products (db/query! db
                                         {:select [:*]
                                          :from :cart-product
                                          :join [:product
                                                 [:= :product.id :cart-product.product-id]]
                                          :where [:and
                                                  [:= :cart-product.cart-id id]
                                                  [:> :cart-product.quantity :product.quantity]]})]
    (when (:cart/checked-out cart)
      (throw (ex-info "Conflict" {:cause "cart already checked out"
                                  :data cart})))
    (when (> (count invalid-cart-products) 0)
      (throw (ex-info "Conflict" {:cause "some product quantity too large"
                                  :data invalid-cart-products})))
    true))
