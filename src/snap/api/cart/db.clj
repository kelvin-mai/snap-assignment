(ns snap.api.cart.db
  (:require [snap.utils.db :as db]))

(defn get-all
  [db]
  (db/query! db {:select [:*]
                 :from :cart}))

(defn get-by-id
  [db id]
  (db/query-one! db {:select [:*]
                     :from :cart
                     :where [:= :cart.id id]}))

(defn create
  [db]
  (db/query-one! db
                 {:insert-into :cart
                  :values :default}))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :cart
                  :where [:= :cart.id id]}))
