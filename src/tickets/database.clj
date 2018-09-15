(ns tickets.database
  (:require [clojure.java.jdbc :as j]))

(def ticket-table-ddl
  (j/create-table-ddl :ticket
                      [[:id        "SERIAL PRIMARY KEY"]
                       [:subject   "VARCHAR NOT NULL"]
                       [:body      "TEXT NOT NULL"]
                       [:status_id "INTEGER NOT NULL"]]))

(defn create-tables [spec]
  (j/db-do-commands spec [ticket-table-ddl]))

(defn drop-tables [spec]
  (j/db-do-commands spec [(j/drop-table-ddl :ticket {:conditional? true})]))
