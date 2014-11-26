(ns stream.planet-clj-links
  (:require [environ.core :as ev]
            [stream.planet-clj.feed :as f]
            [stream.planet-clj.pocket :as p]
            [stream.planet-clj.delicious :as d]
            [stream.planet-clj.utils :as u]
            [clojure.tools.logging :as log]))

(def ^:private activate {:pocket (= (ev/env :getpocket) "yes")
                         :delicious (= (ev/env :delicious) "yes")
                         :database (= (ev/env :database) "yes")})

(defn fetch-articles
  []
  (log/debug "activate flag" activate)
  (when (or (activate :pocket)
            (activate :delicious)
            (activate :database))
    (let [rss-url (or (ev/env :rss-url) "http://planet.clojure.in/atom.xml")
          _ (log/debug "rss-url" rss-url)
          links (f/article-links rss-url)
          _ (log/debug "Found" (count links) "new links")
          domain (u/domain-name rss-url)]
      (if (activate :database)
        (do
          (try
            (f/save-links-to-cassandra! links domain)
            (catch Exception e
              (log/error (str "Error in save-links-to-cassandra! " (.getMessage e)))))
          (f/save-links-to-db! links domain)))
      (if (activate :pocket)
        (dorun
         (map #(p/add-url! (:link %) (:title %) "clojure, planet-clojure") links)))
      (if (activate :delicious)
        (dorun
         (map #(d/bookmark-url! (:link %) (:title %) "clojure, planet-clojure") links)))
      (log/info "fetch-articles completed"))))
