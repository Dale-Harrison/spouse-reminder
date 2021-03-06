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

(defpartial get-reminders-formatted-dyn []
   [:h2 "Reminders"][:p (remins/get-all-reminders (usermod/me))])
	 

(defpartial get-reminders-formatted []
  [:div {:class "wrapper col3"}
    [:div {:id "container"}
       [:div {:class "homepage"}
	[:ul
	 [:li
	  [:h2 "How to future nag!"]
	  [:div {:class "reminder"}
	   [:br ][:p "Simply put in a reminder in the following format."]
	   [:p [:b "My Reminder Detail dd/mm/yyyy tt:mm @location"]]
	   [:p "For example:"]
	   [:p [:b "Pick up milk 28/02/2012 17:00 @Sainsburys"]]]]
	  [:li {:id "dyn"} (get-reminders-formatted-dyn)]
	  [:li {:class "last"} [:h2 "Add Reminder"] [:p (add-reminder-fields)]]]
	[:br {:class "clear"}]]]])

(defpartial get-login-formatted []
  [:div {:class "wrapper col3"}
   [:div {:id "container"}
    [:div {:class "homepage"}
     [:ul
      [:li {:class "middle"}
       [:h2 "Please Log In"]
       [:div {:class "reminder"}
       [:br]
	(form-to [:post "/login"]
		 [:table
		  [:tr
		   [:td (label "lblusername" "Username: ")][:td (text-field "username")]]
		  [:tr
		   [:td [:br]]]
		  [:tr
		   [:td (label "lblpassword" "Password: ")][:td (password-field "password")]]
		  [:tr
		   [:td [:br]]]
		  [:tr
		   [:td (submit-button {:class "submit"} "Submit")]]])
       [:br]
       [:p "Not a member? "[:a {:href "/register"} "register"]]]]]]]])
     

(pre-route "/reminders*" {}
	   (when-not (usermod/user?)
	     (resp/redirect "/login")))


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

(defpage "/reminders/dyn" []
  (get-reminders-formatted-dyn))

(defpage "/logout" []
  (do
    (usermod/logout!)
    (resp/redirect "/login")))
    
	    