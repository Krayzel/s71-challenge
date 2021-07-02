(ns s71-challenge.db
  (:require [clojure.java.jdbc :as j]))


(def db-conn {:dbtype "mysql"
              :dbname "MWej27axH8"
              :host "s71-backend-dev-test.cklodqxnwcql.us-east-1.rds.amazonaws.com"
              :port "3306"
              :user "jeremiah"
              :password "CWjYgbn5g4m4g5h"
              :enabledTLSProtocols "TLSv1.2"})


(defn s
  []
  (j/query db-conn ["SHOW FULL TABLES;"]))
