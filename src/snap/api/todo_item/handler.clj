(ns snap.api.todo-item.handler
  (:require [snap.api.todo-item.db :as todo-item.db]
            [snap.api.todo-item.schema :as todo-item.schema]
            [snap.routing.response :refer [ok created]]
            [snap.routing.exception :refer [not-found]]))

(defn get-all-by-todo-list-id
  [{:keys [db parameters]}]
  (let [id (get-in parameters [:path :todo-list-id])
        response (todo-item.db/get-all-by-todo-list-id db id)]
    (ok response)))

(defn create-todo-item
  [{:keys [db parameters]}]
  (let [body (:body parameters)
        todo-list-id (get-in parameters [:path :todo-list-id])
        data (assoc body :todo-list-id todo-list-id)
        response (todo-item.db/create db data)]
    (created response)))

(defn get-todo-item
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :todo-item-id])
        response (todo-item.db/get-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn delete-todo-item
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :todo-item-id])
        response (todo-item.db/delete-by-id db id)]
    (if response
      (ok response)
      (not-found request))))

(defn update-todo-item
  [{:keys [db parameters] :as request}]
  (let [id (get-in parameters [:path :todo-item-id])
        body (:body parameters)
        response (todo-item.db/update db id body)]
    (if response
      (ok response)
      (not-found request))))

(def routes
  ["/todo-item"
   #_["" {:get get-all-by-todo-list-id
          :post {:parameters {:body todo-item.schema/create-body}
                 :handler create-todo-item}}]
   ["/:todo-item-id" {:parameters {:path todo-item.schema/path-param}
                      :get get-todo-item
                      :delete delete-todo-item
                      :put {:parameters {:body todo-item.schema/update-body}
                            :handler update-todo-item}}]])
