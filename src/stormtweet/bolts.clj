(ns stormtweet.bolts
  "Bolts.

More info on the Clojure DSL here:

https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require [backtype.storm [clojure :refer [emit-bolt! defbolt ack! bolt]]]))

(defbolt stormy-bolt ["tweet"] [{{text :text} :tweet :as tuple} collector]

 (emit-bolt! collector [(if (< 50 (count text))
                           "I'm a short tweet!"
                           "I'm a long tweet!")]
              :anchor tuple)
  (ack! collector tuple))

