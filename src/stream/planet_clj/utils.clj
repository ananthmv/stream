(ns stream.planet-clj.utils
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [stream.planet-clj.db :as db])
  (:import [java.net URL]
           [java.io IOException]
           [java.util.UUID])
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

(defn read-file-lazy
  "Taken from http://stackoverflow.com/a/13312151"
  [file]
  (letfn [(helper [rdr]
                  (lazy-seq
                   (if-let [line (.readLine rdr)]
                     (cons line (helper rdr))
                     (do (.close rdr) nil))))]
         (helper (io/reader file))))

(defn lazy-read-csv
  "Lazily reads a file and generates the CSV data.
   Memory leaks if the sequence is not fully consumed.
   https://gist.github.com/fbmnds/5921134#file-lazy-read-csv-clj
   http://fbmnds.blogspot.ca/2013/07/remarks-on-processing-large-files.html"
  [csv-file]
  (let [in-file (io/reader csv-file) ;; FB: io/reader
        csv-seq (csv/read-csv in-file)
        lazy (fn lazy [wrapped]
               (lazy-seq
                (if-let [s (seq wrapped)]
                  (cons (first s) (lazy (rest s)))
                  (.close in-file))))] ;; FB: .close
    (lazy csv-seq)))

(defn random-guid
  []
  (java.util.UUID/randomUUID))
