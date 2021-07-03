(ns s71-challenge.queue.actions
  (:require [s71-challenge.db :as db]
            [clojure.java.jdbc :as j]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout to-chan]]))

(import java.sql.SQLException)

(comment
  "Used to help create and manage database data easily"


  (db/delete-table db/config)
  (db/create-KrayzelKueue-table db/config)
  (db/add-message db/config {:message_content "Message 7"
                             :message_type (str (type "Message 7"))})
  (db/get-hidden-messages db/config)

  (let [first-id (:id (first (db/get-hidden-messages db/config)))]
    first-id)
  (db/get-messages db/config)

  (db/update-message-status db/config {:status "hidden"
                                       :id 1})

  (db/confirm-message db/config {:id 2}))


; The challenge objective is to create a FiFo multi-queue with messages stored in a MySQL database, with an implementation
; that is independent of test data or use case.
; 
; In addition your application should...
; 1. Fill in the logic for all of the stubbed functions modifying the namespace as needed to develop the queue application.
; 2. Submit a SQL file that includes the table structure and any other data needed for your application.
; 3. Add additional functionality as needed so that the namspace can be run from lein.
; 4. While not required, any notes on the development or optimizations you built in to your application would be a plus.
; 
; If you need access to a MySQL database we can provide credentials upon request.
; 
; Your completed files can be submitted as a zip file, GitHub repo, or GitHub gist. 

(defn push
  "Pushes the given messages to the queue.
   Returns a list of booleans indicating whether or not each message
   was successfully added to the queue."
  [messages]
  ; Iterates over the messages collection by attempting to add the message to the queue
  ; If a SQLException is thrown, the result for the returned list is False
  ; Otherwise, a generated_key hash-map will be returned to indicate successfully adding  
  (def push-atom (atom {}))


  ; Creates a hash-map of all the messages needed to be added
  ; The keys are used for sorting so that the returned list of booleans is in proper order
  ; If only a single message is being sent, put it as a value in a hash-map with only one (:0) key 
  (if (coll? messages)
    (reset! push-atom (zipmap (map #(keyword (str %))
                                   (range (count messages)))
                              messages))
    (reset! push-atom (hash-map :0 messages)))

  (let [c (chan)]
    (go
      (while true
        (let [ch-item (<! c)]
          (let [k (first (keys ch-item))
                v (first (vals ch-item))]
            ; Makes the SQL call and updates the atom with a true if the message was successfully added
            ; Or false if a SQLException was caught
            ; :generated_key is the response from MySQL, returning the ID
            (swap! push-atom assoc k (contains?
                                      (try
                                        (db/add-message db/config {:message_content v
                                                                   :message_type (str (type v))})
                                        (catch SQLException _e))
                                      :generated_key))))))


    ; Creates a smaller new hash-map for consumption in the take function
    (doseq [[k v] @push-atom]
      (go (>! c (hash-map k v)))))

  ; Loops until all values have been updated with booleans
  (while (not-every? boolean? (vals @push-atom)))
  (vals (sort @push-atom)))


(defn peek
  "Returns one or more messages from the queue.
   Messages are still visible after this call is made.
   Optional keyword args:
     message-type - filters for messages of the given type
     limit - returns the given number of messages (default: 1)"
  [& {:keys [message-type limit]
      :or {message-type "%"
           limit 1}}]
  (db/get-messages db/config {:message_type message-type
                              :limit_num limit}))




(defn confirm
  "Deletes the given messages from the queue.
   This function should be called to confirm the successful handling
   of messages returned by the pop function."
  [id]
  (db/confirm-message db/config {:id id}))


;  Update SQL Query to a single transaction to avoid read before writing
(defn pop
  "Returns one or more messages from the queue.
   Messages are hidden for the duration (in sec) specified by the
   required ttl arg, after which they return to the front of the queue.
   Optional keyword args:
     message-type - filters for messages of the given type
     limit - returns the given number of messages (default: 1)"
  [ttl & {:keys [message-type limit]
          :or {message-type "%"
               limit 1}}]

    ; Second, iterate through that map using the ids extracted to update the MySQL database
  (map #(if (= 1 (db/update-message-status db/config
                                           {:id %
                                            :status "working"}))

            ; If the status was sucessful
          (confirm %)
          (db/update-message-status db/config
                                    {:id %
                                     :status "error"})))
         ; First, build a new map of all the ids to loop through by using the peek method
  (map :id (peek :message-type message-type
                 :limit limit)))



(defn queue-length
  "Returns a count of the number of messages on the queue.
   Optional keyword args:
     message-type - filters for message of the given type
     with-hidden? - if truthy, includes messages that have been
                    popped but not confirmed"
  [& {:keys [message-type with-hidden?]}]
  ;; TODO implement this function
  (if with-hidden?
    (count (db/get-hidden-messages db/config))
    (count (db/get-messages db/config {:message_type "%"
                                       :limit_num 99999999999}))))
