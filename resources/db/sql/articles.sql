-- name: insert-articles<!
INSERT INTO clojure_in_articles (link, title, source, domain) VALUES (:link,:title,:source,:domain)

--name: find-all-links
SELECT id, link, title, source, domain, added_on FROM clojure_in_articles;
