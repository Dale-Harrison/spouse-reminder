ns spouse-reminder.views.register
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
	hiccup.form-helpers)
(:require [spouse-reminder.views.main :as main]
	  [spouse-reminder.models.users :as userreg]
          [noir.response :as resp]))

(defpage "/register" {:as register}
           (main/layout
             (form-to [:post "/login"]
                      [:ul.actions
                       [:li (link-to {:class "submit"} "/" "Login")]]
                      (user-fields user)
                      (submit-button {:class "submit"} "submit")))))

(defpage [:post "/regsiter"] {:as register}
    (userreg/add-user register)
    (resp/redirect "/reminders")))
    