(defproject stream "0.0.1"
  :description "First step for Clojure enlightenment
                (http://en.wikipedia.org/wiki/Four_stages_of_enlightenment)"
  :url "https://github.com/ananthmv/stream"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 ;; Core platform
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/java.jdbc "0.3.3"]

                 [environ "0.4.0"]

                 ;; Web
                 [http-kit "2.1.18"]
                 [enlive "1.1.5"]

                 ;; Utility
                 [org.clojars.scsibug/feedparser-clj "0.4.0"
                  :exclusions [org.clojure/clojure]]

                 ;; Database
                 [com.googlecode.flyway/flyway-core "2.3.1"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"
                  :exclusions [org.slf4j/slf4j-api]]
                 [org.postgresql/postgresql "9.3-1100-jdbc41"]
                 [yesql "0.4.0"
                  :exclusions [org.clojure/clojure]]

                 ;; Logging
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17"
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jmdk/jmxtools
                               com.sun.jmx/jmxri]]]

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :min-lein-version "2.3.3"
  :main stream.main
  :aot :all
  :warn-on-reflection true
  :test-paths ["test"]
  :jar-name "stream.jar"
  :uberjar-name "stream-standalone.jar"
  :plugins [[lein-environ "0.4.0"]])
