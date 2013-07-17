(ns drawing.free-exploration
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:use [lamina.core] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp]
        [overtone.at-at :only (now)]
        [drawing.basic_vars] 
        [drawing.toolbox]
        [drawing.struct_vowel]
        [drawing.experiment_options] 
        [drawing.udp])
  (:import (java.awt Color Graphics Dimension GridLayout Font FontMetrics)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame JLabel JTextField JButton JComboBox)
           (java.awt.event ActionListener KeyListener) ;for key input
           (java.lang.Runtime)))

(Free_Ex_Op)
(while @options)
(println @POLAR_COORDINATES)

(def #^{:private true} frame)
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 100))

(defn render [^Graphics g]
  (let [WIDTH (.getWidth frame) ;1382 in my laptop
        HEIGHT (.getHeight frame) ;744 in my laptop
        img (BufferedImage. WIDTH HEIGHT BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)
        y (/ (* HEIGHT (- @F1 (formant_lim 0))) (- (formant_lim 1) (formant_lim 0))) ;cartesian coordinate
        x (/ (* WIDTH (- (formant_lim 3) @F2)) (- (formant_lim 3) (formant_lim 2))) ;cartesian coordinate
        ]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img)  (.getHeight img)))  
    (if @POLAR_COORDINATES
      (doto bg
      (.setColor Color/GREEN)
      (.drawLine (/ WIDTH 2) (/ HEIGHT 2) x y))) ;only for polar coordinate
    (doto bg
      (.setColor Color/BLUE) ;blue square
      (.fillRect (int (- x 10) )  (int (- y 10) )  20 20));
    (doto bg
      (. setFont font)
      (.setColor Color/BLACK)
       (.drawString "a" (formant2pixel A WIDTH 2) (formant2pixel A HEIGHT 1));f2 f1 pair
      (.drawString "e" (formant2pixel E WIDTH 2) (formant2pixel E HEIGHT 1))
      (.drawString "i" (formant2pixel I WIDTH 2) (formant2pixel I HEIGHT 1))
      (.drawString "o" (formant2pixel O WIDTH 2) (formant2pixel O HEIGHT 1))
      (.drawString "u" (formant2pixel U WIDTH 2) (formant2pixel U HEIGHT 1)))
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] []
                           (paint [g] (render g)))
                     (.setPreferredSize (Dimension. 
                                         (first dim)
                                         (last dim)))))

(def frame 
  (doto (JFrame.) 
    (.add panel) .pack .show (. setAlwaysOnTop true) (. toFront)
    (. setExtendedState JFrame/MAXIMIZED_BOTH)
    (.addKeyListener (input-listener))))

;printing thread
(def animator (agent nil))
(defn animation [x]
  (when 1
    (send-off *agent* #'animation)
    (.repaint panel)
    (Thread/sleep animation-sleep-ms) nil))
(send-off animator animation)
 
;receving thread
(def udp-receiver (agent nil))
(defn udp-receive []
  (dosync
    (let [time  (/ (- (now) @TIME) 1000.0) ;in ms
          MSG (receive-msg)
          msg (map read-string (string/split MSG #":"))]
      (reset! F1 (first msg))
      (reset! F2 (second msg))
      (reset! EMG1 (second (rest msg)))
      (reset! EMG2 (last msg))         
))); vowel-showed cursor-f1 cursor-f2
      
(defn udp-reception [x]       
  (when @running
     (udp-receive)
    (send-off *agent* #'udp-reception) 
    ;(Thread/sleep animation-sleep-ms)
    nil))
(send-off udp-receiver udp-reception)

(. Thread (sleep 5000));Starting pause
(def trial-duration 600000) ; in ms

(dosync 
  (reset! TIME (now))
  (. (Runtime/getRuntime) exec "wish C:\\Code\\emg_speech_local\\speech_5vowels.tcl")
  (. Thread (sleep trial-duration))
  (. (Runtime/getRuntime) exec "taskkill /F /IM  wish.exe"))
(System/exit 0)
