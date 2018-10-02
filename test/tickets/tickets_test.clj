(ns tickets.tickets-test
  (:require  [clojure.test :refer :all]
             [ring.mock.request :as mock]
             [tickets.db.ticket :as ticket]
             [tickets.core :refer [app db-spec]]
             [tickets.core-test :refer [database-fixtures parse-body default-headers]]))

(use-fixtures :each database-fixtures)

(def mock-ticket {:id 1
                  :subject "New Ticket"
                  :body "This is a new ticket, yet"
                  :status {:id 1
                           :name "New"}})

(deftest test-tickets-list-route
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
      (is (= expected response)))))

(deftest test-tickets-get-route
  (testing "Get a ticket"
    (let [ticket-fixture (ticket/create-ticket db-spec {:subject "New Ticket"
                                                        :body "This is a new ticket, yet"
                                                        :status_id 1})
          response (parse-body (app (mock/request :get "/api/v1/tickets/1")))
          expected {:status 200
                    :headers default-headers
                    :body {:results mock-ticket}}]
      (is (= expected response))))

  (testing "Get a ticket does not exist"
    (let [response (parse-body (app (mock/request :get "/api/v1/tickets/10")))
          expected {:status 404
                    :headers default-headers
                    :body {:message "Not Found"}}]
      (is (= expected response)))))

(deftest test-tickets-create-route
  (testing "Create a ticket"
    (let [response (parse-body (app (-> (mock/request :post "/api/v1/tickets")
                                        (mock/json-body (select-keys
                                                         mock-ticket
                                                         [:subject :body])))))
          expected {:status 201
                    :headers (conj default-headers {"Location" "/api/v1/tickets/1"})
                    :body {:results mock-ticket}}]
      (is (= expected response)))))

(deftest test-tickets-update-route
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

(def mock-comments [{:id 1
                     :ticket_id 1
                     :body "Could you discribe the error?"}
                    {:id 2
                     :ticket_id 1
                     :body "Yeah! When I push the button, nothing happens..."}])

(deftest test-tickets-comment-list-route
  (testing "List comments on a ticket"
    (let [response (parse-body (app (mock/request :get "/api/v1/tickets/1/comments")))
          expected {:status 200
                    :headers default-headers
                    :body {:results [mock-comments]
                           :page 1
                           :per_page 10
                           :total 2
                           :total_pages 1}}]
      (is (= expected response)))))

(deftest test-tickets-comment-get-route
  (testing "Get a comment"
    (let [response (parse-body (app (mock/request :get "/api/v1/tickets/1/comments/1")))
          expected {:status 200
                    :headers default-headers
                    :body {:results (first mock-comments)}}]
      (is (= expected response)))))
