(ns stream.pocket
  ^{:author "Anand Muthu"
    :doc "Lightweight getpocket.com API wrapper"}
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :as ev]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn api-context
  [protocol host version]
  (str protocol "://" host "/" version))

(def ^:dynamic *rest-api* (api-context "https" "getpocket.com" "v3"))

(def ^:dynamic *add-url* (str *rest-api* "/add"))

(def ^:dynamic *oauth-request* (str *rest-api* "/oauth/request"))

(def ^:dynamic *oauth-authorize* (str *rest-api* "/oauth/authorize"))

(def ^:dynamic *oauth-authorize-url* "https://getpocket.com/auth/authorize?request_token=%s&redirect_uri=%s")

(def ^:private creds {:consumer_key (ev/env :pocket-consumer-key)
                      :access_token (ev/env :pocket-access-token)})

(def ^:private options {:timeout 2000
                        :user-agent "clojure-articles app"
                        :headers {"X-Accept" "application/json"
                                  "Content-Type" "application/x-www-form-urlencoded; charset=UTF-8"}})

(defn- post
  [url params options]
  (log/debug "post url" url)
  (log/debug "post params" params)
  (log/debug "post options" options)
  (let [form {:form-params params}
        data (conj form options)
        {:keys [error status body headers]} @(http/post url data)]
    {:error error :headers headers :status status :body body}))

(defn- request-code
  [consumer-key]
  (let [data {:consumer_key consumer-key :redirect_uri "http://google.com"}
        resp (post *oauth-request* data options)]
    (if-let [err (resp :error)]
      {:error err}
      {:code (:code (json/read-str (resp :body) :key-fn keyword))})))

(defn- authorize-url
  [request_token]
  (let [url (format *oauth-authorize-url* request_token (http/url-encode "http://google.com"))]
    url))

(defn- activate-code
  [consumer-key code]
  (let [data {:consumer_key consumer-key :code code}
        resp (post *oauth-authorize* data options)]
    (if-let [error (:error resp)]
      {:error error}
      {:access_token (:access_token (json/read-str (resp :body) :key-fn keyword))})))

(defn- log-and-wait
  [auth-url]
  (println "Open the below link in the browser and provide the authorization")
  (println auth-url)
  (println "Waiting for 2 minutes before retreiving the access-token")
  (println "....")
  (Thread/sleep 120000))

(defn- check-response
  [res key]
  (let [data (res key)]
    (if data
      data
      (throw (new Exception (str "Got non-success response: " (res :error)))))))

(defn create-access
  []
  (let [consumer-key (creds :consumer_key)
        code (check-response (request-code consumer-key) :code)
        auth-url (authorize-url code)
        _ (log-and-wait auth-url)
        access-token (check-response (activate-code consumer-key code) :access_token)]
    {:access_token access-token}))

(defn add-url!
  ([url tags] (add-url! url nil tags))
  ([url title tags]
   (log/debug "add-url url" url)
   (log/debug "add-url title" title)
   (log/debug "add-url tags"tags)
   (let [data {:url url :title title :tags tags}
         form-data (conj data creds)
         resp (post *add-url* form-data options)]
     (if-let [error (:error resp)]
       error
       (:body resp)))))
