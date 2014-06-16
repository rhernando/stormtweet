(ns stormtweet.topology
  "Topology

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [stormtweet
             [spouts :refer [twitter-spout]]
             [bolts :refer [stormy-bolt raspberry-storm-bolt]]]
            [backtype.storm [clojure :refer [shell-spout-spec topology spout-spec bolt-spec]] [config :refer :all]])
  (:import [backtype.storm LocalCluster LocalDRPC]))

(defn storm-topology []
  (topology
   ;{"tweets" (spout-spec twitter-spout)}
   {"twitter-spout"
        (shell-spout-spec
            ["ruby" "stream_twitter.rb"]
            ;; Stream declaration:
            ["word"]
        )
    }

   {"stormy-bolt" (bolt-spec {"twitter-spout" :shuffle} stormy-bolt :p 2)
    ;"raspberry-storm-bolt" (bolt-spec {"tweets" :shuffle} raspberry-storm-bolt :p 2)
    }))

(defn run! [& {debug "debug" workers "workers" :or {debug "true" workers "2"}}]
  (doto (LocalCluster.)
    (.submitTopology "tuitology"
                     {TOPOLOGY-DEBUG true ;(Boolean/parseBoolean debug)
                      TOPOLOGY-WORKERS (Integer/parseInt workers)}
                     (storm-topology))))
