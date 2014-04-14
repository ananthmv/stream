-- name: insert-articles<!
INSERT INTO clojure_in_articles (link, title, source, domain) VALUES (:link,:title,:source,:domain)

--name: find-all-links
SELECT link from clojure_in_articles
