(ns snap.product.route-utils
  (:require [snap.test-utils :refer [request]]))

(defn create-product-fn
  [router]
  (fn [data]
    (-> (request router
                 :post "/api/product"
                 {:body-params data})
        :data)))

(defn delete-product-fn
  [router]
  (fn [id]
    (-> (request router
                 :delete (str "/api/product/" id)))))
