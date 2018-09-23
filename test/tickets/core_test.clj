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

(def mock-ticket {:id 1
                  :subject "New Ticket"
                  :body "This is a new ticket, yet"
                  :status {:id 1
                           :name "New"}})

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
      (is (= expected response))))

  (testing "List tickets"
    (let [ticket-fixture (ticket/create-ticket db-spec {:subject "New Ticket"
                                                        :body "This is a new ticket, yet"
                                                        :status_id 1})
          response (parse-body (app (mock/request :get "/api/v1/tickets")))
          expected {:status 200
                    :headers default-headers
                    :body {:results [mock-ticket]
                           :page 1
                           :per_page 10
                           :total 1
                           :total_pages 1}}]
      (is (= expected response))))

  (testing "Get a ticket"
    (let [response (parse-body (app (mock/request :get "/api/v1/tickets/1")))
          expected {:status 200
                    :headers default-headers
                    :body {:results mock-ticket}}]
      (is (= expected response))))

  (testing "Create a ticket"
    (let [response (parse-body (app (-> (mock/request :post "/api/v1/tickets")
                                        (mock/json-body (select-keys
                                                         mock-ticket
                                                         [:subject :body])))))
          expected {:status 201
                    :headers (conj default-headers {"Location" "/api/v1/tickets/1"})
                    :body {:results mock-ticket}}]
      (is (= expected response))))

  (testing "Update a ticket"
    (let [ticket-fixture (ticket/create-ticket db-spec {:subject "New Ticket"
                                                        :body "This is a new ticket, yet"
                                                        :status_id 1})
          response (parse-body (app (-> (mock/request :put "/api/v1/tickets/1")
                                        (mock/json-body {:subject "Subject updated!"
                                                         :body "A brand new body here..."
                                                         :status_id 2}))))
          expected {:status 200
                    :headers default-headers
                    :body {:results {:id 1
                                     :subject "Subject updated!"
                                     :body "A brand new body here..."
                                     :status {:id 2
                                              :name "Open"}}}}]
      (is (= expected response))))

  (testing "Update a ticket does not exist"
    (let [response (parse-body (app (-> (mock/request :put "/api/v1/tickets/10")
                                        (mock/json-body {:subject "Ghost ticket"
                                                         :body "This ticket does not exists!"
                                                         :status_id 1}))))
          expected {:status 404
                    :headers default-headers
                    :body {:message "Not Found"}}]
      (is (= expected response)))))
