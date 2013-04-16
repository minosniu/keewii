(defproject keewii "0.1.0-SNAPSHOT"
  :description "A real-time speech synthesizier"
  :url "http://keewii.github.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [overtone "0.7.1"]]
  :compile-path "target/classes"
  :source-paths      ["src/keewii"]
  :java-source-paths ["src/java"]
  :aot [keewii.core]
  :main keewii.core)
