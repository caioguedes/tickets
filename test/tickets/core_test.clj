(ns tickets.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [tickets.core :refer [app]]))

(deftest test-app
  (testing "main route"
    (is (= {:status 200
            :headers {}
            :body "Hello World"}
           (app (mock/request :get "/"))))))
