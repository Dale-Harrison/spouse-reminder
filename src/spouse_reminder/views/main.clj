(ns spouse-reminder.views.main
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [noir.response :as resp]
	    [spouse-reminder.models.users :as userget]))

(defpartial layout [content]
   (html5
    [:head
     (include-css "/css/main.css")
     (include-js "http://code.jquery.com/jquery-1.7.1.js")
     (include-js "http://malsup.github.com/jquery.form.js")
     (include-js "/javascript/javascript.js")]
    [:body {:id "top"}
     [:div {:id "preloader"}
      [:img {:src "/images/floral.jpg" :width "1" :height "1"}]]
     [:div {:class "wrapper col1"}
      [:div {:id "header"}
       [:div {:id "logo"}
	[:h1 [:a {:href "/"} "Spouse Reminder"]]
	[:p "Nagging in the future"]]
       [:div {:id "topnav"}
	[:ul
	 [:li {:class "active"} [:a {:href "reminders"} "Reminders"]]
	 [:li [:a {:href "account"} "Account Details"]]
	 [:li [:a {:href "logout"} "Logout"]]]]]]
     content]))