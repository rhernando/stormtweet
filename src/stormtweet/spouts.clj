(ns stormtweet.spouts
  "Spouts.

  More info on the Clojure DSL here:

  https://github.com/nathanmarz/storm/wiki/Clojure-DSL"
  (:require
   [backtype.storm [clojure :refer [defspout spout emit-spout!]]]
   [clojure.data.json :as json]
   [http.async.client :as ac]
   )
  (:use
   [lamina.core]
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.streaming])
  (:import
   (twitter.callbacks.protocols AsyncStreamingCallback)))

(def my-creds (make-oauth-creds "ziSzNkrNzeQ2pjdGUZYmw"
                                "JApCmbjr5Hjx0LTTMfcmIM20g6ID54o4Vub6TcfB4"
                                "1012280850-Pb7EA6urmlDWVkKOuuLB9IAvGqyw4JiJVYTMEx8"
                                "QAh47SHFRTElih0sSPFTbrFyE6QpVEDA4XarIzM4WA"))
(def-twitter-streaming-method public-stream :post "user.json")

(def infproxy {:host "194.140.11.77" :port 80})
(defspout twitter-spout ["tweet"]
  [conf context collector]

  (let [tweet-channel (channel)
        connection    (statuses-filter :params {:track "rey"}
                                       :oauth-creds my-creds
                                       :proxy infproxy
                                       )
        callback      (AsyncStreamingCallback.
                       (fn on-body-part [response byte-stream]
                         (->> byte-stream
                              str
                              json/read-json
                              (enqueue tweet-channel)))
                       (fn on-failure [response]
                         (println "response"))
                       (fn on-exception [response]
                         (println "on-exception")))]



    (spout
     (nextTuple []
                (emit-spout! collector [@(read-channel tweet-channel)]))
     (ack [id]
          ;; You only need to define this method for reliable spouts
          ;; (such as one that reads off of a queue like Kestrel)
          ;; This is an unreliable spout, so it does nothing here
          ))))
