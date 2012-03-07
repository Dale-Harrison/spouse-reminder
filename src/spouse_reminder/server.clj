(ns spouse-reminder.server
  (:require [noir.server :as server]))

(server/load-views "src/spouse_reminder/views/")

(defn https-url [request-url]
  (str "https://" (:server-name request-url) ":" 8443 (:uri request-url)))

(defn require-https
  [handler]
  (fn [request]
    (if (= (:scheme request) :http)
      (ring.util.response/redirect (https-url request))
      (handler request))))

;;(server/add-middleware require-https)

(defn -main [& m]
  (let [mode (or (first m) :dev)
        port (Integer. (get (System/getenv) "PORT" "80"))]
    (server/start port {:mode (keyword mode)
                        :ns 'spouse-reminder})))