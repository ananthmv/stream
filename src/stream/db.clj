(ns stream.db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [stream.config :as config]
            [yesql.core :refer :all])
  (:import com.jolbox.bonecp.BoneCPDataSource)
  (:gen-class))


(defn pooled-datasource
  [db-spec]
  (let [{:keys [classname subprotocol subname user password
                init-pool-size max-pool-size idle-time partitions]} db-spec
        cpds (doto (BoneCPDataSource.)
               (.setDriverClass classname)
               (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
               (.setUsername user)
               (.setPassword password)
               (.setMinConnectionsPerPartition (inc (int (/ init-pool-size partitions))))
               (.setMaxConnectionsPerPartition (inc (int (/ max-pool-size partitions))))
               (.setPartitionCount partitions)
               (.setStatisticsEnabled true)
               (.setIdleMaxAgeInMinutes (or idle-time 60)))]
    {:datasource cpds :db-spec db-spec}))
;sending pooled-db-spec with :datasource will be used by clojure.java.jdbc
;with pooling, we get 10X performance

(def pooled-db-spec (pooled-datasource config/db))

(defqueries "db/sql/articles.sql")

(defn add-articles
  [link title source domain]
  (insert-articles<! pooled-db-spec link title source domain))

(defn select-links
  []
  (find-all-links pooled-db-spec))

(defn synced-links
  []
  (let [links (map #(:link %) (select-links))
        unique-links (reduce conj (set '{}) links)]
    unique-links))
