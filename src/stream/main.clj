(ns stream.main
  (:require [stream.planet-clj-links :as clj]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn -main
  []
  (log/info "STARTED processing clojure articles")
  (clj/fetch-articles)
  (log/info "COMPLETED processing clojure articles")
  (System/exit 0))
