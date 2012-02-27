(ns spouse-reminder.views.reminder
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.models.reminders :as remins]
            [spouse-reminder.models.users :as usermod]
	    [spouse-reminder.views.main :as main]
            [noir.response :as resp]))

(defpartial add-reminder-fields []
  [:form {:action "/reminders" :method "post" :id "nag"}
   (text-area "body")
   [:br]
   (submit-button {:class "Submit"} "Post!")])

(defpartial get-reminders-formatted []
   [:div {:class "wrapper col3"}
      [:div {:id "container"}
       [:div {:class "homepage"}
	[:ul
	 [:li [:h2 "What is it?"][:p "Spouse Reminder is for all those wives sick of their husband not listening, and for husbands to no longer listen to their wives nagging on about some chore that needs to be done around the house."]]
	 [:li [:h2 "Reminders"][:p (remins/get-all-reminders (usermod/me))]]
	 [:li {:class "last"} [:h2 "Add Reminder"] [:p (add-reminder-fields)]]]
	[:br {:class "clear"}]]]])

(defpartial get-login-formatted []
  [:div {:class "wrapper col3"}
   [:div {:id "container"}
    [:div {:class "homepage"}
     (form-to [:post "/login"]
	      (text-field "username")
	      [:br]
	      (password-field "password")
	      [:br]
              (submit-button {:class "submit"} "Submit"))]]])
     

(pre-route "/reminders*" {}
	   (when-not (usermod/user?)
	     (resp/redirect "/login")))

(defpage "/" []
  (resp/redirect "/reminders"))

(defpage "/reminders" {:as reminder}
  (main/layout (get-reminders-formatted)))

(defpage "/login" {:as user}
         (if (usermod/user?)
           (resp/redirect "/reminders")
           (main/layout (get-login-formatted))))
             

(defpage [:post "/login"] {:as user}
         (if (usermod/login! user)
           (resp/redirect "/reminders")
            (render "/login" user)))

(defpage [:post "/reminders"] {:as reminders}
  (if (usermod/user?)
    (remins/add-reminder reminders)
    (resp/redirect "/login")))
    
	    