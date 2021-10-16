(ns s71-challenge.queue.actions
  (:require [s71-challenge.db :as db]
            [clojure.core.async
             :as a
             :refer [>! <! go chan go-loop]]))

(import java.sql.SQLException)


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
  (let [push-atom (atom {})]


  ; Creates a hash-map of all the messages needed to be added
  ; The keys are used for sorting so that the returned list of booleans is in proper order
  ; If only a single message is being sent, put it as a value in a hash-map with only one (:0) key
 ; We want to nest the hash-map in another hashmap due to how destructuring from the channel is 
  (if (and (coll? messages) (not (= (type messages) clojure.lang.PersistentArrayMap)))
    (reset! push-atom (into (sorted-map-by <)
                            (zipmap (map #(int %)
                                         (range (count messages)))
                                    messages)))
    (reset! push-atom (hash-map :0 messages)))

  ; NOTE: Future improvement should allow for a loop to create new channels
  ; If count of messages exceeds 1024
    (let [c (chan)]
      (dotimes [_n (count @push-atom)]
        (go
          (let [ch-item (<! c)]
            (let [k (first (keys ch-item))
                  v (first (vals ch-item))]
            ; Makes the SQL call and updates the atom with a true if the message was successfully added
            ; Or false if a SQLException was caught
            ; :generated_key is the response from MySQL, returning the ID
              (swap! push-atom assoc k (contains?
                                        (try
                                          (db/add-single-message db/config {:message_content (:message-content v)
                                                                            :message_type (:message-type v)})
                                          (catch SQLException _e))
                                        :generated_key))))))


    ; Formats a new hash-map for consumption in the take function
    ; If keys were not found for message-type, use the primitve class for the type
    ; Example:
    ; {:0 {:message-content "Test Message" :message-type "class.java.lang.String"}}
      (doseq [[k v] @push-atom]
        (go (>! c (hash-map k
                            (if (= (type v) clojure.lang.PersistentArrayMap)
                              v
                              (hash-map :message-content v :message-type (str (type v)))))))))
  ; Loops until all values have been updated with booleans and returns the final list
    (while (not-every? boolean? (vals @push-atom)))
    (vals @push-atom)))



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



; NOTE FUTURE UPDATE:
; Update SQL Query to a single transaction to avoid read before writing
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

  (let [pop-chan (chan)
        messages (map :id (peek :message-type message-type
                                :limit limit))]

    ; Loops through the messages a single time and assigns asynchronous handles
    (dotimes [counter (count messages)]
      (go-loop [message-to-pop (<! pop-chan)]
        (db/update-message-status db/config
                                  {:id message-to-pop
                                   :status "working"})
        ; Sleeps the thread based on the ttl passed in
        (Thread/sleep (* ttl 1000))
        (db/update-message-status db/config
                                  {:id message-to-pop
                                   :status "complete"}))

      (go
        (>! pop-chan (nth messages counter))))
    (str "Number of messages popping: " (count messages))))


(defn confirm
  "Deletes the given messages from the queue.
   This function should be called to confirm the successful handling
   of messages returned by the pop function."
  [messages]
  (let [con-chan (chan)
        messages (if (coll? messages) (into #{} messages) [messages])]
    (dotimes [counter (count messages)]
    ; When a message is taken, call confirm-message to delete similar messages that are completed
      (go
        (db/confirm-message db/config
                            {:message_content (<! con-chan)}))

    ; Checks to see if messages was a collection
    ; If it is a collection, make messages into a unique set
    ; If a single message was passed, put into a vector for doseq
      (go
        (>! con-chan (nth messages counter))))
    (str "Confirmed all complete messages with the text: " messages)))



(defn queue-length
  "Returns a count of the number of messages on the queue.
   Optional keyword args:
     message-type - filters for message of the given type
     with-hidden? - if truthy, includes messages that have been
                    popped but not confirmed"
  [& {:keys [message-type with-hidden?]
      :or {message-type "%"}}]
  (if with-hidden?
    (count (db/get-all-messages db/config))
    (count (db/get-messages db/config {:message_type message-type}))))
