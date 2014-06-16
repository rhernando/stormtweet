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

(def oath-creds (make-oauth-creds "ziSzNkrNzeQ2pjdGUZYmw"
                                "JApCmbjr5Hjx0LTTMfcmIM20g6ID54o4Vub6TcfB4"
                                "1012280850-Pb7EA6urmlDWVkKOuuLB9IAvGqyw4JiJVYTMEx8"
                                "QAh47SHFRTElih0sSPFTbrFyE6QpVEDA4XarIzM4WA"))

(def infproxy {:host "194.140.11.77" :port 80})


(defspout twitter-spout ["tweet"]
  [conf context collector]

   (let [tweet-channel (channel)

        callback     (AsyncStreamingCallback. (comp println #(:text %) json/read-json #(str %2))
                      (comp println response-return-everything)
                  exception-print)
         connection    (statuses-filter :params {:track "rey"}
                     :proxy infproxy
                     :oauth-creds oath-creds
                     :callbacks callback)]


    (spout
     (nextTuple []
                (emit-spout! collector [@(read-channel tweet-channel)]))
     (ack [id]
          ;; You only need to define this method for reliable spouts
          ;; (such as one that reads off of a queue like Kestrel)
          ;; This is an unreliable spout, so it does nothing here
          ))))
