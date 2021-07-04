(ns s71-challenge.core
  (:require [s71-challenge.queue.actions :refer [push pop peek confirm queue-length]]
            [s71-challenge.db :as db])
  (:gen-class))


(defn initialize-db
  "Function that creates a new database table
  can be preserved by passing 'false' in as a parameter"
  [delete-table?]
  (import java.sql.SQLException)
  (try
    (db/create-KrayzelKueue-table db/config)
    (catch Exception _e
      (if delete-table?
        [(db/delete-table db/config)
         (db/create-KrayzelKueue-table db/config)]
        true))))


(defn -main
  "Upon start, if the new-queue command line argument was passed in
   the existing table in the database is deleted and brand new table is created"
  ; If no value was passed into main, call main again with an empty vector for the .contains function
  ([] (-main []))
  ([& args]
   (initialize-db
    (.contains args "new-queue"))
   (println "Connection to the queue confirmed")))



(defn add-random-data
  "Call this with a number of records to generate a bunch of Long message types with random int content"
  [num-of-records-to-generate]
  (db/add-batch-messages db/config {:messages (into [] (repeatedly num-of-records-to-generate #(vector (rand-int 10) "Long")))}))



(comment
  "Used to help create and manage and test database data easily"

  (peek :limit 99)

  (push ["message 1"
         1
         "message 1" "message 1" "message 1" "message 1"
         "message 2" "message 2"
         "message 3" "message 3"
         {:breaking "thing"}
         ["another break"]])


  (pop 2 :message-type "class java.lang.String" :limit 1)

  (queue-length :with-hidden? true)

  (db/get-all-messages db/config)

  (confirm "message 1")

  ; Fun batch insert
  (db/add-batch-messages db/config {:messages (into [] (repeatedly 878 #(vector (rand-int 10) "Long")))}))

