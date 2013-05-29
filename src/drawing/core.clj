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
           (java.awt.event ActionListener KeyListener) ;for key input
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
(def FILENAME (str "data\\" NAME DATE))
(def session_number (atom 0))
(def PAUSE (atom false))

;canvas size
(def #^{:private true} frame)
(def dim [900 600]) ;frequency domain: 200-800,500-2300
(def animation-sleep-ms 16)
(def running (atom true))
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 44))
(def SCREEN-SIZE (atom [0 0]))

(defn input-listener []
    (proxy [ActionListener KeyListener] []
      (actionPerformed [e])
      (keyPressed [e] (if (= (str (.getKeyChar e)) "p") 
                        (doseq [] (println "status changed to " @running) (swap! running not)))) ;pause
  (keyReleased [e])
  (keyTyped [e])))

(defn render [^Graphics g]
  (let [WIDTH (.getWidth frame) ;1382 in my laptop
        HEIGHT (.getHeight frame) ;744 in my laptop
        img (BufferedImage. WIDTH HEIGHT BufferedImage/TYPE_INT_ARGB)
        ;img (BufferedImage. (first dim) (last dim) BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)
        y (/ (* HEIGHT (- @F1 200)) 600)
        x (/ (* WIDTH (- 2300 @F2)) 1800)
        VOWEL  @alphabet
        x_pos (formant2pixel VOWEL WIDTH 2)
        y_pos (formant2pixel VOWEL HEIGHT 1)
        CHAR (VOWEL :name)]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img)  (.getHeight img)))
    (doto bg
      (.setColor Color/yellow)
      (.drawLine (/ WIDTH 2) (/ HEIGHT 2) x y))
    (doto bg
      (.setColor Color/BLUE) ;blue square
      (.fillRect (int (- x 10) )  (int (- y 10) )  20 20));
    (doto bg
      (. setFont font)
      (.setColor Color/BLACK)
      (.drawString CHAR x_pos y_pos))
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
          msg (map read-string (string/split MSG #":"))
          filename (str FILENAME @session_number ".txt");(str "data\\" NAME DATE @session_number ".txt")
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
  (when @running
    (send-off *agent* #'udp-reception))
  nil)
(send-off udp-receiver udp-reception)

(. Thread (sleep 5000));Starting pause

(def trial-duration 5000) ; in ms
(def between-trial 2000) ;in ms
(def total-trials 20)

(loop [i total-trials]
  (let [run (if @running 1 0)]
  (when @running 
    (dosync 
      (let [recording-info (str "cmd /c c:\\sox-14-4-1\\rec.exe -c 2 \"C:\\Users\\KangWoo Lee\\workspace\\keewii\\" FILENAME @session_number ".wav\" trim 0 5")]
     
      (reset! TIME (now))
      (. (Runtime/getRuntime) exec recording-info)
      (. (Runtime/getRuntime) exec "notepad.exe")
      ;(. (Runtime/getRuntime) exec "wish C:\\Code\\emg_speech_local\\speech_5vowels.tcl")
      (reset! alphabet (rand-nth [A E I O U]))
      (reset! session_number (+ 1 @session_number))
      (. Thread (sleep trial-duration))
      (. (Runtime/getRuntime) exec "taskkill /F /IM notepad.exe")
      ;(. (Runtime/getRuntime) exec "taskkill /F /IM  wish.exe")
      (. Thread (sleep between-trial)))))
  (recur (- i run))))
(System/exit 0)
