(ns stream.migrate
  (:import [com.googlecode.flyway.core Flyway]
           [com.googlecode.flyway.core.util.jdbc DriverDataSource]
           com.googlecode.flyway.core.api.FlywayException)
  (:require [stream.config :as config]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn create-datasource!
  "Flyway DataSource"
  [db-spec]
  (let [datasource (DriverDataSource.
                    (db-spec :classname)
                    (db-spec :uri)
                    (db-spec :user)
                    (db-spec :password)
                    (into-array String ""))]
    datasource))

(defn migrate!
  [datasource options]
  (let [flyway (Flyway.)
        cleanup (:cleanup options)]
    (log/info "cleanup " cleanup)
    (.setDataSource flyway datasource)
    (.setLocations flyway (into-array String ["/db/migration"]))
    (when cleanup (.clean flyway))
    (log/info (.getDataSource flyway))
    (.migrate flyway)))

(defn -main [& args]
  (let [datasource (create-datasource! config/db)
        options {:cleanup false}
        migration-status (try
                           (migrate! datasource options)
                           nil
                           (catch FlywayException e
                             e))]
      (when migration-status
        (.printStackTrace migration-status System/out)
        (System/exit 1))))

;(-main)
;(create-datasource! db)
