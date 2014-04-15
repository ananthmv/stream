(ns stream.delicious
  ^{:author "Anand Muthu"
    :doc "Lightweight delicious API wrapper"}
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :as ev]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn api-context
  [protocol host version]
  (str protocol "://" host "/" version))

(def ^:const rest-api (api-context "https" "delicious.com" "v1"))
(def ^:const add-url "https://api.delicious.com/v1/posts/add?url=%s&description=%s&tags=%s")
(def ^:const oauth-authorize "https://avosapi.delicious.com/api/v1/oauth/token?client_id=%s&client_secret=%s&grant_type=credentials&username=%s&password=%s")

(def ^:private creds {:client-id (ev/env :delicious_client_id)
                      :client-secret (ev/env :delicious_client_secret)
                      :username (ev/env :delicious_username)
                      :password (ev/env :delicious_password)})

(def ^:private options {:user-agent "clojure-articles app"
                        :headers {"X-Accept" "application/json"
                                  "Content-Type" "application/x-www-form-urlencoded; charset=UTF-8"}})

(defn- post
  ([url options] (post url "" options))
  ([url params options]
   (log/debug "post params" params)
   (log/debug "post options" options)
   (let [form {:form-params params}
         data (conj form options)
         {:keys [error status body headers]} @(http/post url data)]
     {:error error :headers headers :status status :body body})))

(defn- access-token
  [cred]
  (let [url (format oauth-authorize
                    (cred :client-id)
                    (cred :client-secret)
                    (cred :username)
                    (cred :password))
        resp (post url "" options)]
    (if-let [error (:error resp)]
      {:error error}
      {:access_token (:access_token (json/read-str (resp :body) :key-fn keyword))})))

(defn bookmark-url!
  ([url tags] (bookmark-url! url url tags))
  ([url description tags]
   (log/debug "add-url url" url)
   (log/debug "add-url description" description)
   (log/debug "add-url tags" tags)
   (let [token (:access_token (access-token creds))
         url (format add-url (http/url-encode url) (http/url-encode description) (http/url-encode tags))
         secure-header (update-in options [:headers] conj {"Authorization" (str "Bearer " token)})
         resp (post url "" secure-header)]
     (if-let [error (:error resp)]
       error
       (:body resp)))))
