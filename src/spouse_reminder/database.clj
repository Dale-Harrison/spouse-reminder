(ns spouse-reminder.database
  (:use somnium.congomongo)
  (:use clj-time.core))

(def conn
     (make-connection "spousereminder"
		      :host "127.0.0.1"
		      :port 27017))

(set-connection! conn)

(mongo! :db "reminders")

(defn add-reminder [title user date body]
  (insert! :reminders {:title title :user user :date (str (now)) :body body}))

(defn add-user [username password email spouse]
  (insert! :users {:username username :password password :email email :spouse spouse}))

(defn get-spouse [user]
  (map :spouse
   (fetch
    :users
    :where {:username user})))

(defmacro json-fetch [& args]
  `(str "[" (apply str (interpose "," (fetch ~@args :as :json))) "]"))

(defn get-reminders [user]
  (fetch
   :reminders
   :where {:user user}))

(defn get-reminders-json [user]
  (json-fetch :reminders :where {:user user}))

(defn get-user [user]
  (fetch
   :users
   :where {:username user}))

(defn get-user-password [user]
  (get (fetch-one :users :where {:username user}) :password))