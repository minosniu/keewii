(ns drawing.udp
  (:import (java.net DatagramPacket DatagramSocket InetAddress)))

;UDP- server Port localhost:9001 
(defn receive-data [receive-socket]
  "Packet parsing"
  (let [receive-data (byte-array 1024),
       receive-packet (new DatagramPacket receive-data 1024)]
  (.receive receive-socket receive-packet)
  (new String (.getData receive-packet) 0 (.getLength receive-packet))))
(def receive-msg
  (let [receive-socket (DatagramSocket. 9001)]
		(fn [] (receive-data receive-socket))))
