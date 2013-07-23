;2-control vowel, m,y,w in 1st tab
; xy-plot in 4th tab
(ns keewii.core
  (:use [overtone.live]
        [keewii.toolbox]
        [keewii.sequence]
        [keewii.basic_setting]))
;Sliders for vowel (1st tab)
(dosync
 (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
 (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg))))))

;Buttons for consonant (1st tab)
(doseq [i (range (.length Consonant-list))]
  (let [CONSONANT (str2con_conv (Consonant-list i))
        BUTTON (str "/1/push" (button_re-sort i))]
    (osc-handle server BUTTON (fn [msg] (Enable-Consonant (first (:args msg)) CONSONANT)))))
;stop button (1st tab)
(osc-handle server "/1/push10" (fn [msg] (enable-vowel (first (:args msg)))));vowel only
(osc-handle server "/1/push11" (fn [msg] (enable-play (first (:args msg)))));play
(osc-handle server "/1/push12" (fn [msg] (stop)));stop

;xy plot in 4th tab
(osc-handle server "/4/xy" (fn [msg](println msg) (vowel-xy f1 f2 (seq (:args msg)))))
(osc-handle server "/4/toggle5" (fn [msg](println msg) (enable-play (first (:args msg))))) ;play&stop toggle

;(osc-close client)
;(osc-close server) 
;(stop)
