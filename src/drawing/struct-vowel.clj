(ns drawing.struct_vowel)
;basic vowels
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "a" 710 1100));710/1100
(def E (struct vowel "e" 550 1770));550/1770
(def I (struct vowel "i" 280 2250));280/2250
(def O (struct vowel "o" 590 880)) ;590/880
(def U (struct vowel "u" 310 870));310/870
(def S (struct vowel "Test will \nstart soon" 500 1900))

;temp memory
(def alphabet (atom S))

