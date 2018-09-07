(ns tickets.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]))

(def app
  (api
   (context "/api/v1" []
            (GET "/" [] (ok {:message "Hello World"})))))
