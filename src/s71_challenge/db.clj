(ns s71-challenge.db
  (:require [hugsql.core :as hugsql]))


(def config {:dbtype "mysql"
              :dbname "MWej27axH8"
              :host "s71-backend-dev-test.cklodqxnwcql.us-east-1.rds.amazonaws.com"
              :port "3306"
              :user "jeremiah"
              :password "CWjYgbn5g4m4g5h"
              :enabledTLSProtocols "TLSv1.2"})



(hugsql/def-db-fns "queries.sql")


; Scratchpad for comparing times

;; (defn now [] (new java.util.Date))


;; (defn calc-time []
;;   (let [time1 (now)]
;;     (Thread/sleep 500)
;;     (- (inst-ms (now)) (inst-ms time1))))

;; (calc-time)