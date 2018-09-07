(ns tickets.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [tickets.core :refer [app]]))

(defn parse-body [{:keys [body] :as response}]
  (assoc response :body (json/parse-string (slurp body) true)))

(deftest test-app
  (testing "main route"
    (let [response (parse-body (app (mock/request :get "/api/v1")))
          expected {:status 200
                    :headers {"Content-Type" "application/json; charset=utf-8"}
                    :body {:message "Hello World"}}]
      (is (= expected response)))))
