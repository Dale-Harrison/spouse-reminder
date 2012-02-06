(ns spouse-reminder.database
  (:use somnium.congomongo)
  (:use clj-time.core)
  (:use clojure.contrib.json)
  (:require [clojure.contrib.str-utils2 :as string]))

(def conn
     (make-connection "spousereminder"
		      :host "127.0.0.1"
		      :port 27017))

(set-connection! conn)

(mongo! :db "reminders")

(defn string-id [map]
  (assoc map :_id (str (map :_id)))) 

(defn add-reminder [title user date body]
  (insert! :reminders {:title title :user user :date date :body body :addedon (str (now))}))

(defn add-user [username password email spouse]
  (insert! :users {:username username :password password :email email :spouse spouse}))

(defn get-spouse [user]
  (map :spouse
   (fetch
    :users
    :where {:username user})))

(defn get-reminders [user]
  (fetch
   :reminders
   :where {:user user}))

(defn get-reminders-json [user]
  (str "{\"reminders\": " (json-str (map string-id (get-reminders user))) "}"))

(defn get-user [user]
  (fetch
   :users
   :where {:username user}))

(defn get-user-password [user]
  (get (fetch-one :users :where {:username user}) :password))