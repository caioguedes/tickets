(defproject tickets "0.1.0-SNAPSHOT"
  :description "A simple ticket support system."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [metosin/compojure-api "2.0.0-alpha25"]
                 [cheshire "5.8.0"]]
  :ring {:handler tickets.core/app}
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]]
                   :plugins [[lein-ring "0.12.4"]]}})
