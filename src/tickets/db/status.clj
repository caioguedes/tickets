(ns tickets.db.status
  (:require [clojure.java.jdbc :as j]))

(defn get-status [db-spec id]
  (j/query db-spec
           ["select * from ticket_status where id = ?" id]
           {:result-set-fn first}))
