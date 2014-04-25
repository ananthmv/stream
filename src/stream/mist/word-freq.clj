(ns stream.mist.word-freq
  (:require [clojure.java.io :as io]))

(defn word-freq
  "Snippet taken from http://stackoverflow.com/a/3206954
   Reference for thread macro, doall, mapcat, reduce"
  [f]
  (take 22 (->>
             ; reads the file and forms a list.
             ; doall will perform the operation to create the list from the lazy-seq (line-seq)
             (with-open [rdr (io/reader f)] (doall (line-seq rdr)))
             ; mapcat concat the maps of each line splited as 1gram.
             (mapcat (fn [l] (map #(.toLowerCase %) (re-seq #"\w+" l))))
             (remove #{"the" "and" "of" "to" "a" "i" "it" "in" "or" "is"})
             (reduce #(assoc %1 %2 (inc (%1 %2 0))) {})
             (sort-by val >)))) ; alternative to (sort-by (comp - val)))))

(defn draw-chart
  "Draws the chart based on the highest frequency
   http://stackoverflow.com/a/3206954 "
  [fs]
  (let [[[w f] & _] fs]
    (apply str
           (interpose \newline
                      (map (fn [[k v]]
                             (apply str (concat "|" (repeat (int (* (- 76 (count w)) (/ v f 1))) "_") "| " k " ")))
                           fs)))))
; (println (stream.mist.word-freq/draw-chart (stream.mist.word-freq/word-freq "C:\\projects\\alice.txt")))

(comment
  ;http://stackoverflow.com/a/3194582
  (let[[[_ m]:as s](->> (slurp *in*)
                        .toLowerCase
                        (re-seq #"\w+\b(?<!\bthe|and|of|to|a|i[tns]?|or)")
                        frequencies
                        (sort-by val >)
                        (take 22))
       [b] (sort (map #(/ (- 76 (count (key %)))(val %)) s))
       p #(do
            (print %1)
            (dotimes[_(* b %2)] (print \_))
            (apply println %&))]
    (p " " m)
    (doseq[[k v] s] (p \| v \| k))))
