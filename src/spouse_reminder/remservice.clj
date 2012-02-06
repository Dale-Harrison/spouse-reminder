(ns spouse-reminder.remservice
  (:refer-clojure :exclude (list get delete))
  (:require [spouse-reminder.database :as dbs]
	    [compojure.route :as route]
	    [clojure.contrib.json :as json]))

(defn service-auth-request [username password]
  (if (= (dbs/get-user-password username) password)
    true
    false))

(defn get-service-reminders [params]
  (if (service-auth-request (:username params)(:password params))
    (dbs/get-reminders-json (:username params))
    (str "Authentication Error")))

(defn hello [params]
  (if (service-auth-request (:username params)(:password params))
    (str "{\"helloresponse\":[ " (json/json-str {:hello "Hello"}) "]}")
    (str "Authentication Error")))
