(ns sample4.corekafka
  (:require  [jackdaw.streams :as js]
             [jackdaw.client :as jc]
             [jackdaw.client.log :as jcl]
             [jackdaw.admin :as ja]
             [jackdaw.serdes.edn :refer [serde]]
             [lambdaisland.glogc :as log]
             [willa.streams :refer [transduce-stream]]
            [clojure.spec.alpha :as s]))



(def kafka-config
  {"application.id" "sample4"
   "bootstrap.servers" "localhost:9092"
   "default.key.serde" "jackdaw.serdes.EdnSerde"
   "default.value.serde" "jackdaw.serdes.EdnSerde"
   "cache.max.bytes.buffering" "0"})

;; Serdes tell Kafka how to serialize/deserialize messages
(def serdes
  {:key-serde (serde)
   :value-serde (serde)})

(def admin-client (ja/->AdminClient kafka-config))

;; Each topic needs a config. The important part to note is the :topic-name key.
(def math-problem-calculate-topic
  (merge {:topic-name "math-problem-History"
          :partition-count 1
          :replication-factor 1
          :topic-config {}}
         serdes))


(defn view-messages [topic]
  "View the messages on the given topic"
  (with-open [consumer (jc/subscribed-consumer
                         (assoc kafka-config "group.id" (str (java.util.UUID/randomUUID))) [topic])]
    (jc/seek-to-beginning-eager consumer)
    (->> (jcl/log-until-inactivity consumer 100)
         (map :value)
         doall)))

(defn math-problem-calculate! [calculation]
       ;TODO:: Send calculation with a producer to kafka
    (with-open [producer (jc/producer kafka-config serdes)]
               ;TODO:: Replace with real data
      @(jc/produce! producer math-problem-calculate-topic "Put-ID-here" {:id 1
                                                              :date "4/17/2023"
                                                              :x 2
                                                              :eq "+"
                                                              :y 2
                                                              :total 4}))

      )

(defn topic-exists?
  "Takes a topic name and returns true if the topic exists."
  [topic-config]
  (with-open [client (ja/->AdminClient kafka-config)]
    (ja/topic-exists? client topic-config)))

(defn simple-topology [builder]
(-> (js/kstream builder math-problem-calculate-topic)
    (js/filter (fn [[_ total]]
                 (<= 100 (:total total))))
    (js/map (fn [[key total]]
              [key (select-keys total [:id])]))))

(defn start! []
  "Starts the simple topology"

  (let [builder (js/streams-builder)]
    (simple-topology builder)
    (doto (js/kafka-streams builder kafka-config)
      (js/start))
    (def kafka-streams-app builder))
  (if (false? (topic-exists? math-problem-calculate-topic))
        (ja/create-topics! admin-client [math-problem-calculate-topic])))

(defn stop! []
  "Stops the given KafkaStreams application"
  (js/close kafka-streams-app))

(def topic-math
  {:topic-name "math-problem-History"})
