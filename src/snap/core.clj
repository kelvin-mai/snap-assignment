(ns snap.core
  (:require [integrant.core :as ig]
            [snap.system.core :refer [read-config]]))

(defn -main []
  (let [config (read-config)]
    (ig/init config)))
