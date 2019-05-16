(ns tickets.core
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]
            [tickets.db.ticket :as ticket]
            [tickets.db.comment :as ticket-comment]))

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

    (POST "/" [& parameters]
          :summary "Create a new ticket"
          (let [data (-> (select-keys parameters [:subject :body])
                         ticket/include-defaults)
                ticket-id (:id (ticket/create-ticket db-spec data))
                ticket (ticket/get-ticket db-spec ticket-id)]
            (created (str "/api/v1/tickets/" ticket-id) {:results ticket})))

    (PUT "/:id" [id :as request]
         :summary "Update a ticket"
         (if-not (ticket/get-ticket db-spec id)
           (not-found {:message "Not Found"})
           (let [changes (select-keys (:params request) [:subject :body :status_id])
                 updated (ticket/update-ticket db-spec (assoc changes :id id))]
             (ok {:results (ticket/get-ticket db-spec id)}))))))

(def tickets-comments-routes
  (context "/tickets/:ticket-id" [ticket-id]

    (GET "/comments" [request]
         :summary "List comments on a ticket"
         (let [page (get-in request [:params :page] 1)
               per_page (get-in request [:params :per_page] 10)]
           (ok (ticket-comment/get-comments-on-ticket-paginate
                db-spec
                ticket-id
                page
                per_page))))

    (GET "/comments/:id" [id :as request]
         :summary "Get a comment"
         (if-let [comment (ticket-comment/get-comment db-spec id)]
           (ok {:results comment})
           (not-found {:message "Not Found"})))))

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
