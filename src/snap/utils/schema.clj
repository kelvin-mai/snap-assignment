(ns snap.utils.schema
  (:require [malli.core :as m]
            [clojure.string :as s]))

(defn nanoid?
  ([] (nanoid? 21))
  ([len]
   (m/-simple-schema
    {:type (keyword (str "nanoid-" len))
     :pred #(and (string? %)
                 (boolean (re-matches #"^[a-zA-Z0-9-_]+$" %))
                 (= (count %) len))})))

(def non-blank-string?
  (m/-simple-schema
   {:type :non-blank-string
    :pred #(and (string? %)
                (not (s/blank? %)))}))

(def currency?
  (m/-simple-schema
   {:type :currency
    :pred #(and (double? %)
                ;; TODO: add boolean for only 2 decimal places
                (pos? %))}))
