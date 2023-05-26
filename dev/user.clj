(ns user
  (:require [clojure.tools.namespace.repl :as tools-ns]
            [nrepl.server]
            [integrant.repl :as ig-repl :refer [go halt]]
            [integrant.repl.state :as state]
            [snap.system.core :refer [read-config]]))

(tools-ns/set-refresh-dirs "dev" "src")

(declare db)

(ig-repl/set-prep!
 (fn [] (read-config :dev)))

(defn start-interactive []
  (go)
  (def db (:postgres/db state/system))
  (def router (:reitit/router state/system))
  :ready!)

(defn restart []
  (halt)
  (tools-ns/refresh :after 'user/start-interactive))

(comment
  (halt)
  (restart)
  state/system)
