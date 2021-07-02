(ns s71-challenge.queue.app)





;; ;; We'll use `conj` to add to the structure
;; ;; And then `seq` to look at the queues contents

;; (def numbers-in (queue [1 2 3 4 5]))
;; (seq numbers-in)
;; ;;=> (1 2 3 4 5)

;; ;; We can use `pop` and `peek` as expected


;; (seq (conj numbers-in "a"))


;; (peek numbers-in)
;; ;;=> 1

;; (seq (pop numbers-in))
;; ;;=> (2 3 4 5)

;; ;; And `empty?` can be used to check if the queue is empty.

;; (empty? (drop 5 numbers-in))
;; ;;=> false

;; (empty? (queue))
;; ;;=> true





;; (use 'clojure.contrib.sql)


;; (comment
  
  
;;   (def db {:classname "com.mysql.jdbc.Driver"
;;            :subprotocol "mysql"
;;            :subname "//localhost:3306/clojure_test"
;;            :user "root"
;;            :password "root"})

;;   (with-connection db
;;     (with-query-results rs ["select *"]))
  
;;   )



;; (def mysql-db {:dbtype "mysql"
;;                :dbname "realparsmodel"
;;                :user "root"
;;                :password "password"})



;; (comment

;;   (queue/initialize! db-conn)



;;   (sql/query mysql-db ["select *"])




;;   (println (sql/query mysql-db
;;                       ["select table_name from tables"])))