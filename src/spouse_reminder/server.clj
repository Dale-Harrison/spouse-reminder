(ns spouse-reminder.server
  (:require [noir.server :as server]))

(server/load-views "src/spouse_reminder/views/")

(defn https-url [request-url]
  (str "https://sharp-wind-8679.herokuapp.com"))

(comment (defn require-https
  [handler]
  (fn [request]
    (if (= (:x-forwarded-proto request) :http)
      (ring.util.response/redirect "https://sharp-wind-8679.herokuapp.com")
      (handler request)))))

(defn require-https
  [handler]
  (fn [request]
    (println (get-in request ["x-forwarded-proto"]))))

(server/add-middleware require-https)

(defn -main [& m]
  (let [mode (or (first m) :dev)
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (def myappserver (server/start port
				   {:mode (keyword mode)
				    :ns 'spouse-reminder
				    :session-cookie-attrs {:max-age 3600
							   :secure true}}))))