(ns spouse-reminder.views.account
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
  (:require [noir.validation :as vali])
  (:require [spouse-reminder.models.users :as usermod]
	    [spouse-reminder.views.main :as main]
            [noir.response :as resp]))


(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpartial user-fields [{:keys [password email]}]
  [:tr
   [:td
    (vali/on-error :username error-item)
    (label "username" "Username :")]
   [:td
    (label "username" (usermod/user?))]]
  [:tr
   [:td [:br]]]
  [:tr
   [:td
    (vali/on-error :password error-item)
    (label "password" "Password: ")]
   [:td (password-field "password")]]
  [:tr
   [:td [:br]]]
  [:tr
   [:td (vali/on-error :email error-item)
    (label "email" "Email Address: ")]
   [:td (text-field "email"))])

(defn valid? [{:keys [password email]}]
  (vali/rule (vali/has-value? password)
             [:password "You must have a password"])
  (vali/rule (vali/is-email? email)
	     [:email "Please provide a correctly formatted email address"])
  (not (vali/errors? :username :password :email)))


(pre-route "/account*" {}
	   (when-not (usermod/user?)
	     (resp/redirect "/login")))

(defpage "/account" {:as account}
  (main/layout
   [:div {:class "wrapper col3"}
    [:div {:id "container"}
     [:div {:class "homepage"}
      [:ul
       [:li {:class "middle"}
        [:h2 "Please Log In"]
        [:br]
	[:div {:class "reminder"}
        (form-to [:post "/account"]
		 [:table
	       (user-fields account)
	       [:br]
	       (submit-button {:cass "submit"} "Submit")])]]]]]]))

(defpage [:post "/account"] {:as account}
  (if (valid? account)
    (do
      (usermod/update-user account)
      (resp/redirect "/reminders"))
    (render "/account" account)))
