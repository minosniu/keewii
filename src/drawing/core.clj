(ns drawing.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:use [lamina.core ] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp])
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
(def x (ref 0)) 
(def y (ref 0))

;canvas size
(def dim [900 600]) ;frequency domain: 200-800,500-2300
(def animation-sleep-ms 16)
(def running true)
(def ^{:private true} font (new Font "Georgia" Font/PLAIN 44))

(defn render [^Graphics g]
  (let [img (BufferedImage. (first dim) (last dim) BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img) (.getHeight img)))    
    (doto bg
      (.setColor Color/BLUE) ;blue square
      (.fillRect (* 20 (deref x)) (* 20 (deref y)) 20 20));movement speed and size of block
    (doto bg
      (. setFont font)
      (.setColor Color/BLACK)
      ; x: 900-(f2-500)/2=1150-f2/2, y: f1-200
      (.drawString "a" 650 560);f2 f1 pair
      (.drawString "e" 250 360)
      (.drawString "i" 20 80)
      (.drawString "o" 780 180)
      (.drawString "u" 810 80)
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

(defn animation [x] 
  (when running
    (send-off *agent* #'animation)
     (.repaint panel)
     (Thread/sleep animation-sleep-ms)))

(send-off animator animation)

(loop [MSG (receive-msg)]
  (dosync 
    (let [msg (map read-string (string/split MSG #":"))]   
      (println MSG)
      (ref-set x (first msg))
      (ref-set y (second msg))
      (spit "test.txt" (str @x " " @y "\n")  :append true)))
    (recur (receive-msg)))
