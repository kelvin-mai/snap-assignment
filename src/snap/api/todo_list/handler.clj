(ns snap.api.todo-list.handler
  (:require [snap.api.todo-list.db :as todo-list.db]
            [snap.api.todo-list.schema :as todo-list.schema]
            [snap.routing.response :refer [ok created]]
            [snap.routing.exception :refer [not-found]]))

(defn get-all-todo-lists
  [{:keys [db]}]
  (let [todo-lists (todo-list.db/get-all db)
        response (or todo-lists [])]
    (ok response)))

(defn create-todo-list
  [{:keys [db parameters]}]
  (let [body (:body parameters)
        response (todo-list.db/create db body)]
    (created response)))

(defn get-todo-list-by-id
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        todo-list (todo-list.db/get-by-id db id)]
    (if todo-list
      (ok todo-list)
      (not-found request))))

(defn delete-todo-list
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :id])
        response (todo-list.db/delete-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/todo-list"
   ["" {:get get-all-todo-lists
        :post {:parameters {:body todo-list.schema/create-body}
               :handler create-todo-list}}]
   ["/:id" {:parameters {:path todo-list.schema/path-param}
            :get get-todo-list-by-id
            :delete delete-todo-list}]])
