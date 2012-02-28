(ns spouse-reminder.views.register
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.views.main :as main]
        [spouse-reminder.models.users :as userreg]
        [noir.response :as resp]))

(defpartial get-register-formatted []
  [:div {:class "wrapper col3"}
   [:div {:id "container"}
    [:div {:class "homepage"}
     (form-to [:post "/login"]
	      [:table
	       [:tr
	        [:td (label "u" "Username: ")][:td(text-field "username")]]
	       [:tr
	        [:td (label "p" "Password: ")][:td(password-field "password")]]
	       [:tr
		[:td (label "e" "Email Address: ")][:td(text-field "email")]]]
              (submit-button {:class "submit"} "Submit"))]]])

(defpage "/register" {:as register}
           (main/layout
             (get-register-formatted)))

(defpage [:post "/register"] {:as register}
    (userreg/add-user register)
    (resp/redirect "/reminders"))
    