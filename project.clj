(defproject spouse-reminder "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.4.1"]
                 [hiccup "0.2.6"]
                 [sandbar "0.3.0-SNAPSHOT"]
		 [congomongo "0.1.7"]
		 [clj-time "0.3.4"]]
  :dev-dependencies [[jline "0.9.94"]
                     [ring/ring-devel "0.2.5"]
                     [ring/ring-jetty-adapter "0.2.5"]
                     [ring/ring-httpcore-adapter "0.2.5"]])
