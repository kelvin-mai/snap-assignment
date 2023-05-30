(ns snap.api.product.db
  (:require [snap.utils.db :as db]))

(defn get-all
  [db query]
  (let [base-query {:select [:*]
                    :from :product}
        where-clause {:where [:> :product.quantity 0]}
        final-query (if (:available query)
                      (merge base-query where-clause)
                      base-query)]
    (db/query! db final-query)))

(defn get-by-id
  [db id]
  (db/query-one! db
                 {:select [:*]
                  :from :product
                  :where [:= :product.id id]}))

(defn create
  [db data]
  (db/query-one! db
                 {:insert-into :product
                  :values [data]}))

(defn update
  [db id data]
  (db/query-one! db
                 {:update :product
                  :set data
                  :where [:= :product.id id]}))

(defn delete-by-id
  [db id]
  (db/query-one! db
                 {:delete-from :product
                  :where [:= :product.id id]}))
