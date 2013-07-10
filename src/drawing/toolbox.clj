(ns drawing.toolbox
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(import 'java.util.Date)
(import java.text.SimpleDateFormat)

;Formant limitation
(def formant_lim [200 900 500 2600]);f1_min f1_max f2_min f2_max 
;Date
 (defn date
    ([](Date.))
    ([systime](Date. systime)))
 (defn format-date
    ([](format-date (date) "yyyy MM dd HH mm ss"))
    ([x](if (string? x)
            (format-date (date) x)
            (format-date x "yyyy MM dd HH mm ss")))
    ([dt fmt](.format (SimpleDateFormat. fmt) dt)))
 (defn get-date [] (string/replace (format-date) " " ""))
 
 ;Name input
 (defn get-name []
   (let [reader (java.io.BufferedReader. *in*)
         ln (.readLine reader)] ln))
 
 (defn formant2pixel [VOWEL Scale-factor ind]
  "resize the number of formants to fit into the screen by scale factor from the screen size"
  (if (= ind 1)
    ;move 25pixels with font 100
    (int (+ (/ (* Scale-factor (- (VOWEL :f1)  (formant_lim 0)))  (- (formant_lim 1) (formant_lim 0))) 25)) ;200 600 ->0 1000 (0~1000), 
    (int (- (/ (* Scale-factor (- (formant_lim 3) (VOWEL :f2))) (- (formant_lim 3) (formant_lim 2))) 25)) )); 2600 2100 -> 3000 3000 (0~3000)
 
 ;file functions
(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))
(defn mkdir [path]
  (.mkdirs (io/file path)))


  
