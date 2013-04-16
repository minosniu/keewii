(ns keewii.utils
  (:use [keewii.server] 
        [keewii.formant]
        [keewii.sequence]
        [overtone.live]))
(def vowel (ref {:t 100 :fa 750.0 :fb 1000.0 :fc 2600.0 :vol 1.0}))
 
(defn make-vowel [vowel]
  (doseq[] (f1 (vowel :fa))(f2 (vowel :fb))(f3 (vowel :fc))))

(defn loud-alter-vowel-f1
  [f val] 
  (let [newval (scale-range val 0 1 100 1000)]
    (ctl f :freq newval)
    (dosync (ref-set vowel (merge @vowel {:fa newval}))) ))

(defn loud-alter-vowel-f2
  [f val] 
  (let [newval (scale-range val 0 1 500 3000)]
    (ctl f :freq newval)
    (dosync (ref-set vowel (merge @vowel {:fb newval})))))

(defn silent-alter-vowel-f1 
  [vowel val]  
  (dosync (ref-set vowel (merge @vowel {:fa (scale-range val 0 1 100 1000)}))))

(defn silent-alter-vowel-f2 
  [vowel val]   
  (dosync (ref-set vowel (merge @vowel {:fb (scale-range val 0 1 500 3000)}))))

(defn zip [& colls]
  (into [] (apply map vector colls)))

(defn make-sound [t_ref [{t_init :t, fa1 :fa, fb1 :fb, fc1 :fc vol1 :vol} {t_end :t, fa2 :fa, fb2 :fb, fc2 :fc vol2 :vol}]]
  (let [t_dur (- t_end t_init); ms->s change
        t_dur_fcn (* 0.001 (- t_end t_init))] ;ms->s change
    (if (zero? t_init)
      (at (+ t_init t_ref) (doseq[]  (f1-dyn fa1 fa2 t_dur_fcn vol1) (f2-dyn fb1 fb2 t_dur_fcn vol1) (f3-dyn fc1 fc2 t_dur_fcn vol1) ))
      (at (+ t_init t_ref) (doseq[] (kill f1-dyn f2-dyn f3-dyn) (f1-dyn fa1 fa2 t_dur_fcn vol2)
                             (f2-dyn fb1 fb2 t_dur_fcn vol2) (f3-dyn fc1 fc2 t_dur_fcn vol2))))))

(defn speak [syllable]
  (let [time (now)
        slist (zip (take (dec (count syllable)) syllable) (rest syllable))]
    (dorun (map #(make-sound time %) slist))
    (at (+ 100 time)  
        (doseq[]  
          (make-vowel @vowel)
          (osc-handle server "/1/fader1" (fn [msg] (loud-alter-vowel-f1 f1 (first (:args msg)))))
          (osc-handle server "/1/fader2" (fn [msg] (loud-alter-vowel-f2 f2 (first (:args msg)))))
          (kill f1-dyn f2-dyn f3-dyn)))))
