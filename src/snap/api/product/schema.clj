(ns snap.api.product.schema
  (:require [snap.utils.schema :refer [nanoid?
                                       non-blank-string?
                                       currency?]]))

(def path-param
  [:map
   [:id (nanoid? 12)]])

(def query-param
  [:map
   [:available {:optional true} boolean?]])

(def create-body
  [:map
   [:name non-blank-string?]
   [:price {:optional true} currency?]
   [:quantity {:optional true} pos-int?]])

(def update-body
  [:map
   [:name {:optional true} non-blank-string?]
   [:price {:optional true} currency?]
   [:quantity {:optional true} pos-int?]])
