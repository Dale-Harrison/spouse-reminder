(ns spouse-reminder.server
  (:require [noir.server :as server]))

(server/load-views "src/spouse_reminder/views/")

(defn https-url [request-url]
  (str "https://" (:server-name request-url) ":" (:server-port request-url) (:uri request-url)))

(defn require-https
  [handler]
  (fn [request]
    (if (= (:scheme request) :http)
      (ring.util.response/redirect (https-url request))
      (handler request))))

(server/add-middleware require-https)

(defn -main [& m]
  (let [mode (or (first m) :dev)
        port (Integer. (get (System/getenv) "PORT" "8080"))
	ssl-port (Integer. "443")]
    (def myappserver (server/start ssl-port
				   {:mode (keyword mode)
				    :jetty-options {:port port
						    :ssl-port ssl-port
						    :join? false
						    :ssl? true
						    :keystore "my.keystore"
						    :key-password "foobar"}
				    :ns 'spouse-reminder
				    :session-cookie-attrs {:max-age 3600
							   :secure true}}))))