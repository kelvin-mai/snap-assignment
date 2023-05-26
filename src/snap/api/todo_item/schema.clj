(ns snap.api.todo-item.schema
  (:require [snap.utils.schema :refer [nanoid?
                                       non-blank-string?]]))

(def path-param
  [:map
   [:todo-item-id (nanoid? 12)]])

(def create-body
  [:map
   [:name non-blank-string?]])

(def update-body
  [:map
   [:name {:optional true} non-blank-string?]
   [:completed {:optional true} boolean?]])
