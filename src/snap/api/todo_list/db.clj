(ns snap.api.todo-list.db
  (:require [snap.utils.db :as db]))

(defn get-all
  [db]
  (db/query! db
             {:select [:*]
              :from :todo-list}))

(defn get-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from :todo-list
                  :where [:= :todo-list.id id]}))

(defn create
  [db data]
  (db/query-one! db
                 {:insert-into :todo-list
                  :values [data]}))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :todo-list
                  :where [:= :todo-list.id id]}))
