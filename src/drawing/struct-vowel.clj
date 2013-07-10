(ns drawing.struct_vowel
  (:require [clojure.string :as string]))
;basic vowels
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "a" 710 1100));710/1100
(def E (struct vowel "e" 550 1770));550/1770
(def I (struct vowel "i" 280 2250));280/2250
(def O (struct vowel "o" 590 880)) ;590/880
(def U (struct vowel "u" 310 870));310/870
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
