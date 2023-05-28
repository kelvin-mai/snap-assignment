(ns snap.system.server
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [org.httpkit.server :as http]))

(defmethod ig/init-key :http/server
  [_ {:keys [router config]}]
  (let [port (:port config)]
    (log/info "server started on port" port)
    (http/run-server router {:port port})))

(defmethod ig/halt-key! :http/server
  [_ server]
  (log/info "server stopping")
  (server :timeout 100))
