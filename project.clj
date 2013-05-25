(defproject keewii "0.1.0-SNAPSHOT"
  :description "A real-time speech synthesizier"
  :url "http://keewii.github.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 ;[keewii "0.1.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [aleph "0.3.0-beta15"] ;udp
                 [lamina "0.5.0-rc1"] ;udp
                 [overtone "0.7.1"]]
  :compile-path "target/classes"
  :source-paths      ["src/" "src/keewii"]
  :java-source-paths ["src/java"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :native-path "native"
  :aot [keewii.core]
  :main keewii.core )
