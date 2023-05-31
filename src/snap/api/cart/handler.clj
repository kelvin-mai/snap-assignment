(ns snap.api.cart.handler
  (:require [snap.api.cart.db :as cart.db]
            [snap.api.cart.schema :as cart.schema]
            [snap.routing.response :refer [ok created]]
            [snap.routing.exception :refer [not-found handle-exception]]))

(defn get-all-carts
  [{:keys [db]}]
  (ok (cart.db/get-all db)))

(defn create-cart
  [{:keys [db]}]
  (created (cart.db/create db)))

(defn get-cart-by-id
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (cart.db/get-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-cart
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (cart.db/delete-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn add-to-cart
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        body (:body parameters)
        data (assoc body :cart-id id)
        cart (cart.db/get-by-id db id)]
    (if cart
      (when (cart.db/check-cart-compatibility db data)
        (created (cart.db/add-to-cart db data)))
      (not-found request))))

(defn remove-from-cart
  [{:keys [db parameters] :as request}]
  (let [{:keys [id
                product-id]} (:path parameters)
        data {:cart-id id
              :product-id product-id}
        cart-product (cart.db/get-cart-product db data)]
    (if cart-product
      (ok (cart.db/remove-from-cart db data))
      (not-found request))))

(defn checkout
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        cart (cart.db/get-by-id db id)]
    (if cart
      (when (cart.db/checkout-cart-compatibility db id)
        (ok (cart.db/checkout-cart db id)))
      (not-found request))))

(def routes
  ["/cart"
   ["" {:get get-all-carts
        :post create-cart}]
   ["/:id"
    ["" {:parameters {:path cart.schema/path-param}
         :get get-cart-by-id
         :put checkout
         :delete delete-cart
         :post {:parameters {:body cart.schema/add-to-cart-body}
                :handler add-to-cart}}]
    ["/:product-id" {:parameters {:path cart.schema/nested-path-param}
                     :delete remove-from-cart}]]])
