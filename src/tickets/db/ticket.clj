(ns tickets.db.ticket
  (:require [clojure.java.jdbc :as j]
            [tickets.db.status :as status]))

(defn create-ticket [db-spec ticket]
  (j/insert! db-spec :ticket ticket))

(defn update-ticket [db-spec ticket]
  (j/update! db-spec :ticket ticket ["id = ?" (:id ticket)]))

(defn get-ticket [db-spec id]
  (when-let [ticket (j/query db-spec ["select * from ticket where id = ?" id] {:result-set-fn first})]
    (-> ticket
        (assoc :status (status/get-status db-spec (:status_id ticket)))
        (dissoc :status_id))))
