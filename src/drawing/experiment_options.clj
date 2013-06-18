(ns drawing.experiment_options) 


(import '(javax.swing JFrame JLabel JTextField JButton)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout))

(def CUR (atom true))
(def TGT (atom true))
(defn OPTIONS []
  (let [frame (JFrame. "Experimental options")
        ;temp-text (JTextField.)
        ;celsius-label (JLabel. "Celsius")
        option1 (JButton. "Cursor(O) + Target(O)")
        option2 (JButton. "Cursor(O) + Target(X)")
        option3 (JButton. "Cursor(X) + Target(O)")
        option4 (JButton. "Cursor(X) + Target(X)")
        ;convert-button (JButton. "Convert")
        ;fahrenheit-label (JLabel. "Fahrenheit")
        ]
    (.addActionListener
     option1
     (reify ActionListener 
       (actionPerformed
         [_ evt]
         (reset! CUR true)
         (reset! TGT true)
       
         )))
    (.addActionListener
      option2
      (reify ActionListener 
        (actionPerformed
          [_ evt]
          (reset! CUR true)
          (reset! TGT false)
          )))
    (.addActionListener
      option3
      (reify ActionListener 
        (actionPerformed
          [_ evt]
          (reset! CUR false)
          (reset! TGT true)
          )))
    (.addActionListener
      option4
      (reify ActionListener 
        (actionPerformed
          [_ evt]
             (reset! CUR false)
             (reset! TGT false)
             )))
    (doto frame
      (.setLayout (GridLayout. 4 1 2 2))
      (.add option1)
      (.add option2)
      (.add option3)
      (.add option4)
      (.setSize 300 300)
      (.setVisible true))))
