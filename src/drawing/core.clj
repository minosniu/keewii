(ns drawing.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.contrib.generic.math-functions])
  (:use [lamina.core] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp]
        [drawing.toolbox]
        [overtone.at-at])
  (:import (java.net DatagramPacket DatagramSocket InetAddress)
           (java.awt Color Graphics Dimension GridLayout Font FontMetrics)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame JLabel JTextField)))
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
(def alphabet (ref 6))

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
        ]
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
         (.drawString "a" 650 560);f2 f1 pair
         (.drawString "e" 250 360)
        (.drawString "i" 20 80)
         (.drawString "o" 780 180)
          (.drawString "u" 810 80)
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
(def animator (agent nil))
(defn animation [x]
  (when running
    (send-off *agent* #'animation))
  (.repaint panel)
  (Thread/sleep animation-sleep-ms) nil)
(def session (mk-pool))
(defn animation [x] 
  (when running
    (send-off *agent* #'animation)
     (.repaint panel)
     (Thread/sleep animation-sleep-ms)))

(send-off animator animation)

(defonce NAME (get-name))
 (defonce DATE (get-date))

(def udp-receiver (agent nil))

(defn udp-receive []
  (dosync
    (let [MSG (receive-msg)
          msg (map read-string (string/split MSG #":"))]
      (println MSG)
      
      (ref-set F1 (first msg))
      (ref-set F2 (second msg))))  )

(defn udp-reception [x]       
  (udp-receive)
  (when running
    (send-off *agent* #'udp-reception))
  
  nil)
  
(send-off udp-receiver udp-reception)
;loop for trials
;cond for alphabet




;(loop [MSG (receive-msg)]
;  (dosync 
;    (let [msg (map read-string (string/split MSG #":"))
;          filename (str "data\\" NAME DATE ".txt")]   
;      (println MSG)
;      
;      (ref-set F1 (first msg))
;      (ref-set F2 (second msg))
;      (if (and (and (>= @F1 200) (<= @F1 800 )) (and (>= @F2 500 ) (<= @F2 2300 )) )
;        (spit filename (str @F1 " " @F2 "\n")  :append true))  
;      ))
;(at (+ 8000 (now)) #(dosync (println "repaint") (ref-set @alphabet (int (* 5 (rand)) )) (.repaint panel)) session)
;    (recur (receive-msg)))

