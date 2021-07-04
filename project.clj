(defproject s71-challenge "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "5.1.44"]
                 [com.layerware/hugsql "0.5.1"]
                 [org.clojure/core.async "0.4.500"]]
  :main s71-challenge.core
  :repl-options {:init-ns s71-challenge.core})
