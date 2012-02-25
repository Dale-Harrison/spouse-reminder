(ns spouse-reminder.core
  (:use (ring.adapter jetty)
        (ring.middleware file params keyword-params)
        (compojure core)
        (hiccup core page-helpers form-helpers)
        (sandbar core
                 stateful-session
                 auth
                 form-authentication
                 validation))
  (:require [spouse-reminder.database :as db]
	    [spouse-reminder.remservice :as service]
	    [compojure.handler :as handler]
	    [ring.util.response :as ringresp]))

(comment *********************** security config *********************)

(def properties
     {:username "Username"
      :password "Password"
      :username-validation-error "Enter either admin or member"
      :password-validation-error "Enter a password!"
      :logout-page "/after-logout"})

(defn query [type data]
  (ensure-any-role-if (= type "Member") #{:member}
		      (= type "Admin") #{:admin}
		      data))

(comment *********************** regular expressions ********************)

(defn first-match [m]
  (if (coll? m) (first m) m))

(defn get-body [entry]
  (first-match (re-find #"(.*)(?=(.\d{1,2})/(\d{1,2})/(\d{4}|\d{2})\s(\d{1,2}):(\d{1,2}))" entry)))

(defn get-date [entry]
  (first-match (re-find #"(\d{1,2})/(\d{1,2})/(\d{4}|\d{2})\s(\d{1,2}):(\d{1,2})" entry)))

(defn get-location [entry]
  (if (= (re-find #"@.*" entry) nil)
    ""
    (re-find #"@.*" entry)))

(comment *********************** page views *****************************)

(defn layout [content]
  (html
   (doctype :html5)
   [:html
    [:head
     (stylesheet "main.css")
     (stylesheet "sandbar-forms.css")
     (include-js "jquery.form.js")]
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
       (str "You are logged in as tofix")]]]]))

(defn wrapper-col-3-reminders [reminders addreminder]
  (html
   [:div {:class "wrapper col3"}
      [:div {:id "container"}
       [:div {:class "homepage"}
	[:ul
	 [:li [:h2 "Item 1"][:p "This is item 1"]]
	 [:li [:p reminders]]
	 [:li {:class "last"} [:p addreminder]]]
	[:br {:class "clear"}]]]]))

(defn wrapper-col-3-login-register [content]
  (html
  [:div {:class "wrapper col3"}
      [:div {:id "container"}
       [:div {:class "homepage"}
       [:p content]]]]))

(defn permission-denied-view []
 [:div
  [:h3 "Permission Denied"]
  [:div (link-to "home" "Home")]])

(defn after-logout-view []
 [:div
  [:h3 "Logout"]
  [:p "You are no longer logged in!"]
  [:div (link-to "home" "Home")]])

(defn format-reminder [reminder]
   [:p (:date reminder) " " (:body reminder)])

(defn get-reminders-data []
  [:div (map format-reminder (db/get-reminders "Dale"))])

(defn get-user-reminders-view []
      (query "Member" (get-reminders-data)))

(defn add-reminder-view-data []
  (form-to [:post "/reminders/add"]
              [:fieldset
               [:legend "Create a new reminder"]
               [:ol
                [:li
                 [:label {:for :reminder} "Reminder"]
                 (text-area :reminder)]
		[:button {:type "submit"} "Post!"]]]))

(defn add-reminder-view []
  (query "Member" (add-reminder-view-data)))

(defn register-user-view []
  (form-to [:post "/register"]
	   [:fieldset
	    [:legend "Create a new user account"]
	    [:ol
	     [:li
	      [:label {:for :username} "User Name"]
	      (text-field :username)]
	     [:li
	      [:label {:for :password} "Password"]
	      (password-field :password)]
	     [:li
	      [:label {:for :email} "Email Address"]
	      (text-field :email)]]
	    [:button {:type "submit"} "Register!"]]))

(comment ******************** Post functions ************************)

(defn add-reminder-post [reminder]
  (do
    (db/add-reminder (current-username) (get-body reminder) (get-date reminder) (get-location reminder))
    (ringresp/redirect "/reminders")))

(defn register-user-post[username password email]
  (do
    (db/add-user username password email)
    (ringresp/redirect "/login")))


(comment ******************** user validation ***********************)

(defn is-member [username]
  (if (= (:usertype (db/get-user username) "Member"))
    true
    false))

(defn valid-user-password [username password]
  (if (= (db/get-user-password username) password)
    true
    false))
 
(defrecord DemoAdapter []
  FormAuthAdapter
  (load-user
   [this username password]
   (let [login {:username username :password password}]
     (if (is-member username)
         (merge login {:roles #{:member}})
          login)))
  (validate-password
   [this]
   (fn [m]
     (if (= (:username m) (:password m))
       m
       (add-validation-error m "Password is incorrect!")))))

(defn form-authentication-adapter []
  (merge (DemoAdapter.) properties))

(comment ************************* Routes and security config ***************************)

(defroutes my-routes
  (GET "/reminders" [] (layout (wrapper-col-3-reminders ((get-user-reminders-view) (add-reminder-view)))))
  (POST "/reminders/add" [reminder] (add-reminder-post reminder))
  (GET "/register" [] (layout (wrapper-col-3-login-register (register-user-view))))
  (POST "/register" [username password email] (register-user-post username password email))
  (GET "/logout*" [] (logout! properties))
  (GET "/after-logout" [] (layout (after-logout-view)))
  (GET "/permission-denied*" [] (layout (permission-denied-view)))
  (GET "/remservice/reminders" {params :params} (service/get-service-reminders params))
  (GET "/remservice/hello" {params :params} (service/hello params))
  (form-authentication-routes (fn [_ c] (layout (wrapper-col-3-login-register c)))
			      (form-authentication-adapter)))

(def security-config
     [#"/login.*" :ssl
      #"/register.*" :ssl
      #".*.css|.*.png" :any-channel
      #".*" :nossl])

(def app (-> my-routes
	     handler/api
             (with-security form-authentication)
	     wrap-stateful-session
             (wrap-file "public")
             (with-secure-channel security-config 8080 8443)))

(run-jetty (var app) {:join? false :ssl? true :port 8080 :ssl-port 8443
                        :keystore "my.keystore"
                        :key-password "foobar"})
