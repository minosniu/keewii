(ns drawing.core
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

(defn formant2pixel [VOWEL ind]
  "resize the number of formants to fit into the screen"
  (if (= ind 1)
    (- (VOWEL :f1) 200)
    (- 1150 (/ (VOWEL :f2) 2))))
(defn render [^Graphics g]
  (let [img (BufferedImage. (first dim) (last dim) BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)
        y (- @F1 200) 
        x (- 1150 (/ @F2 2))
        VOWEL  @alphabet
        x_pos (formant2pixel VOWEL 2)
        y_pos (formant2pixel VOWEL 1)
        CHAR (VOWEL :name)]
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
      (.drawString CHAR x_pos y_pos ))
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
          filename (str "data\\" NAME DATE @session_number ".txt")
          VOWEL (:name @alphabet)
          VOWEL-f1 (:f1 @alphabet)
          VOWEL-f2 (:f1 @alphabet)]
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

(def trial-duration 5000) ; in ms
(def between-trial 2000) ;in ms
(def total-trials 20)

(dotimes [i total-trials]
  (dosync 
    (reset! TIME (now))
    (. (Runtime/getRuntime) exec "notepad.exe")
    (reset! alphabet (rand-nth [A E I O U]))
    (reset! session_number (+ 1 @session_number))
    (. Thread (sleep trial-duration))
    (. (Runtime/getRuntime) exec "taskkill /F /IM notepad.exe")
    (. Thread (sleep between-trial))))
(System/exit 0)

;(recording-start "awesome.wav") ;overtone lib
;(recording-stop)
