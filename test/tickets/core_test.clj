(ns tickets.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [tickets.database :refer [create-tables drop-tables]]
            [tickets.db.ticket :as ticket]
            [tickets.core :refer [app db-spec]]))

(defn database-fixtures [test]
  (do
    (drop-tables db-spec)
    (create-tables db-spec))
  (test)
  (drop-tables db-spec))

(use-fixtures :each database-fixtures)

(defn parse-body [{:keys [body] :as response}]
  (assoc response :body (json/parse-string (slurp body) true)))

(def default-headers {"Content-Type" "application/json; charset=utf-8"})

(deftest test-app
  (testing "Main router"
    (let [response (parse-body (app (mock/request :get "/api/v1")))
          expected {:status 200
                    :headers default-headers
                    :body {:message "Hello World"}}]
      (is (= expected response))))

  (testing "Not found"
    (let [response (parse-body (app (mock/request :get "/api/v1/not-found")))
          expected {:status 404
                    :headers default-headers
                    :body {:message "Not Found"}}]
      (is (= expected response)))))
