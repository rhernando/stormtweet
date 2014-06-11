(ns stormtweet.bolts
  "Bolts.

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [backtype.storm [clojure :refer [emit-bolt! defbolt ack! bolt]]]))

(defbolt stormy-bolt ["stormy"] [{{text :text} :tweet :as tuple} collector]
  (emit-bolt! collector [text]
              :anchor tuple)
  (ack! collector tuple))

(defbolt stormtweet-bolt ["message"] [{stormy :stormy :as tuple} collector]
  (emit-bolt! collector [(str "stormtweet produced: "stormy)] :anchor tuple)
  (ack! collector tuple))
