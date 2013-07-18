(ns drawing.experiment_options
  (:use [drawing.toolbox :only (parse-int)]
        [drawing.basic_vars])
  (:import (javax.swing JFrame JLabel JTextField JButton JComboBox)
           (java.awt.event ActionListener ItemListener MouseListener MouseAdapter KeyListener)
           (java.awt GridLayout)
           (java.util.Vector))) 

;coordinate options
(defn OPTIONS []
  (let [frame (JFrame. "Experimental options")
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
    (.addMouseListener 
      Name
      (proxy [MouseAdapter] []
        (mouseClicked [e]
          (.setText Name "")
          (.setToolTipText Name "Name")))) 
    (.addMouseListener 
      Start
      (proxy [MouseAdapter] []
        (mouseClicked [e]
          (.setText Start "1")
          (.setToolTipText Start "Starting point")))) 

    
    (doto frame
      (.setLayout (GridLayout. 4 1 2 2))
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) 
      (.setLocation 500 300) 
      (.add option)
      (.add Name)
      (.add Start)
      (.add Submit)
      (.setSize 400 300)
      (.setVisible true))))

(defn Free_Ex_Op []
  (let [frame (JFrame. "Experimental options")
        option (JComboBox.  (java.util.Vector. ["Cartesian coordinate" "Polar coordinate"])) 
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
          (.dispose frame) 
          (reset! options false))))
    (doto frame
      (.setLayout (GridLayout. 2 1 2 2))
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE) 
      (.setLocation 500 300) 
      (.add option)
      (.add Submit)
      (.setSize 400 120)
      (.setVisible true))))

;(OPTIONS)
