(ns drawing.experiment_options) 


(import '(javax.swing JFrame JLabel JTextField JButton JComboBox)
        '(java.awt.event ActionListener ItemListener)
        '(java.awt GridLayout)
        '(java.util.Vector))
(def start-trial (atom 1))
(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))
(def options (atom true)) 
(def CUR (atom true))
(def TGT (atom true))
(def POLAR_COORDINATES (atom false))
;For Cursor&Target options
;(defn OPTIONS []
;  (let [frame (JFrame. "Experimental options")
;        ;temp-text (JTextField.)
;        ;celsius-label (JLabel. "Celsius")
;        option (JComboBox.  (java.util.Vector. ["Cursor+Target" "Cursor only" "Target only" "Nothing"])) 
;        Name (JTextField. "Type your name here.")
;        Start (JTextField. "Set the trial number to start from. (1~25)") 
;        Submit (JButton. "Submit") ]
;    (.addItemListener
;      option
;       (proxy [java.awt.event.ItemListener] []
;                      (itemStateChanged [item-event]
;                        (case (.getSelectedIndex option)
;                          0 (doseq [] (reset! CUR true) (reset! TGT true)) 
;                          1 (doseq [] (reset! CUR true) (reset! TGT false))
;                          2 (doseq [] (reset! CUR false) (reset! TGT true))
;                          3 (doseq [] (reset! CUR false) (reset! TGT false)))))) 
;    (.addActionListener
;      Submit
;      (reify ActionListener 
;        (actionPerformed
;          [_ evt]
;          (def NAME (.getText Name))
;           (reset! start-trial (parse-int (.getText Start)) ) 
;          (.dispose frame) 
;          (reset! options false))))
;
;    (doto frame
;      (.setLayout (GridLayout. 4 1 2 2))
;      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) 
;      (.setLocation 500 300) 
;      (.add option)
;      (.add Name)
;      (.add Start)
;      (.add Submit)
;      (.setSize 300 300)
;      (.setVisible true))))

;coordinate options
(defn OPTIONS []
  (let [frame (JFrame. "Experimental options")
        ;temp-text (JTextField.)
        ;celsius-label (JLabel. "Celsius")
        option (JComboBox.  (java.util.Vector. ["Cartesian coordinate" "Polar coordinate"])) 
        Name (JTextField. "Type your name here.")
        Start (JTextField. "Set the trial number to start from. (1~25)") 
        Submit (JButton. "Submit") ]
    (.addItemListener
      option
       (proxy [java.awt.event.ItemListener] []
                      (itemStateChanged [item-event]
                        (case (.getSelectedIndex option)
                          0 (doseq [] (reset! POLAR_COORDINATES false))
                          1 (doseq [] (reset! POLAR_COORDINATES true)))))) 
    (.addActionListener
      Submit
      (reify ActionListener 
        (actionPerformed
          [_ evt]
          (def NAME (.getText Name))
           (reset! start-trial (parse-int (.getText Start)) ) 
          (.dispose frame) 
          (reset! options false))))

    (doto frame
      (.setLayout (GridLayout. 4 1 2 2))
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) 
      (.setLocation 500 300) 
      (.add option)
      (.add Name)
      (.add Start)
      (.add Submit)
      (.setSize 300 300)
      (.setVisible true))))

;(OPTIONS)
