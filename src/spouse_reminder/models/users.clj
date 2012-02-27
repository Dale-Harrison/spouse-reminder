(ns spouse-reminder.models.users
  (:use somnium.congomongo)
  (:require [noir.session :as sessions]))

(def conn
     (make-connection "spousereminder"
		      :host "127.0.0.1"
		      :port 27017))

(set-connection! conn)

(mongo! :db "reminders")

;; Gets

(defn admin? []
  (sessions/get :admin))

(defn user? []
  (sessions/get :username))

(defn me []
  (sessions/get :username))

;; Operations

(defn add-user [userg]
  (insert! :users {:username (:username userg)
		   :password (:password userg)
		   :email (:email userg)
		   :usertype "Member"}))

(defn get-user [userg]
  (fetch
   :users
   :where {:username userg}))

(defn get-user-type [userg]
  (get (fetch-one
	:users
	:where {:username userg})
	:usertype))

(defn is-member [userg]
  (if (or (= (get-user-type userg) "Member") (= (get-user-type userg) "Admin"))
    true
    false))

(defn get-user-password [userg]
  (get (fetch-one :users
		  :where {:username userg})
       :password))

(defn login! [{:keys [username password] :as user}]
  (if (= password (get-user-password username))
      (do
        (if (is-member username)
	  (sessions/put! :username username)
          (str "Invalid username or password")))))

