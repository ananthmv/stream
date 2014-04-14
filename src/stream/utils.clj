(ns stream.utils
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [net.cgrand.enlive-html :as html]
            [stream.db :as db])
  (:import [java.net URL]
           [java.io IOException])
  (:gen-class))

(defn fetch-url
  [purl]
  (-> purl URL. html/html-resource))

(defn title
  [url]
  (try
    (let [content (fetch-url url)
          title (html/select content [:title])]
      (first (:content (first title))))
    (catch IOException e
      (log/error e "fetch error")
      url)))

(defn synced-links
  "Reads and creates a data set for the previous logged links"
  [file]
  (if (.exists (clojure.java.io/as-file file))
    (with-open [rdr (clojure.java.io/reader file)]
      (reduce conj (set '{}) (line-seq rdr)))
    (set '{})))

(defn domain-name
  [url]
  (nth (clojure.string/split url #"/") 2))

(defn sync-old-links
  []
  (let [links (synced-links "links.log")]
    (map #(db/add-articles % (clojure.string/trim (title %)) "clojure.in" (domain-name %)) links)))
