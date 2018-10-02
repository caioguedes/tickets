(ns tickets.core
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]
            [tickets.db.ticket :as ticket]))

(def mock-ticket {:id 1
                  :subject "New Ticket"
                  :body "This is a new ticket, yet"
                  :status {:id 1
                           :name "New"}})

(def db-spec {:dbtype "postgresql"
              :dbname "tickets"
              :host "localhost"
              :user "postgres"
              :password ""
              :stringtype "unspecified"})

(def tickets-routes
  (context "/tickets" []

    (GET "/" [request]
         :summary "List tickets"
         (let [page (get-in request [:params :page] 1)
               per_page (get-in request [:params :per_page] 10)]
           (ok (ticket/find-tickets-paginate db-spec page per_page))))

    (GET "/:id" [id]
         :summary "Get a single ticket"
         (if-let [ticket (ticket/get-ticket db-spec id)]
           (ok {:results ticket})
           (not-found {:message "Not Found"})))

    (POST "/" []
          :summary "Create a new ticket"
          (created "/api/v1/tickets/1" {:results mock-ticket}))

    (PUT "/:id" [id :as request]
         :summary "Update a ticket"
         (if-not (ticket/get-ticket db-spec id)
           (not-found {:message "Not Found"})
           (let [changes (select-keys (:params request) [:subject :body :status_id])
                 updated (ticket/update-ticket db-spec (assoc changes :id id))]
             (ok {:results (ticket/get-ticket db-spec id)}))))))

(def mock-comments [{:id 1
                     :ticket_id 1
                     :body "Could you discribe the error?"}
                    {:id 2
                     :ticket_id 1
                     :body "Yeah! When I push the button, nothing happens..."}])

(def tickets-comments-routes
  (context "/tickets/:ticket-id" [ticket-id]

    (GET "/comments" [request]
         :summary "List comments on a ticket"
         (let [page (get-in request [:params :page] 1)
               per_page (get-in request [:params :per_page] 10)]
           (ok {:results [mock-comments]
                :page 1
                :per_page 10
                :total 2
                :total_pages 1})))

    (GET "/comments/:id" [id :as request]
         :summary "Get a comment"
         (ok {:results (first mock-comments)}))))

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
     tickets-routes
     ;; Tickets Comments
     tickets-comments-routes)

   (undocumented
    (route/not-found (not-found {:message "Not Found"})))))
