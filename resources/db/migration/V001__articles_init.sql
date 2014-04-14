--create file name with 'yyyyMMddHHmmssSSS' format
--20140415121212121___articles_init.sql
-- create a lein task similar to Groovy task https://gist.github.com/jeremyjarrell/6083207

CREATE TABLE clojure_in_articles (
  id SERIAL NOT NULL,
  link VARCHAR(300) NOT NULL,
  title TEXT NOT NULL,
  source VARCHAR(50),
  domain VARCHAR(50),
  added_on timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT primary_key_id PRIMARY KEY (id));

-- ALTER TABLE clojure_in_articles OWNER TO articles;

CREATE UNIQUE INDEX clojure_in_articles_id_idx ON clojure_in_articles (id);

CREATE INDEX clojure_in_articles_link_idx ON clojure_in_articles
  USING hash (link);

CREATE INDEX clojure_in_articles_link_source ON clojure_in_articles (link, source);
