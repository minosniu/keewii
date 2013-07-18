(ns drawing.struct_vowel)
;basic vowels
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "\u0251" 710 1100));710/1100, ɑ
(def E (struct vowel "\u025B" 550 1770));550/1770, ɛ
(def I (struct vowel "i" 280 2250));280/2250
(def O (struct vowel "\u0254" 590 880)) ;590/880, ɔ
(def U (struct vowel "u" 310 870));310/870

(def S (struct vowel "Test will start soon" 430 2150))
(def R (struct vowel "Relax" 430 1750))

;temp memory
(def alphabet (atom S))

