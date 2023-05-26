(ns snap.routing.middleware
  (:require [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as rcc]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (merge request env)))))})

(def global-middlewares
  [muuntaja/format-middleware
   rcc/coerce-exceptions-middleware
   rcc/coerce-request-middleware
   rcc/coerce-response-middleware
   wrap-env])
