(ns drawing.initializing
  (:require [clojure.string :as string])
  (:use [drawing.experiment_options]
        [drawing.toolbox]
        [drawing.struct_vowel]
        [drawing.basic_vars] ) 
  (:import (java.io File)))

;Experimental options & subject info
(OPTIONS)
(while @options)

(def Condition (atom (if @POLAR_COORDINATES "Polar" "Cart"))) 
;(defonce DATE (get-date))
;(def FILENAME (str "C:\\Code\\keewii1\\data\\" NAME DATE "\\" @Condition "\\")) ;In case we need date

;File & Directory info
(def FILENAME (str "C:\\Code\\keewii1\\data\\" NAME "\\" @Condition "\\"))

;Make directories
(mkdir FILENAME)
(mkdir (str FILENAME "\\dat\\"))
(mkdir (str FILENAME "\\wav\\"))

(defn seq_check [filename]
  "This function checks if seq.txt exists" 
  (let [f (File. filename)]
    (cond
      (.isFile f)      (def randomized-sequence 
                         (into [] (map #(matcher %) (string/split (slurp (str FILENAME "seq.txt")) #"\s+"))) );"file"
      (.isDirectory f) "directory"
      (.exists f)      "other"
      :else            (doseq [] (def randomized-sequence  ;non existent case
                                   (shuffle (reduce into (map #(repeat 5 %) [A E I O U])))) 
                         (spit (str FILENAME "seq.txt") 
                               (string/join " " (reduce str (map #(% :name) randomized-sequence))))))))
