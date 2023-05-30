(ns snap.api.product.handler
  (:require [snap.api.product.db :as product.db]
            [snap.api.product.schema :as product.schema]
            [snap.routing.response :refer [ok created]]
            [snap.routing.exception :refer [not-found]]))

(defn get-all-products
  [{:keys [db parameters]}]
  (let [query (:query parameters)
        response (product.db/get-all db query)]
    (ok response)))

(defn create-product
  [{:keys [db parameters]}]
  (let [body (:body parameters)
        response (product.db/create db body)]
    (created response)))

(defn get-product-by-id
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (product.db/get-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-product
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (product.db/delete-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn update-product
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        body (:body parameters)
        response (product.db/update db id body)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/product"
   ["" {:get {:parameters {:query product.schema/query-param}
              :handler get-all-products}
        :post {:parameters {:body product.schema/create-body}
               :handler create-product}}]
   ["/:id" {:parameters {:path product.schema/path-param}
            :get get-product-by-id
            :delete delete-product
            :put {:parameters {:body product.schema/update-body}
                  :handler update-product}}]])
