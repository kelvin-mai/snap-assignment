(ns snap.api.todo-list.db
  (:require [snap.utils.db :as db]
            [snap.api.todo-item.db :as todo-item.db]))

(defn get-all
  [db]
  (db/query! db
             {:select [:*
                       [{:select [[[:count :*]]]
                         :from :todo-item
                         :where [:= :todo-item.todo-list-id :todo-list.id]}
                        :todo-items]
                       [{:select [[[:count :*]]]
                         :from :todo-item
                         :where [:and
                                 [:= :todo-item.todo-list-id :todo-list.id]
                                 [:= :todo-item.completed true]]}
                        :todo-items-completed]]
              :from :todo-list}))

(defn get-by-id
  [db id]
  (let [todo-list (db/query-one! db
                                 {:select [:*]
                                  :from :todo-list
                                  :where [:= :todo-list.id id]})
        todo-items (when todo-list
                     (todo-item.db/get-all-by-todo-list-id db id))]
    (when todo-list
      (assoc todo-list :todo-list/todo-items todo-items))))

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
