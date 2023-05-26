(ns snap.api.routes)

(def health-route
  ["/health-check"
   {:name ::health-check
    :get (fn [_] {:status 200
                  :body {:ping "pong"}})}])

(def api-routes
  [["/api"
    health-route]])
