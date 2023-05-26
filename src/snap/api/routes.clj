(ns snap.api.routes
  (:require [snap.api.todo-list.handler :as todo-list]))

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_] {:status 200
                  :body {:ping "pong"}})}])

(def api-routes
  [["/api"
    health-route
    todo-list/routes]])
