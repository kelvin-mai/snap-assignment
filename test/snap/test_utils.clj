(ns snap.test-utils
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
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

(defn request
  ([router method uri]
   (request router method uri {}))
  ([router method uri opts]
   (->> (router (merge
                 {:request-method method
                  :uri uri}
                 opts))
        :body
        (m/decode "application/json"))))
