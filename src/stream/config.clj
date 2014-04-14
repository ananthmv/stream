(ns stream.config
  (:gen-class))

(def db {:classname   "org.postgresql.Driver"
         :subprotocol "postgresql"
         :uri         "jdbc:postgresql://localhost:5432/articles"
         :subname     "//localhost:5432/articles"
         :user        "articles"
         :password    "articles"
         :init-pool-size 4
         :max-pool-size 20
         :partitions 2
         :idle-time 60})
