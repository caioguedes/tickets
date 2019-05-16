(ns tickets.database
  (:require [clojure.java.jdbc :as j]))

(def ticket-status-table-ddl
  (j/create-table-ddl :ticket_status
                      [[:id   "SERIAL PRIMARY KEY"]
                       [:name "VARCHAR NOT NULL"]]))


(def ticket-table-ddl
  (j/create-table-ddl :ticket
                      [[:id        "SERIAL PRIMARY KEY"]
                       [:subject   "VARCHAR NOT NULL"]
                       [:body      "TEXT NOT NULL"]
                       [:status_id "INTEGER NOT NULL"]
                       [:updated_at "TIMESTAMP"]
                       [:created_at "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"]]))
                       [:status_id :integer "NOT NULL" "REFERENCES ticket_status(id)"]]))

(def ticket-comment-table-ddl
  (j/create-table-ddl :ticket_comment
                      [[:id "SERIAL PRIMARY KEY"]
                       [:ticket_id :integer "NOT NULL" "REFERENCES ticket(id)"]
                       [:body "VARCHAR NOT NULL"]]))

(def default-ticket-status [{:name "New"}
                            {:name "Open"}
                            {:name "Pending"}
                            {:name "Hold"}
                            {:name "Solved"}
                            {:name "Closed"}])

(defn create-tables [spec]
  (do
    (j/db-do-commands spec [ticket-status-table-ddl
                            ticket-table-ddl
                            ticket-comment-table-ddl])
    (j/insert-multi! spec :ticket_status default-ticket-status)))

(defn drop-tables [spec]
  (j/db-do-commands spec [(j/drop-table-ddl :ticket_comment {:conditional? true})
                          (j/drop-table-ddl :ticket {:conditional? true})
                          (j/drop-table-ddl :ticket_status {:conditional? true})]))
