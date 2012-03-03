(ns spouse-reminder.views.remservice
  (:refer-clojure :exclude (list get delete))
  (:use noir.core
        hiccup.core
        hiccup.page-helpers)
  (:require [spouse-reminder.models.users :as user]
	    [spouse-reminder.models.reminders :as rems]
	    [spouse-reminder.views.main :as main]
            [noir.response :as resp]
	    [clojure.contrib.json :as json]))

(defn service-auth-request [username password]
  (if (= (user/get-user-password username) password)
    true
    false))

(defpage "/remservice/reminders" {:keys [username password lastupdate]}
  (if (service-auth-request username password)
    (rems/get-reminders-after-last-update-json username lastupdate)
    (str "Authentication Error")))

(defpage "/remservice/hello" {:keys [username password]}
  (if (service-auth-request username password)
    (str "{\"helloresponse\":[ " (json/json-str {:hello "Hello"}) "]}")
    (str "Authentication Error")))


