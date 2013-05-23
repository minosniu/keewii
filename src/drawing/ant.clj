(ns drawing.ants
  (:require [clojure.data.json :as json]
            [clojure.string :as string])
  (:use [lamina.core ] 
        [aleph.tcp]
        [gloss.core]
        [aleph.udp])
  (:import (java.net DatagramPacket DatagramSocket InetAddress)
           (java.awt Color Graphics Dimension)
           (java.awt.image BufferedImage)
           (javax.swing JPanel JFrame)))


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

;canvas
(def dim 80)
(def animation-sleep-ms 16)
(def running true)
;world is a 2d vector of refs to cells
(defstruct cell :food :pher)
(def world
  (mapv (fn [_]
          (mapv (fn [_] (ref (struct cell 0 0)))
                (range dim)))
        (range dim)))
(defn place [[x y]]
  (-> world (nth x) (nth y)))

;pixels per world cell
(def scale 5)

(defn fill-cell [^Graphics g x y c]
  (doto g
    (.setColor c)
    (.fillRect (* x scale) (* y scale) scale scale)))

(defn render [^Graphics g]
  (let [v (dosync (vec (for [x (range dim) y (range dim)]
                         @(place [x y]))))
        img (BufferedImage. (* scale dim) (* scale dim)
                            BufferedImage/TYPE_INT_ARGB)
        bg (.getGraphics img)]
    (doto bg
      (.setColor Color/WHITE) ;background
      (.fillRect 0 0 (.getWidth img) (.getHeight img)))
    (doto bg
      (.setColor Color/BLUE) ;blue square
      (.fillRect (* 20 (deref x)) (* 20 (deref y)) 20 20)
         )
    (.drawImage g img 0 0 nil)
    (.dispose bg)))

(def ^JPanel panel (doto (proxy [JPanel] []
                           (paint [g] (render g)))
                     (.setPreferredSize (Dimension.
                                         (* scale dim)
                                         (* scale dim)))))

(def frame (doto (JFrame.) (.add panel) .pack .show))
(def animator (agent nil))
(defn animation [x]
  (when running
    (send-off *agent* #'animation))
  (.repaint panel)
  (Thread/sleep animation-sleep-ms)
  nil)
(defn animation [x] 
  (when running
    (send-off *agent* #'animation)
     (.repaint panel)
     (Thread/sleep animation-sleep-ms)
    )
 )

(send-off animator animation)

(loop [MSG (receive-msg)]
  (dosync 
    (let [msg (map read-string (string/split MSG #":"))]   
      (println MSG)
      (ref-set x (first msg))
      (ref-set y (second msg))
      (spit "test.txt" (str @x " " @y "\n")  :append true)))
    (recur (receive-msg)))
