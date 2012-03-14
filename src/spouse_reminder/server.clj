(ns spouse-reminder.server
  (:require [noir.server :as server]))

(server/load-views "src/spouse_reminder/views/")

(defn https-url [request-url]
  (str "https://sharp-wind-8679.herokuapp.com"))

(comment (defn require-https
  [handler]
  (fn [request]
    (if (= (:scheme request) :http)
      (ring.util.response/redirect "http://sharp-wind-8679.herokuapp.com:443")
      (handler request)))))

(defn require-https
  [handler]
    (println handler))

(server/add-middleware require-https)

(defn -main [& m]
  (let [mode (or (first m) :dev)
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (def myappserver (server/start port
				   {:mode (keyword mode)
				    :ns 'spouse-reminder
				    :session-cookie-attrs {:max-age 3600
							   :secure true}}))))