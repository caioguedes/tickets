(ns tickets.db.comment
  (:require [clojure.java.jdbc :as j]))

(defn create-comment-on-ticket [db-spec ticket-id body]
  (j/insert! db-spec :ticket_comment {:ticket_id ticket-id
                                      :body body}))

(defn get-total-comments-on-ticket [db-spec ticket-id]
  (j/query db-spec
           ["select count(*) total from ticket_comment where ticket_id = ?"
            ticket-id]
           {:row-fn :total
            :result-set-fn first}))

(defn get-comment [db-spec id]
  (j/query db-spec
           ["select * from ticket_comment where id = ?" id]
           {:result-set-fn first}))

(defn get-comments-on-ticket-paginate [db-spec ticket-id page per-page]
  (let [offset (max 0 (* (- page 1) per-page))
        total (get-total-comments-on-ticket db-spec ticket-id)
        total-pages (int (Math/ceil (/ total per-page)))
        comments (j/query db-spec
                         ["select * from ticket_comment where ticket_id = ? limit ? offset ?"
                          ticket-id
                          per-page
                          offset])]
    {:results comments
     :page page
     :per_page per-page
     :total total
     :total_pages total-pages}))
