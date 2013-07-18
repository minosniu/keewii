(ns drawing.basic_vars
  (:import (java.awt Font)))

"Custom variables: Can be changed"
;canvas size
(def dim [1050 600]) ;dimension size only when we restore down the window
(def animation-sleep-ms 16)
;Formant limitation
(def formant_lim [0 900 0 3000]);f1_min f1_max f2_min f2_max 
;trial setting
(def trial-duration 6000) ; in ms
(def between-trial 5000) ;in ms
(def total-trials 25) ;25 trials
;temp folder 
(def Temp_data "C:\\Code\\keewii1\\temp\\") 
;font
(def font (new Font "Arial unicode MS" Font/PLAIN 100)) ;only with Unicode


"Do not change this below"
(defn now
  "Return the current time in ms (from overtone-at-at lib)"
  [] (System/currentTimeMillis))
;log data in logfile
(def TIME (atom (now)))
(def F1 (atom 210)) 
(def F2 (atom 510))
(def F3 (atom 2890)) 
(def EMG1 (atom 0.0))
(def EMG2 (atom 0.0))
;thread running?
(def running (atom true))
;pause 
(def PAUSE (atom false))
(def CUR_TGT_sleep (atom true)) ;It hides cursor and target during interval
;GUI subject setting
;experimental option. true-polar, false-cartesian
(def start-trial (atom 1))
(def options (atom true)) 
(def POLAR_COORDINATES (atom false)) 
;trial session number
(def session_number (atom 0)); don't change this manually
