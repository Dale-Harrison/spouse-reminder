(ns spouse-reminder.database
  (:use somnium.congomongo)
  (:use clj-time.core)
  (:use clj-time.format)
  (:use clj-time.coerce)
  (:use clojure.contrib.json)
  (:require [clojure.contrib.str-utils2 :as string]))

(def conn
     (make-connection "spousereminder"
		      :host "127.0.0.1"
		      :port 27017))

(set-connection! conn)

(mongo! :db "reminders")

(def short-formatter (formatter "dd/MM/yyyy HH:mm"))

(defn string-id [map]
  (assoc map :_id (str (map :_id)))) 

(defn add-reminder [user body date location]
  (insert! :reminders {:user user :body body :date date :location location :addedon (to-long (now))}))

(defn add-user [username password email]
  (insert! :users {:username username :password password :email email :usertype "Member"}))

(defn get-spouse [user]
  (map :spouse
   (fetch
    :users
    :where {:username user})))

(defn get-reminders [user]
  (fetch
   :reminders
   :where {:user user}
   :sort {:addedon -1}))

(defn get-reminders-json [user]
  (str "{\"reminders\": " (json-str (map string-id (get-reminders user))) "}"))

(defn get-user [user]
  (fetch
   :users
   :where {:username user}))

(defn get-user-password [user]
  (get (fetch-one :users :where {:username user}) :password))