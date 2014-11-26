(ns stream.planet-clj.cassandra
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]
            ;[qbits.hayt.fns :as fns] has fns for timebased UUIDs
            [clojurewerkz.cassaforte.uuids :as uid]))

;initialize the Cassandra connection
(def conn (cc/connect ["127.0.0.1"]))

;Change the session to use stream_clj_links keyspace
(cql/use-keyspace conn "stream_clj_links")

(defn init
  []
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
                                         :primary-key [:id]})))

(defn add-articles
  [columns]
  (let [tname "articles"]
    (cql/insert conn tname columns)))

(defn select-links
  []
  (let [tname "articles"]
    (cql/select conn tname)))

(defn synced-links
  []
  (let [links (map #(:link %) (select-links))
        unique-links (reduce conj (set '{}) links)]
    unique-links))
