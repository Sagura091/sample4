(ns sample4.handler
  (:require
    [lambdaisland.glogi :as log]
    [re-frame.core :as rf]
    [lambdaisland.glogi.console :as glogi-console]))




(defmulti event-msg-handler :id)


(defmethod event-msg-handler :default
  [{:keys [event]}]
  (log/info :unhandled-event event))
(defmethod event-msg-handler :chsk/recv
  [{:keys [?data]}]
  (let [[event-type data] ?data]
    ;TODO:: parse the history from the event and dispatch :setHistoryFromServer

    (log/info :push-event data)))


(defmethod event-msg-handler :some/broadcast
  [{:keys [event]}]
  (log/info :some/broadcast event))

(defmethod event-msg-handler :chsk/ws-ping
  [{:keys [event]}]
  (log/info :ping event))


(defmethod event-msg-handler :some/history-view
  [{:keys [?data]}]
  (let [[event-type data] ?data]
  (log/debug :some-history-view data)))

(defmethod event-msg-handler :some/history
  [{:keys [event]}]
  (log/info :some/history-view event))

