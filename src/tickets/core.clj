(ns tickets.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]))

(def app
   (GET "/" [] (ok "Hello World")))
