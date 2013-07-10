(ns drawing.struct_vowel
  (:require [clojure.string :as string]))
;basic vowels
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "a" 750 1000));750/1000
(def E (struct vowel "e" 530 1840));530/1840
(def I (struct vowel "i" 290 2350));280/2250
(def O (struct vowel "o" 440 650)) ;370/730
(def U (struct vowel "u" 220 500));290/680
(def S (struct vowel "Test will \nstart soon" 500 1900))
;(:ind vowel)
(def alphabet (atom S))

;(shuffle  (reduce into (map #(repeat 5 %) ["u" "o" "i" "a" "e"]))  )
(defn matcher [CH]
  "This function converts char to vowel structure"
  (case CH
    "a" A
    "e" E
    "i" I
    "o" O
    "u" U))
