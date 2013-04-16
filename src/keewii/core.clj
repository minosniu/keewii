(ns keewii.core
  (:use [overtone.live]
        [keewii.formant]
        [keewii.utils]
        [keewii.server]
        [keewii.sequence]
        [keewii.touchosc])
  (:gen-class))
(defn -main [& args]
  (zero-conf-on)
  (dosync
    (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
    (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg))))))
  (osc-handle server "/1/push7" (fn [msg] (enable-m (first (:args msg)))));m + vowel
  (osc-handle server "/1/push8" (fn [msg] (enable-w (first (:args msg)))));w + vowel
  (osc-handle server "/1/push9" (fn [msg] (enable-y (first (:args msg)))));y + vowel
  (osc-handle server "/1/push4" (fn [msg] (enable-b (first (:args msg)))));b + vowel
  (osc-handle server "/1/push5" (fn [msg] (enable-l (first (:args msg)))));l + vowel
  (osc-handle server "/1/push6" (fn [msg] (enable-d (first (:args msg)))));d + vowel
  (osc-handle server "/1/push12" (fn [msg] (stop)));stop
  (osc-handle server "/4/xy" (fn [msg] (vowel-xy f1 f2 (seq (:args msg)))))
  (osc-handle server "/4/toggle5" (fn [msg] (enable-play (first (:args msg)))))
  ;(osc-listen server (fn [msg] (println msg)) :debug);listener connection
  ;(osc-rm-listener server :debug) ; remove listener
  ;(osc-close client)
  ;(osc-close server) 
  )
