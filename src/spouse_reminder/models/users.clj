(ns spouse-reminder.models.users
  (:use somnium.congomongo)
  (:require [noir.session :as sessions])
  (:use [somnium.congomongo.config :only [*mongo-config*]]))

(defn split-mongo-url [url]
  "Parses mongodb url from heroku, eg. mongodb://user:pass@localhost:1234/db"
  (let [matcher (re-matcher #"^.*://(.*?):(.*?)@(.*?):(\d+)/(.*)$" url)] ;; Setup the regex.
    (when (.find matcher) ;; Check if it matches.
      (zipmap [:match :user :pass :host :port :db] (re-groups matcher))))) ;; Construct an options map.

(defn maybe-init []
  "Checks if connection and collection exist, otherwise initialize."
  (when (not (connection? *mongo-config*)) ;; If global connection doesn't exist yet.
    (let [mongo-url (get (System/getenv) "MONGOHQ_URL") ;; Heroku puts it here.
	  config    (split-mongo-url mongo-url)] ;; Extract options.
      (println "Initializing mongo @ " mongo-url)
      (mongo! :db (:db config) :host (:host config) :port (Integer. (:port config))) ;; Setup global mongo.
      (authenticate (:user config) (:pass config)) ;; Setup u/p.
      (or (collection-exists? :reminders) ;; Create collection named 'firstcollection' if it doesn't exist.
	  (create-collection! :reminders)))))

;; Gets

(defn admin? []
  (sessions/get :admin))

(defn user? []
  (sessions/get :username))

(defn me []
  (sessions/get :username))


;; Operations

(defn get-user [userg]
  (fetch-one
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

(defn logout! []
  (sessions/clear!))

(defn user-exists? [username]
  (if (= (get-user username) nil)
    false
    true))

(defn add-user [userg]
    (insert! :users {:username (:username userg)
		     :password (:password userg)
		     :email (:email userg)
		     :usertype "Member"}))

(defn process-user-addition [userg]
  (if (user-exists? (:username userg))
    false
    (do
      (add-user [userg])
      (sessions/put! (:username userg))
      true)))
      