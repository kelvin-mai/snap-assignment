(ns snap.core
  (:require [snap.system.core :refer [read-config]]))

(defn -main []
  (read-config))
