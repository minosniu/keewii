(ns drawing.toolbox
  (:use  [drawing.basic_vars]) 
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [drawing.struct_vowel :as struct_vowel])
  (:import (java.awt.event ActionListener KeyListener)
           (java.util Date)
           (java.text SimpleDateFormat)))

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
  (let [SHIFT 0] ;move 25pixels with font 100, but cursor is also moved.
    (if (= ind 1)
    (int (+ (/ (* Scale-factor (- (VOWEL :f1)  (formant_lim 0)))  (- (formant_lim 1) (formant_lim 0))) SHIFT)) ;200 600 ->0 1000 (0~1000), 
    (int (- (/ (* Scale-factor (- (formant_lim 3) (VOWEL :f2))) (- (formant_lim 3) (formant_lim 2))) SHIFT)) ))); 2600 2100 -> 3000 3000 (0~3000)
 
 (defn parse-int [s]
   "str2int"
   (Integer. (re-find  #"\d+" s )))
 
 ;file functions
(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))
(defn mkdir [path]
  (.mkdirs (io/file path)))

(defn input-listener []
  "key-listener for pause, and future use of others" 
    (proxy [ActionListener KeyListener] []
      (actionPerformed [e])
      (keyPressed [e] (if (= (str (.getKeyChar e)) "p") 
                        (doseq [] (println "Pause: " @running) (swap! running not)))) ;pause
  (keyReleased [e])
  (keyTyped [e])))

(defn matcher [CH]
  "This function converts char to vowel structure"
  (case CH
    "\u0251" struct_vowel/A
    "\u025B" struct_vowel/E
    "i" struct_vowel/I
    "\u0254" struct_vowel/O
    "u" struct_vowel/U))
