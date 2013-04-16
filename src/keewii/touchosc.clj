(ns keewii.touchosc
  (:use [overtone.live]
        [keewii.formant]
        [keewii.utils]
        [keewii.sequence]))

(defn enable-m
  [val]
  (if (= val 1.0) (doseq [] (speak (m1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    ))
(defn enable-w
  [val]
  (if (= val 1.0) (doseq [] (speak (w1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    ))
(defn enable-y
  [val]
  (if (= val 1.0) (doseq [] (speak (y1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    ))
(defn enable-b
  [val]
  (if (= val 1.0) (doseq [] (speak (b1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    ))
(defn enable-l
  [val]
  (if (= val 1.0) (doseq [] (speak (l1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    ))
(defn enable-d
  [val]
  (if (= val 1.0) (doseq [] (speak (d1 @vowel))) 
    (doseq[]
      (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
      (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop))
    )) 

(defn vowel-xy
  [fa fb val]
  "xy plot for vowel-mapping"
  (let [x_freq (+ (* -1.0 (first val)) 1.0)
        y_freq (+ (* -1.0 (second val)) 1.0)
        newfreq_a (scale-range x_freq 0 1 200 850)
        newfreq_b (scale-range y_freq 0 1 500 2500)]
    (doseq [] 
      (ctl fa :freq newfreq_a)
      (ctl fb :freq newfreq_b)
      (println newfreq_a newfreq_b))))

(defn enable-play ; play basic f1,f2,f3
  [val]
  (if (= val 1.0) (doseq [] (f1 750)(f2 1000)(f3 2890))
    (stop)))
