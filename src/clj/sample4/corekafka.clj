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
  (let [id (rand-int 1000)
        X (get calculation :x)
        Y (get calculation :y)
        eq (get calculation :equation-map)
        total (get calculation :Total)]
    (def date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date)))
    (log/debug :kafka "blake" + (str calculation))
   ;; (log/debug :kafka "blake X:" + (str calculation :x))
    (with-open [producer (jc/producer kafka-config serdes)]
      @(jc/produce! producer math-problem-calculate-topic id {:id id
                                                              :date date
                                                              :x X
                                                              :eq eq
                                                              :y Y
                                                              :ed 40}))))

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





(comment

  ;; Part 1 - Simple Topology
  (def topic-name nil)
  (start!)
  ;; create the "purchase-made" and "large-transaction-made" topics
  (ja/create-topics! admin-client [math-problem-calculate-topic])
  (math-problem-calculate! [11 "+",25,30 ])
  (ja/topic-exists? admin-client [{:keys ["Yo"] :as "math"}])
  (def totally (view-messages math-problem-calculate-topic))
  (topic-exists? admin-client "Math")
  (into [] totally)
  (println totally)

  (topic-exists? math-problem-calculate-topic)
  ()
)
