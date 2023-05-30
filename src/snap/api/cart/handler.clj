(ns snap.api.cart.handler
  (:require [snap.api.cart.db :as cart.db]
            [snap.api.cart.schema :as cart.schema]
            [snap.routing.response :refer [ok created]]
            [snap.routing.exception :refer [not-found]]))

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

(def routes
  ["/cart"
   ["" {:get get-all-carts
        :post create-cart}]
   ["/:id" {:parameters {:path cart.schema/path-param}
            :get get-cart-by-id
            :delete delete-cart}]])
