;one slider control for vowel, a-i-eo-u in 1st tab only
(ns keewii-single-vowel.core
    (:use [overtone.live]
          [keewii.basic_setting]
          [keewii.sequence]
          [keewii.toolbox]))

;Vowel slider
(osc-handle server "/1/fader1" (fn [msg] (alter-vowel (first (:args msg)))))
;Buttons for consonant (1st tab)
(doseq [i (range (.length Consonant-list))]
  (let [CONSONANT (str2con_conv (Consonant-list i))
        BUTTON (str "/1/push" (button_re-sort i))]
    (osc-handle server BUTTON (fn [msg] (Enable-Consonant-single (first (:args msg)) CONSONANT)))))

;Other functions: vowel only, play, stop
(osc-handle server "/1/push10" (fn [msg] (enable-vowel (first (:args msg)))));vowel only
(osc-handle server "/1/push11" (fn [msg] (enable-play (first (:args msg)))));play
(osc-handle server "/1/push12" (fn [msg] (stop)));stop
;(osc-rm-handler server "/1/fader1")
;(osc-close client)
;(osc-close server) 
(stop)