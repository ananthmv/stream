(ns stream.resume
  "I'm Anand Muthu, a software enthusiasts. In school (1999), I had started
  programming with BASIC, then learned Visual Basic, quickly moved
  to Java for my day job and now I'm diving into Clojure.

  I have started playing with various flavour of Linux (Fedora/Ubuntu)
  that we would get from the magazines in our college.

  In June ’05 after graduation, I joined a startup, AddVal Technology
  where I had spent most of my time working on a large scale projects
  for Singapore Airlines. My experience is mostly with Java ecosystem,
  Web development and UNIX. Past years, I had worked for service businesses
  (Verizon and Bell Canada) where I've built/worked with Web 2.0,
  CMS, Rule Engines, Parser generators, Databases, Web services, Messaging,
  and most recently, Big Data.

  Now I'm planning to get back into some exciting, cutting edge Software
  development based on Clojure technology stack. My current interests
  includes Startups, functional programming, data driven intelligence,
  thumb driven and responsive applications."

  (:require [clojure.edn :as edn]))

(defrecord Experience [company role fromdate todate months description techstack])

(defonce *title* "Resume circa. 1998")

(defonce name-info { :firstname "Anand"
                     :lastname "Muthu" })

(defn print-resume
  []
  (let [fullname (str (:firstname name-info) (:lastname name-info))
        resume   (edn/read-string (slurp "resume.edn"))
        exp      (map (fn [m](map->Experience m)) (:experience resume))
        skills   (:skills resume)
        awards   (:awards resume)
        interest (:interest resume)
        linkedin (:linked-in resume)
        edu      (:education resume)
        schools  (:schools edu)
        degrees  (:degrees edu)]
    (println "print pdf resume")))

;(print-resume)
