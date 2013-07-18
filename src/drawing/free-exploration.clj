(ns drawing.free-exploration
  (:require [clojure.string :as string])
  (:use [drawing.basic_vars] 
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
(def #^{:private true} frame)

(defn vowel_map_render [^Graphics g]
  (let [WIDTH (.getWidth frame) ;1382 in my laptop
        HEIGHT (.getHeight frame) ;744 in my laptop
        img (BufferedImage. WIDTH HEIGHT BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)
        y (/ (* HEIGHT (- @F1 (formant_lim 0))) (- (formant_lim 1) (formant_lim 0))) ;cartesian coordinate
        x (/ (* WIDTH (- (formant_lim 3) @F2)) (- (formant_lim 3) (formant_lim 2)))] ;cartesian coordinate
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img)  (.getHeight img)))  
    (if @POLAR_COORDINATES
      (doto bg 
      (.setColor Color/GREEN)
      (.drawLine (/ WIDTH 2) (/ HEIGHT 2) x y))) ;line only for polar coordinate
    (doto bg
      (.setColor Color/BLUE) ;blue square cursor
      (.fillRect (int (- x 10) )  (int (- y 10) )  20 20));
    (doto bg
      (. setFont font) ;word
      (.setColor Color/BLACK)
      ;f2 f1 pair
       (.drawString (A :name) (formant2pixel A WIDTH 2) (formant2pixel A HEIGHT 1)) ;a
      (.drawString (E :name) (formant2pixel E WIDTH 2) (formant2pixel E HEIGHT 1)) ;e
      (.drawString (I :name) (formant2pixel I WIDTH 2) (formant2pixel I HEIGHT 1))
      (.drawString (O :name) (formant2pixel O WIDTH 2) (formant2pixel O HEIGHT 1)) ;o
      (.drawString (U :name) (formant2pixel U WIDTH 2) (formant2pixel U HEIGHT 1)))
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] [] (paint [g] (vowel_map_render g)))
                     (.setPreferredSize (Dimension. (first dim) (last dim)))));only when we restore down the window
(def frame 
  (doto (JFrame.) 
    (.add panel) .pack .show (. setAlwaysOnTop true) (. toFront)
    (. setExtendedState JFrame/MAXIMIZED_BOTH)
    (.addKeyListener (input-listener))))

;printing thread
(def animator (agent nil))
(defn animation [x]
  (when true
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
(def free-trial-duration 600000) ; in ms

(dosync 
  (reset! TIME (now))
  (. (Runtime/getRuntime) exec "wish C:\\Code\\emg_speech_local\\speech_5vowels.tcl")
  (. Thread (sleep free-trial-duration))
  (. (Runtime/getRuntime) exec "taskkill /F /IM  wish.exe"))
(System/exit 0)
