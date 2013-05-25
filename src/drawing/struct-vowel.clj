(ns drawing.struct-vowel)

;basic vowels
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "a" 750 1000))
(def E (struct vowel "e" 530 1840))
(def I (struct vowel "i" 280 2250))
(def O (struct vowel "o" 370 730))
(def U (struct vowel "u" 290 680))
(def S (struct vowel "Test will start soon" 500 1800))
;(:ind vowel)
(def alphabet (atom S))
