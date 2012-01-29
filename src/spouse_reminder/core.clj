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

(comment *********************** login form config *********************)

(def properties
     {:username "Username"
      :password "Password"
      :username-validation-error "Enter either admin or member"
      :password-validation-error "Enter a password!"
      :logout-page "/after-logout"})

(defn query [type]
 (ensure-any-role-if (= type :top-secret) #{:admin}
                     (= type :members-only) #{:member}
                     (str (name type) " data")))

(comment *********************** page views *****************************)

(defn layout [content]
  (html
   (doctype :html4)
   [:html
    [:head
     (stylesheet "sandbar.css")
     (icon "icon.png")]
    [:body
     [:h2 "Sandbar Security Example"]
     content
     [:br]
     [:div (if-let [username (current-username)]
             [:div
              (str "You are logged in as " username ". ")
              (link-to "logout" "Logout")])]]]))

(defn data-view [title data & links]
  [:div
   [:h3 title]
   [:p data]
   (if (seq links) links [:div (link-to "/" "Home")])])

(defn home-view-data []
   [:div (link-to "reminders" "You Reminders!")])

(defn home-view []
  (data-view "Home" (home-view-data)))
             
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
  [:div
   [:h3 (:title reminder)]
   [:p (:date reminder)]
   [:p (:body reminder)]])

(defn query [type data]
  (ensure-any-role-if (= type "Member") #{:member}
		      (= type "Admin") #{:admin}
		      data))

(defn get-reminders-data []
  [:div (link-to "reminder" "You Reminders!")]
  [:br]
  [:div (map format-reminder (db/get-reminders "Dale"))])

(defn get-user-reminders-view []
  (data-view "Reminders"
      (query "Member" (get-reminders-data))))

(defn add-reminder-view-data []
  (form-to [:post "/reminders/add"]
              [:fieldset
               [:legend "Create a new reminder"]
               [:ol
                [:li
                 [:label {:for :title} "Title"]
                 (text-field :title)]
                [:li
                 [:label {:for :body} "Body"]
                 (text-area :body)]
		[:li
		 [:lable {:for :date} "Date"]
		 (text-area :date)]
		[:button {:type "submit"} "Post!"]]]))

(defn add-reminder-view []
  (query "Member" (add-reminder-view-data)))

(defn add-reminder-post [title date body]
  (do
    (db/add-reminder title (current-username) date body)
    (ringresp/redirect "/reminders")))

(comment ******************** Post functions ************************)

(defn add-reminder [title body date]
  (db/add-reminder title body date (current-username)))

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
  (GET "/" [] (layout (home-view)))
  (GET "/reminders" [] (layout (get-user-reminders-view)))
  (GET "/reminders/add" [] (layout (add-reminder-view)))
  (POST "/reminders/add" [title body date] (add-reminder-post title body date))
  (GET "/logout*" [] (logout! properties))
  (GET "/after-logout" [] (layout (after-logout-view)))
  (GET "/permission-denied*" [] (layout (permission-denied-view)))
  (GET "/remservice*" {params :params} (service/get-service-reminders params))
  (form-authentication-routes (fn [_ c] (layout c))
                              (form-authentication-adapter)))

(def security-config
     [#"/login.*" :ssl
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
