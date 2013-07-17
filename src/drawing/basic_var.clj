(ns drawing.basic_vars
  (:use [overtone.at-at :only (now)]))

;log data in logfile
(def TIME (atom (now)))
(def F1 (atom 210)) 
(def F2 (atom 510))
(def F3 (atom 2890)) 
(def EMG1 (atom 0.0))
(def EMG2 (atom 0.0))
;Formant limitation
(def formant_lim [0 900 0 3000]);f1_min f1_max f2_min f2_max 
;thread running?
(def running (atom true))

;pause 
(def PAUSE (atom false))
(def CUR_TGT_sleep (atom true)) ;It hides cursor and target during interval

;canvas size
(def dim [1050 600]) ;frequency domain: 200-900,500-2600
(def animation-sleep-ms 16)

;GUI subject setting
;experimental option. true-polar, false-cartesian
(def start-trial (atom 1))
(def options (atom true)) 
(def POLAR_COORDINATES (atom false)) 
