(ns spouse-reminder.views.index
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.views.main :as main]
            [noir.response :as resp]))

(defpartial website-details []
  [:h2 "Nag anywhere, any time!"]
  [:p "Women! Does your man always forget to pick up bread from the shop?  Does he forget about the important things you have planned in the day?  If your nodding along then this is the app for you! Spouse Reminder is the state of the art in nagg... managing your partners time."]
  [:p "Simply create an account, download the android app, and any reminders you add to the site will automatically sync to your partners phone and give them a little notification to remind them of a chore they forgot."])

(defpartial main-content []
  (main/layout
   [:div {:class "wrapper col3"}
    [:div {:id "container"}
       [:div {:class "homepage"}
	[:ul
	 [:li
	  (site-detail)]]
	[:br {:class "clear"}]]]]))

(defpage "/" []
  (main-content))
