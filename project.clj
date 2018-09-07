(defproject tickets "0.1.0-SNAPSHOT"
  :description "A simple ticket support system."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot tickets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
