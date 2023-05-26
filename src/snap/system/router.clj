(ns snap.system.router
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [muuntaja.core :as m]
            [reitit.ring :as ring]

            [snap.routing.middleware :as mw]
            [snap.api.routes :refer [api-routes]]))

(defmethod ig/init-key :reitit/routes
  [_ {:keys [db]}]
  (log/info "initializing routes")
  (ring/ring-handler
   (ring/router
    api-routes
    {:data {:env {:db db}
            :muuntaja m/instance
            :middleware mw/global-middlewares}})
   (ring/routes
    (ring/redirect-trailing-slash-handler))))
