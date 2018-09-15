(ns tickets.db.ticket
  (:require [clojure.java.jdbc :as j]))

(defn create-ticket [db-spec ticket]
  (j/insert! db-spec :ticket ticket))

(defn update-ticket [db-spec ticket]
  (j/update! db-spec :ticket ticket ["id = ?" (:id ticket)]))

(defn get-ticket [db-spec id]
  (when-let [ticket (j/query db-spec ["select * from ticket where id = ?" id] {:result-set-fn first})]
    (-> ticket
        (assoc :status {:id 1 :name "New"}) ;; Temporary
        (dissoc :status_id))))

