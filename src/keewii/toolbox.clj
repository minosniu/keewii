(ns keewii.toolbox
  (:use [overtone.live]
        [keewii.basic_setting]))

(def vowel (ref {:t 100 :fa 750.0 :fb 1000.0 :fc 2600.0 :vol 1.0}))

;server
(def server (osc-server 44100 Server-name))
(zero-conf-on)
;(osc-listen server (fn [msg] (println msg)) :debug); listener connection
;(osc-rm-listener server :debug) ; remove listener

;define static formants - for vowel
(definst f1 [freq (F1 :freq) Amp (F1 :Amp) BW (F1 :BW)] (* Amp (bpf (saw F0) freq (/ BW freq))))
(definst f2 [freq (F2 :freq) Amp (F2 :Amp) BW (F2 :BW)] (* Amp (bpf (saw F0) freq (/ BW freq))))
(definst f3 [freq (F3 :freq) Amp (F3 :Amp) BW (F3 :BW)] (* Amp (bpf (saw F0) freq (/ BW freq))))

;define dynamic formants - for consonant and changing vowels
(definst f1-dyn [start-freq 200.0 end-freq 850.0 dur 0.02 Amp (F1 :Amp) BW (F1 :BW)] 
  (* Amp (bpf (saw F0) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f2-dyn [start-freq 500.0 end-freq 2600.0 dur 0.02 Amp (F2 :Amp) BW (F2 :BW)] 
  (* Amp (bpf (saw F0) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f3-dyn [start-freq 2000.0 end-freq 3500.0 dur 0.02 Amp (F3 :Amp) BW (F3 :BW)] 
  (* Amp (bpf (saw F0) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))

;Basic functions
(defn str2int [s]
  (Integer. (re-find  #"\d+" s )))
(defn button_re-sort [NUM]
  "789456123 to 123456789 (flip upside down)"
  (cond
    (< NUM 3) (str (+ NUM 7))
    (< NUM 6) (str (+ NUM 1))
    (< NUM 9) (str (- NUM 5))))
(defn zip [& colls]
  (into [] (apply map vector colls)))
(defn make-sound [t_ref [{t_init :t, fa1 :fa, fb1 :fb, fc1 :fc vol1 :vol} {t_end :t, fa2 :fa, fb2 :fb, fc2 :fc vol2 :vol}]]
  (let [t_dur (- t_end t_init); ms->s change
        t_dur_fcn (* 0.001 (- t_end t_init))] ;ms->s change
    (if (zero? t_init)
      (at (+ t_init t_ref) (doseq[]  (f1-dyn fa1 fa2 t_dur_fcn vol1) (f2-dyn fb1 fb2 t_dur_fcn vol1) (f3-dyn fc1 fc2 t_dur_fcn vol1) ))
      (at (+ t_init t_ref) (doseq[] (kill f1-dyn f2-dyn f3-dyn) (f1-dyn fa1 fa2 t_dur_fcn vol2)
                             (f2-dyn fb1 fb2 t_dur_fcn vol2) (f3-dyn fc1 fc2 t_dur_fcn vol2))))))
(defn vowel-xy
  [fa fb val]
  "xy plot for vowel-mapping"
  (let [x_freq (+ (* -1.0 (first val)) 1.0)
        y_freq (+ (* -1.0 (second val)) 1.0)
        newfreq_a (scale-range x_freq 0 1 (F1range 0) (F1range 1))
        newfreq_b (scale-range y_freq 0 1 (F2range 0) (F2range 1))]
  (doseq [] 
    (ctl fa :freq newfreq_a)
    (ctl fb :freq newfreq_b))))
(defn make-vowel [vowel]
  (doseq[] (f1 (vowel :fa))(f2 (vowel :fb))(f3 (vowel :fc))))
(defn enable-play [val] 
  "play basic f1,f2,f3"
  (if (= val 1.0) (doseq [] (f1 750)(f2 1000)(f3 2890))
    (stop)))
(defn enable-vowel [val]
  "play basic f1,f2,f3 and stop"
  (if (= val 1.0) (doseq [] (f1 (vowel :fa))(f2 (vowel :fb))(f3 (vowel :fc)))(stop)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Double slider control of vowel
(defn loud-alter-vowel-f1
  [f val] 
  (let [newval (scale-range val 0 1 (F1range 0) (F1range 1))]
    (ctl f :freq newval)
    (dosync (ref-set vowel (merge @vowel {:fa newval}))) ))
(defn loud-alter-vowel-f2
  [f val] 
  (let [newval (scale-range val 0 1 (F2range 0) (F2range 1))]
    (ctl f :freq newval)
    (dosync (ref-set vowel (merge @vowel {:fb newval})))))
(defn silent-alter-vowel-f1 
  [vowel val]  
  (dosync (ref-set vowel (merge @vowel {:fa (scale-range val 0 1 (F1range 0) (F1range 1))}))))
(defn silent-alter-vowel-f2 
  [vowel val]   
  (dosync (ref-set vowel (merge @vowel {:fb (scale-range val 0 1 (F2range 0) (F2range 1))}))))

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
;slider connection after consonant
(defn two-slider-connection []
  (doseq[]
    (osc-handle server "/1/fader1" (fn [msg] (silent-alter-vowel-f1 vowel (first (:args msg)))))
    (osc-handle server "/1/fader2" (fn [msg] (silent-alter-vowel-f2 vowel (first (:args msg)))))(stop)))
;consonant functions
(defn Enable-Consonant [val Consonant]
  "consonant sound + vowel"
  (if (= val 1.0) (speak (Consonant @vowel)) (two-slider-connection)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;single slider control of vowel
(defn make-f [f-list]
  "this function makes vowel transition using minimum jerk trajectory"
  (let [l (count f-list)
        p-nodes (into [] (map #(/ % (dec l)) (range l)))
        pf-map (zipmap p-nodes f-list)
        slist (zip (drop-last p-nodes) (rest p-nodes))]
    (fn [p] 
      (first 
        (for [[lo-p hi-p] slist
              :when (<= lo-p p hi-p)]
          (let [xi (pf-map lo-p)
                xf (pf-map hi-p)
                p (* (- p lo-p) (dec l))] 
            (+ xi (* (- xi xf) (- (* 15.0 (* p p p p)) (* 6.0 (* p p p p p)) (* 10.0 (* p p p)) )))))))))
(def y-f1 (make-f vowel-order-f1))
(def y-f2 (make-f vowel-order-f2))
(defn y [p] (vector (y-f1 p) (y-f2 p))) 
(defn alter-vowel 
  [val]  
  (let [[freq-f1 freq-f2] (y val)]
    (doseq [](ctl f1 :freq freq-f1) (ctl f2 :freq freq-f2)
      (dosync (ref-set vowel (merge @vowel {:fa freq-f1 :fb freq-f2}))))))
(defn speak-single [syllable]
  (let [time (now)
        slist (zip (take (dec (count syllable)) syllable) (rest syllable))]
    (dorun (map #(make-sound time %) slist))
    (at (+ 100 time)  
        (doseq[]  
          (make-vowel @vowel)
          (osc-handle server "/1/fader1" (fn [msg] (println msg)(alter-vowel (first (:args msg)))))
          (kill f1-dyn f2-dyn f3-dyn)))))
;slider connection after consonant
(defn one-slider-connection []
  (doseq[]
    (osc-handle server "/1/fader1" (fn [msg] (alter-vowel vowel (first (:args msg))))) (stop)))
;consonant functions
(defn Enable-Consonant-single [val Consonant]
  "consonant sound + vowel"
  (if (= val 1.0) (speak-single (Consonant @vowel)) (one-slider-connection)))
