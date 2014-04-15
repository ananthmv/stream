# Stream
#####First step for Clojure enlightenment

Clojure has many inspiring things to learn and use them in real time project. Many Clojure developers
has been writing various lightweight libraries, utilities and  wrapper projects using Clojure. The ecosystem
is growing every week and many articles are being written and shared in Twitter, FB and in weekly digest
mailing list. Notably, Planet Clojure (created by BG and Alex) has aggregated a great list of clojure articles with various flavors.

Stream is containing the experiments and utilities built to learn Clojure in [Dhammapada](http://en.wikipedia.org/wiki/Dhammapada) way.

## 1. Articles
Any given day, I used to log into clojure.in to read articles and often pocket them to read it later
during commute. Unfortunately, the site has only finite set of "latest" articles those are 5 days or less old (based on the number of articles being written). The older articles will be lost when reader haven't logged in for a long time.

This mini-project is aims to store those invaluable article's links into getpocket.com and delicious.com respectively. Note that this implementation uses various lib to learn and experiment with them. Production
grade performance/usage is not guaranteed.

##### credentials

We need to add the following credentials into `./lein/profiles.clj` or env variable before running the deamon.

```clj
  {:user {:env
        :getpocket "no"
        :pocket-consumer-key "consumer-key"
        :pocket-access-token "access-token"
        :delicious "yes"
        :delicious-client-id "client-id"
        :delicious-client-secret "client-secret"
        :delicious-username "unique-username"
        :delicious-password "secret-password"
        :database "no"
        :database-uri "jdbc:postgresql://localhost:5432/dbname"
        :db-username "username"
        :db-password "secretpass"
        :rss-url}}
```

Currently, postgresql db is supported to store the links and related information.

##### delicious link

Currently, the deamon is running in my personal server and links are being added into an [delicious account](https://delicious.com/ananthmv/planet-clojure)

## 2. TamilNadu School Fee Date
Writing scripts to scrap links, download those PDF, read them and parse the content into text file. Load the content into database and visualize using incanter.

In-Progress

## License

Copyright © 2014 Anand Muthu

Distributed under the Eclipse Public License either version 1.0
