(ns drawing.toolbox
  (:require [clojure.string :as string]
            ;[overtone.live :as live]
            )
  ;(:use ;[clojure.contrib.math])
      
  )
(import 'java.util.Date)
(import java.text.SimpleDateFormat)

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
    (int (/ (* Scale-factor (- (VOWEL :f1)  200))  600))
    (int (/ (* Scale-factor (- 2300 (VOWEL :f2))) 1800))))

 
