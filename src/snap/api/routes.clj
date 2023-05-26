(ns snap.api.routes
  (:require [snap.api.todo-list.handler :as todo-list]
            [snap.api.todo-item.handler :as todo-item]))

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_] {:status 200
                  :body {:ping "pong"}})}])

(def api-routes
  [["/api"
    health-route
    todo-list/routes
    todo-item/routes]])
