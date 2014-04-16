(ns stream.main
  (:require [environ.core :as ev]
            [stream.feed :as f]
            [stream.pocket :as p]
            [stream.delicious :as d]
            [stream.utils :as u]
            [clojure.tools.logging :as log])
  (:gen-class))

(def ^:private activate {:pocket (= (ev/env :getpocket) "yes")
                         :delicious (= (ev/env :delicious) "yes")
                         :database (= (ev/env :database) "yes")})

(defn -main
  []
  (log/debug "activate flag" activate)
  (when (or (activate :pocket)
            (activate :delicious)
            (activate :database))
    (let [rss-url (or (ev/env :rss-url) "http://planet.clojure.in/atom.xml")
          _ (log/debug "rss-url" rss-url)
          links (f/article-links rss-url)
          _ (log/debug "Found" (count links) "new links")]

      (if (activate :database)
        (f/save-links! links (u/domain-name rss-url)))
      (if (activate :pocket)
        (dorun
         (map #(p/add-url! (:link %) (:title %) "clojure, planet-clojure") links)))
      (if (activate :delicious)
        (dorun
         (map #(d/bookmark-url! (:link %) (:title %) "clojure, planet-clojure") links)))
      (log/info "completed processing clojure articles"))))
