(ns stream.planet-clj.cassandra
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]
            ;[qbits.hayt.fns :as fns] has fns for timebased UUIDs
            [clojurewerkz.cassaforte.uuids :as uid]))

(defn get-connection
  [hosts keyspace]
  "Initialize the Cassandra connection"
  (when-let [conn (cc/connect hosts)]
    (cql/use-keyspace conn keyspace)
    conn))

(defn add-articles
  [columns]
  (let [conn (get-connection ["127.0.0.1"] "stream_clj_links")
        tname "articles"]
    (cql/insert conn tname columns)))

(defn select-links
  []
  (let [conn (get-connection ["127.0.0.1"] "stream_clj_links")
        tname "articles"]
    (cql/select conn tname)))

(defn synced-links
  []
  (let [links (map #(:link %) (select-links))
        unique-links (reduce conj (set '{}) links)]
    unique-links))
