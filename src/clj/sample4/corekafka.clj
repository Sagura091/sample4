(ns sample4.corekafka
  (:require [jackdaw.streams :as js]
          [jackdaw.client :as jc]
          [jackdaw.client.log :as jcl]
          [jackdaw.admin :as ja]
          [jackdaw.serdes.edn :refer [serde]]
          [willa.streams :refer [transduce-stream]]
          [willa.core :as w]
          [willa.viz :as wv]
          [willa.experiment :as we]
          [willa.specs :as ws]
          [clojure.spec.alpha :as s]))



(def kafka-config
  {"application.id" "kafka-example"
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



(defn simple-topology [builder])
(-> (js/kstream builder math-problem-calculate-topic)
    (js/filter (fn [[_ total]]
                 (<= 100 (:total total))))
    (js/map (fn [[key total]]
              [key (select-keys total [:user-id])])))

(defn start! []
  "Starts the simple topology"
  (let [builder (js/streams-builder)]
    (doto (js/kafka-streams builder kafka-config)
      (js/start))))

(defn stop! [kafka-streams-app]
  "Stops the given KafkaStreams application"
  (js/close kafka-streams-app))

