(ns spouse-reminder.remservice
  (:refer-clojure :exclude (list get delete))
  (:require [spouse-reminder.database :as dbs]
	    [compojure.route :as route]
	    [clj-json.core :as json]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn service-auth-request [username password]
  (if (= (dbs/get-user-password username) password)
    true
    false))

(defn get-service-reminders [params]
  (if(= (service-auth-request (:username params) (:password params)))
    (json-response (dbs/get-reminders username))
    (json-response ("Invalid Username") "403")))