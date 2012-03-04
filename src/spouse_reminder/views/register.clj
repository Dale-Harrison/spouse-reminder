(ns spouse-reminder.views.register
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [noir.validation :as vali])
  (:require [spouse-reminder.views.main :as main]
        [spouse-reminder.models.users :as userreg]
        [noir.response :as resp]))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial user-fields [{:keys [username password email]}]
  (vali/on-error :username error-item)
  (label "username" "Username :")
  (text-field "username")
  [:br]
  (vali/on-error :password error-item)
  (label "password" "Password: ")
  (password-field "password")
  [:br]
  (vali/on-error :email error-item)
  (label "email" "Email Address: ")
  (text-field "email"))

(defn valid? [{:keys [username password email]}]
  (vali/rule (vali/min-length? username 3)
             [:username "Your user name must have more than 1 letters."])
  (vali/rule (vali/has-value? username)
	     [:username "Your user name must have a value."])
  (vali/rule (vali/max-length? username 20)
	     [:username "Your user name must be less that 20 letters."])
  (vali/rule (vali/has-value? password)
             [:password "You must have a password"])
  (vali/rule (vali/is-email? email)
	     [:email "Please provide a correctly formatted email address."])
  (not (vali/errors? :username :password :email)))

(defpage "/register" {:as register}
  (if (userreg/user?)
    (userreg/logout!))
  (main/layout
   [:div {:class "wrapper col3"}
    [:div {:id "container"}
     [:div {:class "homepage"}
      (form-to [:post "/register"]
	       (user-fields register)
	       [:br]
	       (submit-button {:class "submit"} "Submit"))]]]))

(defpage [:post "/register"] {:as register}
  (if (valid? register)
    (do
      (if (userreg/add-user register)
	(resp/redirect "/reminders")
	(do
	  [:div {:class "wrapper col3"}
	   [:div {:id "container"}
	    [:div {:class "homepage"}
	     (form-to [:post "/register"]
		      [:p "This username has already been taken."]
		      (user-fields register)
		      (submit-button {:class "submit"} "Submit"))]]])))
    (render "/register" register)))
  
      
    