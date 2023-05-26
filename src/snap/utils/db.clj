(ns snap.utils.db
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn- make-query
  [f db sql-map]
  (f db
     (sql/format sql-map)
     {:return-keys true
      :builder-fn rs/as-kebab-maps}))

(defn query!
  [& args]
  (apply make-query jdbc/execute! args))

(defn query-one!
  [& args]
  (apply make-query jdbc/execute-one! args))
