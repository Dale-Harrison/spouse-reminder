(ns spouse-reminder.views.account
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [spouse-reminder.models.users :as usermod]
	    [spouse-reminder.views.main :as main]
            [noir.response :as resp]))


(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial user-fields [{:keys [password email]}]
  (vali/on-error :username error-item)
  (label "username" "Username :")
  (label "username" (usermod/user?))
  [:br]
  (vali/on-error :password error-item)
  (label "password" "Password: ")
  (password-field "password")
  [:br]
  (vali/on-error :email error-item)
  (label "email" "Email Address: ")
  (text-field "email"))

(defn valid? [{:keys [password email]}]
  (vali/rule (vali/has-value? password)
             [:password "You must have a password"])
  (vali/rule (vali/is-email? email)
	     [:email "Please provide a correctly formatted email address."])
  (not (vali/errors? :username :password :email)))

(defpage "/account" {:as account}
  (main/layout
   [:div {:class "wrapper col3"}
    [:div {:id "container"}
     [:div {:class "homepage"}
      (form-to [:post "/account"]
	       (user-fields account)
	       [:br]
	       (submit-button {:class "submit"} "Submit"))]]]))

(defpage [:post "/account"] {:as account}
  (if (valid? register)
    (do
      (userreg/update-user register)
      (resp/redirect "/reminders"))
    (render "/register" register)))
