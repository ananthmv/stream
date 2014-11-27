(defproject stream "0.0.2"
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
                 [org.clojure/data.csv "0.1.2"]

                 [environ "1.0.0"]

                 ;; Web
                 [http-kit "2.1.18"]
                 [enlive "1.1.5"]

                 ;; Utility
                 [org.clojars.scsibug/feedparser-clj "0.4.0"
                  :exclusions [org.clojure/clojure]]
                 [org.apache.pdfbox/pdfbox "1.8.4"]
                 [instaparse "1.3.4"]

                 ;; Database
                 [com.googlecode.flyway/flyway-core "2.3.1"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"
                  :exclusions [org.slf4j/slf4j-api]]
                 [org.postgresql/postgresql "9.3-1100-jdbc41"]
                 [yesql "0.4.0"
                  :exclusions [org.clojure/clojure]]

                 ;;Distributed Data Store
                 [clojurewerkz/cassaforte "2.0.0-rc1"] ; Cassandra

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
  :aliases {"cc" ["do" "clean," "uberjar"]} ;commit check
  :jvm-opts ["-Dclojure.compiler.disable-locals-clearing=true"
             "-Djava.net.preferIPv4Stack=true"
             "-Dsun.net.inetaddr.ttl=0"
             "-XX:+TieredCompilation"
             "-Xms1G" ;256m
             "-Xmx1G"
             "-server"]
  :jar-name "stream.jar"
  :uberjar-name "stream-standalone.jar"
  :plugins [[lein-environ "1.0.0"]])
