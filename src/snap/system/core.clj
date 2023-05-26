(ns snap.system.core
  (:require [aero.core :as aero]
            [taoensso.timbre :as log]
            [integrant.core :as ig]
            snap.system.db))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod ig/init-key :system/config
  [_ config]
  (log/info "system starting with config" config)
  config)

(defn read-config
  ([] (read-config :prod))
  ([profile]
   (aero/read-config
    "resources/config.edn"
    {:profile profile})))
