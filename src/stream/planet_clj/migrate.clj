(ns stream.planet-clj.migrate
    ^{:author "Anand Muthu"
    :doc "A quick dirty hack to migrate data from PostgreSQL to Cassandra"}
  (:require [stream.planet-clj.db :as db]
            [stream.planet-clj.cassandra :as cas]
            [clojurewerkz.cassaforte.uuids :as uid]))

(defn migrate!
  []
  (let [links (db/select-links)]
    (dorun
     (map #(cas/add-articles (assoc % :added_on (.getTime (java.util.Date.)) :id (uid/random))) links))))
