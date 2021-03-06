(ns stream.planet-clj.feed
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [stream.planet-clj.cassandra :as cas]
            [stream.planet-clj.db :as db]
            [stream.planet-clj.utils :as util]
            [clojurewerkz.cassaforte.uuids :as uid])
  (:use [feedparser-clj.core]
        [clojure.set :as set])
  (:gen-class))

(def ^:private log-file "links.log")

(defn synced-links
  "Reads and creates a data set for the previous logged links"
  [file]
  (if (.exists (clojure.java.io/as-file file))
    (with-open [rdr (clojure.java.io/reader file)]
      (reduce conj (set '{}) (line-seq rdr)))
    (set '{})))

#_(defn persist-links
  "Stores the links into the log-file"
  [entries file]
  (spit file (apply str (map #(str % "\n") entries)) :append false))

(defn save-links-to-cassandra!
  [entries source]
  (dorun
   (map #(cas/add-articles {:id (uid/random)
                            :link (:link %)
                            :title (:title %)
                            :source source
                            :domain (util/domain-name (:link %))
                            :added_on (.getTime (java.util.Date.))}) entries)))

(defn save-links-to-db!
  [entries source]
  (dorun
   (map #(db/add-articles (:link %) (:title %) source (util/domain-name (:link %))) entries)))

(defn filter-old
  [ol nl]
  (doall
   (filter (fn [l]
            (some #(= (:link l) %) ol)) nl)))

(defn filter-empty
  [nl]
  (doall
   (filter #(not (= (:link %) "")) nl)))

(defn extract-data
  [entries]
  (map #(let [title (:title %)
              link (:link %)]
          {:link link :title title}) entries))

(defn article-links
  [rss-url]
  (let [feed (parse-feed rss-url)
        data (extract-data (:entries feed))
        _ (log/debug "extracted data" data)
        links (set (map #(:link %) data))
        old-links (db/synced-links)
        latest-links (set/difference links old-links)
        _ (log/debug "latest links" latest-links)
        new-links (filter-old latest-links data)
        nl (filter-empty new-links)
        ]
    (log/debug "completed collecting articles links")
    nl))
