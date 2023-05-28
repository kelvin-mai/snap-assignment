;; based on https://github.com/paulbutcher/clj-migratus
(ns snap.migrations.core
  (:require [migratus.core :as migratus]))

(defn init [config]
  (migratus/init config))

(defn create [config name]
  (migratus/create config name))

(defn migrate [config]
  (migratus/migrate config))

(defn rollback [config]
  (migratus/rollback config))

(defn reset [config]
  (migratus/reset config))

(defn rollback-until-just-after [config id]
  (migratus/rollback-until-just-after config (Long/parseLong id)))

(defn up [config ids]
  (->> ids
       (map #(Long/parseLong %))
       (apply migratus/up config)))

(defn down [config ids]
  (->> ids
       (map #(Long/parseLong %))
       (apply migratus/down config)))

(defn pending-list [config]
  (migratus/pending-list config))

(defn migrate-until-just-before [config id]
  (migratus/migrate-until-just-before config (Long/parseLong id)))

(defn -main [& args]
  (let [command (first args)
        ;; hard coding for CLI only
        config {:store :database
                :migration-dir "migrations"
                :db {:dbtype "postgresql"
                     :dbname "snap_db"
                     :user "postgres"
                     :password "postgres"}}]
    (condp = command
      "init" (init config)
      "create" (create config (second args))
      "migrate" (migrate config)
      "rollback" (rollback config)
      "reset" (reset config)
      "rollback-until-just-after" (rollback-until-just-after config (second args))
      "up" (up config (rest args))
      "down" (down config (rest args))
      "pending-list" (println (pending-list config))
      "migrate-until-just-before" (migrate-until-just-before config (second args)))))
