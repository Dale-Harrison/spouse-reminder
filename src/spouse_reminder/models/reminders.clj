(ns spouse-reminder.models.reminders
  (:use somnium.congomongo)
  (:use clj-time.core)
  (:use clj-time.format)
  (:use clj-time.coerce)
  (:use clojure.contrib.json)
  (:refer-clojure :exclude [extend])
  (:require [clojure.contrib.str-utils2 :as string]
	    [spouse-reminder.models.users :as use]
	    [spouse-reminder.models.heroku-mongo :as db]))


(def short-formatter (formatter "dd/MM/yyyy HH:mm"))
(def readable-formatter (formatter "dd-MMM-yyyy HH:mm"))

(defn first-match [m]
  (if (coll? m) (first m) m))

(defn get-body [entry]
  (first-match (re-find #"(.*)(?=(.\d{1,2})/(\d{1,2})/(\d{4}|\d{2})\s(\d{1,2}):(\d{1,2}))" entry)))

(defn get-date [entry]
  (first-match (re-find #"(\d{1,2})/(\d{1,2})/(\d{4}|\d{2})\s(\d{1,2}):(\d{1,2})" entry)))

(defn get-location [entry]
  (if (= (re-find #"@.*" entry) nil)
    ""
    (re-find #"@.*" entry)))

(defn get-reminders [userget]
  (db/fetch
   :reminders
   :where {:user userget}
   :limit 5
   :sort {:addedon}))

(defn format-reminder [reminder]
   [:div {:class "reminder"} [:b (unparse readable-formatter (parse short-formatter (:date reminder)))] [:br] (:body reminder)])

(defn get-all-reminders [user]
  [:div (map format-reminder (get-reminders user))])

(defn get-reminders-after-last-update [userget longtime]
  (db/fetch
   :reminders
   :where {:user userget
	   :addedon {:$gt longtime}}))

(defn get-all-reminders-after-last-update [user longtime]
  [:div (map format-reminder (get-reminders-after-last-update user longtime))])

(defn add-reminder [reminder]
  (db/insert! :reminders {:user (use/me)
		       :body (get-body (:body reminder))
		       :date (get-date (:body reminder))
		       :location (get-location (:body reminder))
		       :addedon (to-long (now))}))

(defn string-id [map]
  (assoc map :_id (str (map :_id)))) 

(defn get-reminders-json [userget]
  (str "{\"reminders\": " (json-str (map string-id (get-reminders userget))) "}"))

(defn get-reminders-after-last-update-json [userget longtime]
  (str "{\"reminders\": " (json-str (map string-id (get-reminders-after-last-update userget (Long/parseLong longtime)))) "}"))
	    