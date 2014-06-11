(defproject stormtweet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [twitter-api "0.7.5"]
                 [lamina "0.5.2"]]
  :aot [stormtweet.TopologySubmitter]
  ;; include storm dependency only in dev because production storm cluster provides it
  :profiles {:dev {:dependencies [[storm "0.8.1"]]}})