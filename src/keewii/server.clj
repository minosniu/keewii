(ns keewii.server
  (:use overtone.live))
  (def server (osc-server 44100 "keewii"))
  (zero-conf-on)
