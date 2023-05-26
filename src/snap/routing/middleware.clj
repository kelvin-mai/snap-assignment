(ns snap.routing.middleware
  (:require [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as rcc]
            [snap.routing.exception :as exception]))

(def wrap-env
  {:name ::env
   :compile
   (fn [{:keys [env]} _]
     (fn [handler]
       (fn [request]
         (handler (merge request env)))))})

(def global-middlewares
  [parameters/parameters-middleware
   muuntaja/format-middleware
   exception/exception-middleware
   rcc/coerce-request-middleware
   rcc/coerce-response-middleware
   wrap-env])
