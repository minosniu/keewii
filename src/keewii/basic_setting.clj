(ns keewii.basic_setting)

(def Server-name "Minos-KangWoo-Keewii") ;server name
(def Consonant-list ["m" "w" "y"]) ;add more consonants that 'sequence.clj' has.
(def F0 100)
(def F1 {:freq 750.0 :Amp 6.0 :BW 50.0})
(def F2 {:freq 1150.0 :Amp 5.6 :BW 70.0})
(def F3 {:freq 2600.0 :Amp 5.2 :BW 110.0})

(def F1range [200 900])
(def F2range [500 2600])

;sigle slider part
(def vowel-order-f1 [750.0 280.0 590 290.0])
(def vowel-order-f2 [1000.0 2250.0 880 680.0]);a-i-ɔ-u, you can manually change.

;Formants of vowels
;u:290, 680 or 290, 700 
;o: 370 730 2890
;eo (but) 590 880 2540
;a: 750,1000,2890 or 710, 1100 2540 or 750,1000,2300
;e: 530, 1840 or 550 1770 2490 or 690f 1660 2490
;i: 280, 2250 2890
;w: 300 1320 2480