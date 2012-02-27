(ns spouse-reminder.views.reminder
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.models.reminders :as remins]
            [spouse-reminder.models.users :as usermod]
	    [spouse-reminder.views.main :as main]
            [noir.response :as resp]))

(defn format-reminder [reminder]
   [:p (:date reminder) " " (:body reminder)])

(defn get-reminders []
  [:div (map format-reminder (remins/get-all-reminders (usermod/me)))])

(defpartial add-reminder-fields [{:keys [reminder] :as reminder}]
  (label "reminder" "Reminder")
  (text-area "reminder" reminder))

(defpartial get-reminders-formatted []
   [:div {:class "wrapper col3"}
      [:div {:id "container"}
       [:div {:class "homepage"}
	[:ul
	 [:li [:h2 "Item 1"][:p "This is item 1"]]
	 [:li [:p get-reminders]]
	 [:li {:class "last"} [:p add-reminder-fields]]]
	[:br {:class "clear"}]]]])

(defpartial user-fields [{:keys [username password] :as usr}]
            (text-field {:placeholder "Username"} :username username)
            (password-field {:placeholder "Password"} :password))

(pre-route "/reminders*" {}
	   (when-not (usermod/user?)
	     (resp/redirect "/login")))

(defpage "/" []
  (resp/redirect "/reminders"))

(defpage "/reminders" {:as reminder}
  (main/layout get-reminders-formatted))

(defpage "/login" {:as user}
         (if (usermod/user?)
           (resp/redirect "/reminders")
           (main/layout
             (form-to [:post "/login"]
                      [:ul.actions
                       [:li (link-to {:class "submit"} "/" "Login")]]
                      (user-fields user)
                      (submit-button {:class "submit"} "submit")))))

(defpage [:post "/login"] {:as user}
         (if (usermod/login! user)
           (resp/redirect "/reminders")
            (render "/login" user)))

(defpage [:post "/reminders/add"] {:as reminder}
  (if (usermod/user?)
    (remins/add-reminder reminder)
    (resp/redirect "/login")))
    
	    