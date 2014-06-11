(ns stormtweet.topology
  "Topology

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [stormtweet
             [spouts :refer [twitter-spout]]
             [bolts :refer [stormy-bolt stormtweet-bolt]]]
            [backtype.storm [clojure :refer [topology spout-spec bolt-spec]] [config :refer :all]])
  (:import [backtype.storm LocalCluster LocalDRPC]))

(defn storm-topology []
  (topology
   {"spout" (spout-spec twitter-spout)}

   {"stormy-bolt" (bolt-spec {"spout" ["tweet"]} stormy-bolt :p 2)
    "stormtweet-bolt" (bolt-spec {"stormy-bolt" :shuffle} stormtweet-bolt :p 2)}))

(defn run! [& {debug "debug" workers "workers" :or {debug "true" workers "2"}}]
  (doto (LocalCluster.)
    (.submitTopology "tuitology"
                     {TOPOLOGY-DEBUG (Boolean/parseBoolean debug)
                      TOPOLOGY-WORKERS (Integer/parseInt workers)}
                     (storm-topology))))
