(ns snap.test-utils
  (:require [integrant.core :as ig]
            [snap.system.core :as system]))

(def test-system (atom nil))

(defn use-system []
  (fn [test-fn]
    (reset! test-system
            (let [config (system/read-config :test)]
              (ig/init config)))
    (test-fn)
    (ig/halt! @test-system)
    (reset! test-system nil)))
