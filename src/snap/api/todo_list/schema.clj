(ns snap.api.todo-list.schema
  (:require [snap.utils.schema :refer [nanoid?
                                       non-blank-string?]]))

(def path-param
  [:map [:id (nanoid? 12)]])

(def create-body
  [:map
   [:name non-blank-string?]])
