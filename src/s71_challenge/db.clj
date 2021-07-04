(ns s71-challenge.db
  (:require [hugsql.core :as hugsql]))


(def config {:dbtype "mysql"
             :dbname "MWej27axH8"
             :host "s71-backend-dev-test.cklodqxnwcql.us-east-1.rds.amazonaws.com"
             :port "3306"
             :user "jeremiah"
             :password "CWjYgbn5g4m4g5h"
             :enabledTLSProtocols "TLSv1.2"
             :useSSL false})



(hugsql/def-db-fns "queries.sql")
