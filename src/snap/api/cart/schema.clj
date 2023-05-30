(ns snap.api.cart.schema
  (:require [snap.utils.schema :refer [nanoid?]]))

(def path-param
  [:map
   [:id (nanoid? 12)]])
