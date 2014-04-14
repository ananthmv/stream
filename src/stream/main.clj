(ns stream.main
  (:require [environ.core :as ev]
            [stream.feed :as f]
            [stream.pocket :as p]
            [stream.delicious :as d]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn -main
  []
  (let [rss-url (or (ev/env :rss-urls) "http://planet.clojure.in/atom.xml")
        _ (log/debug "rss-url" rss-url)
        links (f/article-links rss-url)
        _ (log/debug "Found" (count links) "new links")
        pocket (map #(p/add-url! (:link %) (:title %) "clojure, planet-clojure") links)
        _ (log/debug "pocketing links" pocket)
        bookmark (map #(d/bookmark-url! (:link %) (:title %) "clojure, planet-clojure") links)
        _ (log/debug "bookmarking links" bookmark)
        ]
    (log/info "completed processing clojure articles")))
