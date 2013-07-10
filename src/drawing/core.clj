(ns drawing.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:use [lamina.core] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp]
        [drawing.toolbox]
        [drawing.struct_vowel]
        [drawing.experiment_options] 
        [overtone.at-at :only (now)]
        [clojure.contrib.math]
        [drawing.udp])
  (:import (java.awt Color Graphics Dimension GridLayout Font FontMetrics)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame JLabel JTextField)
           (java.awt.event ActionListener KeyListener) ;for key input
           (java.lang.Runtime)))
(import '(java.io File)) 

;MACRO!!
;(def POLAR_COORDINATES true) 

;log data in logfile
(def TIME (atom (now)))
(def F1 (atom 210)) 
(def F2 (atom 510))
(def F3 (atom 2890)) 
(def EMG1 (atom 0.0))
(def EMG2 (atom 0.0))

;filename info
(OPTIONS)
(while @options)
;(def Condition (atom (str (if @CUR "C" "") (if @TGT "T" "") (if (not (or @CUR @TGT)) "none")))) ;Cur+Tgtoptions
(def Condition (atom (if @POLAR_COORDINATES "Polar" "Cart"))) 
(defonce DATE (get-date))

;(def FILENAME (str "C:\\Code\\keewii1\\data\\" NAME DATE "\\" @Condition "\\"))
(def FILENAME (str "C:\\Code\\keewii1\\data\\" NAME "\\" @Condition "\\"))
(def Temp_data "C:\\Code\\keewii1\\temp\\") 
(def session_number (atom 0))
(def PAUSE (atom false))
(def CUR_TGT_sleep (atom true)) ;It hides cursor and target during interval
(mkdir FILENAME)
(mkdir (str FILENAME "\\dat\\"))
(mkdir (str FILENAME "\\wav\\"))
(defn seq_check [filename]
  "This function checks if seq.txt exists" 
  (let [f (File. filename)]
    (cond
      (.isFile f)      (def randomized-sequence 
                         (into [] (map #(matcher %) (string/split (slurp (str FILENAME "seq.txt")) #"\s+"))) );"file"
      (.isDirectory f) "directory"
      (.exists f)      "other"
      :else            (doseq [] (def randomized-sequence  ;non existent case
                                   (shuffle (reduce into (map #(repeat 5 %) [A E I O U])))) 
                         (spit (str FILENAME "seq.txt") 
                               (string/join " " (reduce str (map #(% :name) randomized-sequence))))))))
;canvas size
(def #^{:private true} frame)
(def dim [1050 700]) ;frequency domain: 200-900,500-2600
(def animation-sleep-ms 16)
(def running (atom true))
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 100))

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
        ;y (/ (* HEIGHT (- @F1 0)) 1000) ;polar coordinate
        ;x (/ (* WIDTH (- 3000 @F2)) 3000) ;polar coordinate
        y (/ (* HEIGHT (- @F1 (formant_lim 0))) (- (formant_lim 1) (formant_lim 0))) ;cartesian coordinate
        x (/ (* WIDTH (- (formant_lim 3) @F2)) (- (formant_lim 3) (formant_lim 2))) ;cartesian coordinate
        VOWEL  @alphabet
        x_pos (formant2pixel VOWEL WIDTH 2)
        y_pos (formant2pixel VOWEL HEIGHT 1)
        CHAR (VOWEL :name)]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img)  (.getHeight img)))
    
    (when (and @CUR @CUR_TGT_sleep) ;draw cursor only when CURSOR = true
      (doseq []  
        (if @POLAR_COORDINATES
          (doto bg
            (.setColor Color/GREEN)
            (.drawLine (/ WIDTH 2) (/ HEIGHT 2) x y)));only for polar coordinate
        (doto bg
          (.setColor Color/BLUE) ;blue square
          (.fillRect (int (- x 10) )  (int (- y 10) )  20 20)))) 
    ;(if @TGT (println "true") (println "false")) 
    (if @CUR_TGT_sleep
      (if @TGT  
        (doto bg
          (. setFont font)
          (.setColor Color/BLACK)
          (.drawString CHAR x_pos y_pos))
        (doto bg
          (.setFont font)
          (.setColor Color/BLACK)
          (.drawString CHAR 
             (int (/ (* WIDTH 1500) 3000))
            (int (/ (* HEIGHT 500)  1000)))))
      (doto bg
        (.setFont font)
        (.setColor Color/BLACK)
        (.drawString "Relax" 
          (int (/ (* WIDTH  (- 3000 1700)) 3000));1700 = actual position
          (int (/ (* HEIGHT 500)  1000))))
      ) 
    
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] []
                           (paint [g] (render g)))
                     (.setPreferredSize (Dimension. 
                                         (first dim)
                                         (last dim)))))
(def frame 
  (doto (JFrame.) 
    (.add panel) .pack .show (. setAlwaysOnTop true) 
    (. setExtendedState JFrame/MAXIMIZED_BOTH) 
    (.addKeyListener (input-listener))))

;printing thread
(def animator (agent nil))
(defn animation [x]
  (when true;@running
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
          ;filename (str FILENAME @session_number ".txt");(str "data\\" NAME DATE @session_number ".txt")
          temp_folder (str Temp_data (format "%02d" @session_number) ".txt")
          VOWEL (:name @alphabet)
          VOWEL-f1 (:f1 @alphabet)
          VOWEL-f2 (:f1 @alphabet)]
      ;(println MSG)
      (reset! F1 (first msg))
      (reset! F2 (second msg))
      (reset! EMG1 (second (rest msg)))
      (reset! EMG2 (last msg))   
;(doseq [] (live/ctl f1 :freq (first msg)) (live/ctl f2 :freq (second msg)))      
  (if (and (and (>= @F1 100) (<= @F1 900 )) (and (>= @F2 300 ) (<= @F2 2500 )) (and (>= time 0.0 ) (<= time 5.0 )) )
          (spit temp_folder (str time " " @EMG1 " " @EMG2 " " @F1 " " @F2  "\n")  :append true) )
  ))); vowel-showed cursor-f1 cursor-f2
      
(defn udp-reception [x]       
  (udp-receive)
  (send-off *agent* #'udp-reception)
  nil)
(send-off udp-receiver udp-reception)

(. Thread (sleep 5000));Starting pause

(def trial-duration 5000) ; in ms
(def between-trial 2000) ;in ms
(def total-trials 25)

(seq_check (str FILENAME "seq.txt")) ;If exists, we will use pre-defined sequence

(doseq [i (range  (- @start-trial 1) total-trials)]
  (reset! session_number  (inc i)) 
  (while (not @running)) 
  (let [recording-info (str "cmd /c c:\\sox-14-4-1\\rec.exe -c 2 " Temp_data (format "%02d" @session_number) ".wav trim 0 5")]
    ;recording-info (str "cmd /c c:\\sox-14-4-1\\rec.exe -c 2 " FILENAME @session_number ".wav trim 0 5")]
    (reset! CUR_TGT_sleep true)
    (reset! TIME (now))
    (. (Runtime/getRuntime) exec recording-info)
    
    (. (Runtime/getRuntime) exec "wish C:\\Code\\emg_speech_local\\speech_5vowels.tcl")      
    (reset! alphabet (randomized-sequence i)) 
     ;It saves the right answer
    (spit (str Temp_data (format "%02d" @session_number) ".txt") (str "\n" ((randomized-sequence i) :name) "\n") :append true )  
    (. Thread (sleep trial-duration))
    (. (Runtime/getRuntime) exec "taskkill /F /IM  wish.exe")
    (reset! CUR_TGT_sleep false) 
    (. Thread (sleep between-trial))
    (copy-file (str Temp_data (format "%02d" @session_number) ".wav") (str FILENAME "\\wav\\" (format "%02d" @session_number) ".wav"))
    (copy-file (str Temp_data (format "%02d" @session_number) ".txt") (str FILENAME "\\dat\\" (format "%02d" @session_number) ".dat"))
    ))
(System/exit 0)
