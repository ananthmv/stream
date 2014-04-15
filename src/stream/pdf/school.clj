(ns stream.pdf.school
  (:import [java.io File OutputStreamWriter FileOutputStream BufferedWriter InputStream OutputStream]
           [java.net URL])
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.java.io :as io])
  (:gen-class))

(def to-ignore {#"S.No C.Code School Name LKG UKG I II III IV V VI VII VIII IX X XI XII" ""
                #"Note:- NF - There is no Recognition, Hence No Fee is Fixed." ""
                #"DISTRICT [ REVISED FEE STRUCTURE FOR APPEALED SCHOOLS ]" ""
                ;#"\r" "#"
                #"\n" ""
                })

(defn clean-up!
  [f]
  (let [content (slurp f)
        cleaned (reduce #(apply clojure.string/replace %1 %2) content to-ignore)
        _ (spit (str "c-" f) cleaned)]))

(defn get-page
  [url]
  (-> url URL. html/html-resource))

(defn fetch-pdf-names
  []
  (let [url "http://www.tn.gov.in/schooleducation/private_fee.htm"
        content (get-page url)
        hrefs (html/select content [:table :tr :td :p :strong :a])
        links (map (fn [href-el]
                     (let [href (->> href-el :attrs :href)
                           url (str "http://www.tn.gov.in" href)]
                       {:file (nth (clojure.string/split href #"/") 3);/schooleducation/pdf/32_ARIYALUR.pdf"
                        :url url})) hrefs)]
    links))

(def ^:dynamic *download-buffer-size*
  "A buffer size (byte) used in a downloading process."
  4096)

(defn- save-to-disk!
  "Downloads from the InputStream to the OutputStream. To print progress, it
   requires the content length."
  [^InputStream is ^OutputStream os content-len]
  (let [data (byte-array *download-buffer-size*)]
    (loop [len (.read is data)
           sum len]
      (when-not (= len -1)
        (.write os data 0 len)
        (let [len (.read is data)]
          (recur len (+ sum len)))))))

(defn http-download!
  "Downloads from the url via HTTP/HTTPS and saves it to local as f."
  [url f & {:keys [auth]}]
  (let [{:keys [error status body headers]} @(http/get url)
        content-len (if-let [content-len (headers :content-length)]
                      (Integer. ^String content-len) -1)]
    (with-open [os (io/output-stream f)]
      (save-to-disk! body os content-len))))

(defn download-school-fee-data
  []
  (let [pdfs (fetch-pdf-names)
        _ (dorun ;dorun is required to run fn with lazy-seq input
           (map (fn[fl] (http-download! (:url fl) (:file fl))) pdfs))]
    pdfs))
