(ns sample4.SenteWebsocket.handler
  (:require [lambdaisland.glogc :as log]
            [sample4.SenteWebsocket.socket :as socket]
            [sample4.corekafka :as kafka]))

(defmulti -event-msg-handler :id)

(defn event-msg-handler
  [{:as ev-msg :keys [id ?data event]}]
  (println ev-msg)
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (println id + " " + ?data)
  (log/info :unhandled-event event))

(defmethod -event-msg-handler :chsk/ws-ping
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (log/debug :ping event))

(defmethod -event-msg-handler :some/history
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (kafka/math-problem-calculate! ?data)
  (log/debug :yo "Blake " + ?data))


(defmethod -event-msg-handler :some/history-view
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (log/debug :History-View "Blake tis is the server Grabbing data from topic in kafka")
  (socket/send-data :some/history-view {:history (kafka/view-messages kafka/math-problem-calculate-topic)}))
