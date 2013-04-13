KeeWii
======

KeeWii is a real-time speech synthesizer leveraged by Overtone and Clojure. 


Naming
======

KeeWii was initially called MyoTalk, literally a speech synthesizer controlled by muscle activities. Coincidentally the Chinese transliteration of "MyoTalk" sounds very much like kiwi (猕猴桃). Imagine one does the following transformations in Clojure:

    (tweak-until-unique-on-github (voice-to-english-text (text-to-chinese-speech "MyoTalk")))
    
You'll end up with the name "KeeWii".

Installation
============
KeeWii can be included in your Leiningen project by adding:

    [keewii "0.1.0"]

Usage
=====
