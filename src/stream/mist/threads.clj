(ns stream.mist.threads
  "Threading in Clojure"
  (:import (java.util.concurrent Callable Future ExecutorService Executors)))

; Blocking Thread with Callable methods
(def ^ExecutorService e (Executors/newSingleThreadExecutor))
(def ^Future f (.submit e (cast Callable #(reduce + (range 10)))))
(.get f)

e
(.isTerminated e)


;clojure.lang.PersistentArrayMap will be created for map with less than 8 pairs
;(class {:a 10})

;clojure.lang.PersistentHashMap will be created for map with more than 8 pairs
;(class {:a 10 :b 10 :c 10 :d 10 :ag 10 :at 10 :ax 10 :aa 10 :u 10 :y 10 :r 10 :e 10})
