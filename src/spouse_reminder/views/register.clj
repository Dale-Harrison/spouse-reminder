(ns spouse-reminder.views.register
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.views.main :as main]
        [spouse-reminder.models.users :as userreg]
        [noir.response :as resp]))

(defpartial user-fields [{:keys [username password email]}]
  (label "username" "Username :")
  (text-field "username" username)
  (label "password" "Password: ")
  (password-field "password" password)
  (label "email" "Email Address: ")
  (text-field "email" email))


  

(defpage "/register" {:as register}
           (main/layout
             [:div {:class "wrapper col3"}
	      [:div {:id "container"}
	       [:div {:class "homepage"}
		(form-to [:post "/login"]
		 (user-fields register)
		  (submit-button {:class "submit"} "Submit"))]]]))

(defpage [:post "/register"] {:as register}
    (userreg/add-user register))
    