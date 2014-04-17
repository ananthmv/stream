(ns stream.pdf.school
  (:import [java.io File OutputStreamWriter FileOutputStream BufferedWriter InputStream OutputStream]
           [java.net URL]
           [java.io FileNotFoundException StringWriter])
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.java.io :as io]
            [instaparse.core :as instaparse])
  (:gen-class))

(def to-ignore {#"S.No C.Code School Name LKG UKG I II III IV V VI VII VIII IX X XI XII" ""
                #"Note:- NF - There is no Recognition, Hence No Fee is Fixed." ""
                #"\d[0-9]\s-\s[A-Z]+\s\s(DISTRICT \[ REVISED FEE STRUCTURE FOR APPEALED SCHOOLS \])" ""
                ;#"\n" ""
                })

(defn clean-up!
  [f regex]
  (let [content (slurp f)
        cleaned (reduce #(apply clojure.string/replace %1 %2) content regex)
        _ (spit (str "c-" f) cleaned)]))

;(clean-up! "02_THIRUNELVELI.txt" to-ignore)

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

(defn- str-non-nil
  "Exactly like `clojure.core/str`, except it returns an empty string
  with no args (whereas `str` would return `nil`)."
  [& args]
  (apply str "" args))

(def ^:private parser (instaparse/parser (io/resource "stream/pdf/schoolinfo.bnf")))

(def parser-transforms
  {:c-num (fn [& args]
           [:c-num (apply str-non-nil args)])
   :name (fn [& args]
           [:name (apply str-non-nil args)])
   :word (fn [& args]
           [:word (apply str-non-nil args)])
   :fees (fn [& args]
           [:fees (apply str-non-nil args)])
   :postal (fn [& args]
           [:postal (apply str-non-nil args)])
   :hypen (fn [& args]
           [:hypen (apply str-non-nil args)])
   :schoolinfo list})

(defn process-instaparse-result
  [parsed]
  (cond
   (instaparse/failure? parsed) (let [failure (instaparse/get-failure parsed)]
                                  (binding [*out* (StringWriter.)]
                                    (instaparse.failure/pprint-failure failure)
                                    (throw (ex-info (.toString *out*)
                                                    failure))))
   :else (first parsed)))

(defn parse-schoolinfo
  "Parses a string with stream schoolinfo syntax into a sequence"
  [text]
  (process-instaparse-result
   (instaparse/transform parser-transforms
                         (instaparse/parses parser
                                            (str text "\n");;; TODO This is a workaround for files with no end-of-line marker.
                                            ;:start :schoolinfo
                                            ))))

;(clean-up! "02_THIRUNELVELI.txt" to-ignore)
;(parse-schoolinfo (slurp "c-02_THIRUNELVELI.txt"))
;(clojure.pprint/pprint (parse-schoolinfo (slurp "c-02_THIRUNELVELI.txt")))
