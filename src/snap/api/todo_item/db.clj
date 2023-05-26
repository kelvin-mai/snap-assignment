(ns snap.api.todo-item.db
  (:require [snap.utils.db :as db]))

(defn get-all-by-todo-list-id
  [db todo-list-id]
  (db/query! db
             {:select [:*]
              :from :todo-item
              :where [:= :todo-item.todo-list-id todo-list-id]}))

(defn get-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from :todo-item
                  :where [:= :todo-item.id id]}))

(defn create
  [db data]
  (db/query-one! db
                 {:insert-into :todo-item
                  :values [data]}))

(defn update
  [db id data]
  (db/query-one! db
                 {:update :todo-item
                  :set data
                  :where [:= :todo-item.id id]}))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :todo-item
                  :where [:= :todo-item.id id]}))
