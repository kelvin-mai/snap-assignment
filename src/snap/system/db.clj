(ns snap.system.db
  (:require [integrant.core :as ig]
            [taoensso.timbre :as log]
            [hikari-cp.core :as hikari]
            [migratus.core :as migratus]))

(defmethod ig/init-key :postgres/db
  [_ {:keys [config]}]
  (log/info "starting database connection pool")
  (let [options (:db config)
        datasource (hikari/make-datasource options)
        migration-config {:store :database
                          :migration-dir "migrations"
                          :db {:datasource datasource}}]
    (log/info "using database migrations")
    (log/info "pending migrations:" (migratus/pending-list migration-config))
    (migratus/migrate migration-config)
    (log/info "completed migrations:" (migratus/completed-list migration-config))
    datasource))

(defmethod ig/halt-key! :postgres/db
  [_ ds]
  (log/info "closing database connection pool")
  (hikari/close-datasource ds))
