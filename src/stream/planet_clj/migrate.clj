(ns stream.planet-clj.migrate
  ^{:author "Anand Muthu"
    :doc "A quick dirty hack to migrate data from PostgreSQL to Cassandra"}
  (:require [stream.planet-clj.db :as db]
            [stream.planet-clj.cassandra :as cas]
            [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]
            [clojurewerkz.cassaforte.uuids :as uid]))


(defn init
  []
  (let [conn (cas/get-connection ["127.0.0.1"] "stream_clj_links")]
    (try
      (cql/drop-keyspace conn "stream_clj_links")
      (catch Exception _ nil))
    (cql/create-keyspace conn "stream_clj_links"
                         (with {:replication
                                {:class "SimpleStrategy"
                                 :replication_factor 1}}))
    (cql/use-keyspace conn "stream_clj_links")
    (cql/create-table conn :articles
                      (column-definitions {:id :uuid
                                           :link :varchar
                                           :title :text
                                           :source :varchar
                                           :domain :varchar
                                           :added_on :timestamp
                                           :primary-key [:id]}))))


(defn migrate!
  []
  (let [links (db/select-links)]
    (dorun
     (map #(cas/add-articles (assoc % :added_on (.getTime (java.util.Date.)) :id (uid/random))) links))))
