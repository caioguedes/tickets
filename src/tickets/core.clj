(ns tickets.core
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]))

(def mock-ticket {:id 1
                   :subject "New Ticket"
                   :body "This is a new ticket, yet"
                   :status {:id 1
                            :name "New"}})

(def tickets-routes
  (context "/tickets" []

    (GET "/" []
      :summary "List tickets"
      (ok {:results [mock-ticket]
           :page 1
           :per_page 10
           :total 1
           :total_page 1}))

    (GET "/:id" [id]
      :summary "Get a single ticket"
      (ok {:results mock-ticket}))

    (POST "/" []
      :summary "Create a new ticket"
      (ok {:results mock-ticket}))))

(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Tickets Api"
                   :description "A simple tickets support system."}
            :tags [{:name "api"
                    :description "Tickets Api"}]}}}

   (context "/api/v1" []
     :tags ["api"]
     (GET "/" [] (ok {:message "Hello World"}))
     ;; Tickets
     tickets-routes)
   (undocumented
    (route/not-found (not-found {:message "Not Found"})))))
