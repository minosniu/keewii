(ns keewii.formant
  (:use overtone.live))

;define static formants - for vowel
(definst f0 [freq 120.0 Amp 6.4 BW 50.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))
(definst f1 [freq 750.0 Amp 6.0 BW 50.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))
(definst f2 [freq 1150.0 Amp 5.6 BW 70.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))
(definst f3 [freq 2600.0 Amp 5.2 BW 110.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))
(definst f4 [freq 3300.0 Amp 4.8 BW 250.0 ] (* Amp (bpf (saw 100) freq (/ BW  freq))))
(definst f5 [freq 3850.0 Amp 4.4 BW 200.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))
(definst f6 [freq 4900.0 Amp 4.0 BW 1000.0 ] (* Amp (bpf (saw 100) freq (/ BW freq))))

;define dynamic formants - for consonant and changing vowels
(definst f1-dyn [start-freq 100.0 end-freq 500.0 dur 0.02 Amp 1.0 BW 50.0] (* 6.0 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f2-dyn [start-freq 1000.0 end-freq 1500.0 dur 0.02 Amp 1.0 BW 70.0] (* 5.6 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f3-dyn [start-freq 2000.0 end-freq 2500.0 dur 0.02 Amp 1.0 BW 110.0] (* 5.2 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f4-dyn [start-freq 3000.0 end-freq 3500.0 dur 0.02 Amp 1.0 BW 250.0] (* 4.8 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f5-dyn [start-freq 3500.0 end-freq 4000.0 dur 0.02 Amp 1.0 BW 200.0] (* 4.4 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
(definst f6-dyn [start-freq 4000.0 end-freq 4500.0 dur 0.02 Amp 1.0 BW 1000.0] (* 4.0 Amp (bpf (saw 100) (line start-freq end-freq dur) (/ BW (line start-freq end-freq dur)))))
