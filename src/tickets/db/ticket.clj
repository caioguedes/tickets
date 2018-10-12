(ns tickets.db.ticket
  (:require [clojure.java.jdbc :as j]
            [tickets.db.status :as status]))

(defn include-defaults [ticket]
  (-> ticket
      (assoc :status_id 1)))

(defn include-ticket-status [db-spec ticket]
  (-> ticket
      (assoc :status (status/get-status db-spec (:status_id ticket)))
      (dissoc :status_id)))

(defn create-ticket [db-spec ticket]
  (first (j/insert! db-spec :ticket ticket)))

(defn update-ticket [db-spec ticket]
  (j/update! db-spec :ticket (dissoc ticket :id) ["id = ?" (:id ticket)]))

(defn get-ticket [db-spec id]
  (j/query db-spec
           ["select * from ticket where id = ?" id]
           {:row-fn (partial include-ticket-status db-spec)
            :result-set-fn first}))

(defn get-total-tickets [db-spec]
  (j/query db-spec
           ["select count(*) total from ticket"]
           {:row-fn :total
            :result-set-fn first}))

(defn find-tickets-paginate [db-spec page per_page]
  (let [offset (max 0 (* (- page 1) per_page))
        total (get-total-tickets db-spec)
        total_pages (int (Math/ceil (/ total per_page)))
        tickets (j/query db-spec
                         ["select * from ticket limit ? offset ?" per_page offset]
                         {:row-fn (partial include-ticket-status db-spec)})]
    {:results tickets
     :page page
     :per_page per_page
     :total total
     :total_pages total_pages}))
