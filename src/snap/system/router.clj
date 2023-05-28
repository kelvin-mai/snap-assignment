(ns snap.system.router
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.malli :as malli]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.swagger :as swagger]
            [snap.routing.middleware :as mw]
            [snap.api.routes :refer [api-routes]]))

(defmethod ig/init-key :reitit/routes
  [_ {:keys [db]}]
  (log/info "initializing routes")
  (ring/ring-handler
   (ring/router
    [["/swagger.json" {:get {:no-doc true
                             :swagger {:info {:title "Snap Assignment App"}
                                       :basePath "/"}}
                       :handler (swagger/create-swagger-handler)}]
     api-routes]
    {:data {:env {:db db}
            :coercion malli/coercion
            :muuntaja m/instance
            :middleware mw/global-middlewares}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"})
    (ring/redirect-trailing-slash-handler))))
