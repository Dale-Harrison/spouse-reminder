(ns spouse-reminder.views.main
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [noir.response :as resp]))

(defpartial layout [content]
   (html5
    [:head
     (include-css "/css/main.css")
     (include-js "/js/jquery.form.js")]
    [:body {:id "top"}
     [:div {:class "wrapper col1"}
      [:div {:id "header"}
       [:div {:id "logo"}
	[:h1 [:a {:href "#"} "Spouse Reminder"]]
	[:p "Nagging in the future"]]
       [:br {:class "clear"}]]]
     content
     [:div {:class "wrapper col4"}
      [:div {:id "footer"} "This is the bottom"]]
     [:div {:class "wrapper col5"}
      [:div {:id "copyright"}
       (str "You are logged in as tofix")]]]))
         