(ns drawing.free-exploration
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:use [lamina.core] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp]
        [drawing.toolbox]
        [drawing.struct-vowel]
        [overtone.at-at]
        [clojure.contrib.math]
        [drawing.udp])
  (:import (java.awt Color Graphics Dimension GridLayout Font FontMetrics)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame JLabel JTextField)
           (java.lang.Runtime)))

;log data in logfile
(def TIME (atom (now)))
(def F1 (atom 210)) 
(def F2 (atom 510))
(def EMG1 (atom 0.0))
(def EMG2 (atom 0.0))

;filename info
(defonce NAME (get-name))
(defonce DATE (get-date))
(def session_number (atom 0))

;canvas size
(def dim [900 600]) ;frequency domain: 200-800,500-2300
(def animation-sleep-ms 16)
(def running true)
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 44))

(defn render [^Graphics g]
  (let [img (BufferedImage. (first dim) (last dim) BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)
        y (- @F1 200) 
        x (- 1150 (/ @F2 2))]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img) (.getHeight img)))    
    (doto bg
      (.setColor Color/BLUE) ;blue square
      (.fillRect (int (- x 10) )  (int (- y 10) )  20 20));
    (doto bg
      (. setFont font)
      (.setColor Color/BLACK)
      ; x: 900-(f2-500)/2=1150-f2/2, y: f1-200
      (.drawString "a" 650 560);f2 f1 pair
      (.drawString "e" 250 360)
      (.drawString "i" 20 80)
      (.drawString "o" 780 180)
      (.drawString "u" 810 80))
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] []
                           (paint [g] (render g)))
                     (.setPreferredSize (Dimension. 
                                         (first dim)
                                         (last dim)))))

(def frame (doto (JFrame.) (.add panel) .pack .show (. setAlwaysOnTop true) (. setExtendedState JFrame/MAXIMIZED_BOTH)))

;printing thread
(def animator (agent nil))
(defn animation [x]
  (when running
    (send-off *agent* #'animation))
  (.repaint panel)
  (Thread/sleep animation-sleep-ms) nil)
(send-off animator animation)

;receving thread
(def udp-receiver (agent nil))
(defn udp-receive []
  (dosync
    (let [time  (/ (- (now) @TIME) 1000.0) ;in ms
          MSG (receive-msg)
          msg (map read-string (string/split MSG #":"))
          filename (str "data\\" NAME DATE @session_number ".txt")]
      (println MSG)
      (reset! F1 (first msg))
      (reset! F2 (second msg))
      (reset! EMG1 (second (rest msg)))
      (reset! EMG2 (last msg))         
  (if (and (and (>= @F1 100) (<= @F1 900 )) (and (>= @F2 300 ) (<= @F2 2500 )) )
        (spit filename (str time " " @EMG1 " " @EMG2 " " @F1 " " @F2 "\n")  :append true))))); vowel-showed cursor-f1 cursor-f2
      
(defn udp-reception [x]       
  (udp-receive)
  (when running
    (send-off *agent* #'udp-reception))
  nil)
(send-off udp-receiver udp-reception)

(. Thread (sleep 5000));Starting pause
(def trial-duration 20000) ; in ms

(dosync 
  (reset! TIME (now))
  (. (Runtime/getRuntime) exec "notepad.exe")
  (. Thread (sleep trial-duration))
  (. (Runtime/getRuntime) exec "taskkill /F /IM notepad.exe"))
(System/exit 0)

;(recording-start "awesome.wav") ;overtone lib
;(recording-stop)
