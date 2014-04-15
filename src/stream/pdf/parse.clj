(ns stream.pdf.parse
  (:import [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.util PDFTextStripper]
           [java.io File OutputStreamWriter FileOutputStream BufferedWriter]
           [java.net URL])
  (:require [net.cgrand.enlive-html :as html])
  (:gen-class))

(defn pdf-text
  [src wr]
  (with-open [pd (PDDocument/load src)]
    (let [stripper (PDFTextStripper.)
          content (.getText stripper pd)]
      (.writeText stripper pd wr))))

(defn parse-pdf
  ([src] (with-out-str
           (pdf-text src *out*)))
  ([src writer]
   (pdf-text src writer)))

(defn extract-pdf
  [file]
  (let [dest (str (first (clojure.string/split file #"\.")) ".txt")
        wr (BufferedWriter. (OutputStreamWriter. (FileOutputStream. (File. dest))))]
    (parse-pdf file wr)
    (.close wr)))
