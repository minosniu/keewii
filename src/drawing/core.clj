(ns drawing.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            ;[clojure.contrib.generic.math-functions]
            )
  (:use [lamina.core] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp]
        [drawing.toolbox]
        [overtone.at-at]
        [clojure.contrib.math])
  (:import (java.net DatagramPacket DatagramSocket InetAddress)
           (java.awt Color Graphics Dimension GridLayout Font FontMetrics)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame JLabel JTextField)
           (java.lang.Runtime) ))
;UDP + Java swing 

(defn receive-data [receive-socket]
  "Packet parsing"
  (let [receive-data (byte-array 1024),
       receive-packet (new DatagramPacket receive-data 1024)]
  (.receive receive-socket receive-packet)
  (new String (.getData receive-packet) 0 (.getLength receive-packet))))
(def receive-msg
	(let [receive-socket (DatagramSocket. 9001)]
		(fn [] (receive-data receive-socket))))

;coordinate of square
(def F1 (ref 210)) 
(def F2 (ref 510))
(defstruct vowel :name :f1 :f2)
(def A (struct vowel "a" 750 1000))
(def E (struct vowel "e" 530 1840))
(def I (struct vowel "i" 280 2250))
(def O (struct vowel "o" 370 730))
(def U (struct vowel "u" 290 680))
(def S (struct vowel "Test will start soon" 500 1800))
;(:ind vowel)
(defn formant2pixel [VOWEL ind]
  (if (= ind 1)
    (- (VOWEL :f1) 200)
    (- 1150 (/ (VOWEL :f2) 2))))

(def alphabet (atom S))
(def COUNT (atom 0))
(def session_number (atom 0))
(defonce NAME (get-name))
(defonce DATE (get-date))
(def trial (mk-pool))
(def session (mk-pool))

;canvas size
(def dim [900 600]) ;frequency domain: 200-800,500-2300
(def animation-sleep-ms 16)
(def running true)
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 44))

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
      ;(.fillRect (* 20 (deref x)) (* 20 (deref y)) 20 20));movement speed and size of block
    (doto bg
      (. setFont font)
      (.setColor Color/BLACK)
      ; x: 900-(f2-500)/2=1150-f2/2, y: f1-200
      ;(case @alphabet
      (.drawString CHAR x_pos y_pos )
      ;(.drawString "a" 650 560);f2 f1 pair
      ;(.drawString "e" 250 360)
      ;(.drawString "i" 20 80)
      ;(.drawString "o" 780 180)
      ;(.drawString "u" 810 80)
         ; )
      )
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] []
                           (paint [g] (render g)))
                     (.setPreferredSize (Dimension. 
                                         (first dim)
                                         (last dim)))))

(def frame (doto (JFrame.) (.add panel) .pack .show))

;printing thread
(def animator (agent nil))
;(defn character[] )
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
    (let [time (now)
          MSG (receive-msg)
          msg (map read-string (string/split MSG #":"))
          filename (str "data\\" NAME DATE @session_number ".txt")
          VOWEL (:name @alphabet)
          VOWEL-f1 (:f1 @alphabet)
          VOWEL-f2 (:f1 @alphabet)
          ]
      (println MSG)
      (ref-set F1 (first msg))
      (ref-set F2 (second msg))
  (if (and (and (>= @F1 100) (<= @F1 900 )) (and (>= @F2 300 ) (<= @F2 2500 )) )
        (spit filename (str time " " @F1 " " @F2 "\n")  :append true)); vowel-showed cursor-f1 cursor-f2
  )))
      
 
(defn udp-reception [x]       
  (udp-receive)
  (when running
    (send-off *agent* #'udp-reception))
  nil)
(send-off udp-receiver udp-reception)

(. Thread (sleep 5000))

(def trial-duration 5000) ; in ms
(def between-trial 2000) ;in ms
(def total-trials 20)

(dotimes [i total-trials]
  (dosync 
    (. (Runtime/getRuntime) exec "notepad.exe")
    (reset! alphabet (rand-nth [A E I O U]))
    (reset! session_number (+ 1 @session_number))
    (. Thread (sleep trial-duration))
    (. (Runtime/getRuntime) exec "taskkill /F /IM notepad.exe")
    (. Thread (sleep between-trial)))
  )
(System/exit 0)

