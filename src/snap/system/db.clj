(ns snap.system.db
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [hikari-cp.core :as hikari]))

(defmethod ig/init-key :postgres/db
  [_ {:keys [config]}]
  (log/info "starting database connection pool")
  (let [options (:db config)]
    (hikari/make-datasource options)))

(defmethod ig/halt-key! :postgres/db
  [_ ds]
  (log/info "closing database connection pool")
  (hikari/close-datasource ds))
