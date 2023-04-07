(ns sample4.handler
  (:require
    [lambdaisland.glogi :as log]
    [cljs-node-io.core :as io]
    [re-frame.core :as rf]
    [lambdaisland.glogi.console :as glogi-console]
    [cljs.nodejs :as nodejs]
    [cljs.core.async :as async]
    [goog.fs :as fs]))



(defn write-to-file [file-path data]
  (.getFile fs file-path
            #(.createWriter %1
                            (fn [writer]
                              (let [blob (js/Blob. #js [data] {:type "text/plain"})]
                                (.write writer blob))))))


(defmulti event-msg-handler :id)

(defn event-msg-handler
  [{:as ev-msg :keys [id ?data event]}]
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :default
  [{:keys [event]}]
  (log/info :unhandled-event event))

(comment
  (io/spit "~/Test.txt" "BLAKE YOU RULE" :append true)
  ()

  ())
(defmethod event-msg-handler :chsk/recv
  [{:keys [?data]}]
  (let [[event-type data] ?data]
    ;(println (into [] (get  data :history)))
    ;(println (get-in ?data [1]))
    (cond
      (contains? (get-in ?data [1]) :history)  (let [histoy-data (into [] (get  data :history))]
                                                 (doseq [i histoy-data]
                                                   (rf/dispatch [:setHistoryFromServer i]))))
    (log/info :push-event data)))


(defmethod event-msg-handler :some/broadcast
  [{:keys [event]}]
  (log/info :some/broadcast event))

(defmethod -event-msg-handler :chsk/ws-ping
  [{:keys [event]}]
  (log/info :ping event))


(defmethod -event-msg-handler :some/history-view
  [{:keys [?data]}]
  (let [[event-type data] ?data]
    (log/debug :yobro "blake Yo bro in the client handler!!!! WOOOOOOO")
  (log/debug :some-history-view data)))

(defmethod -event-msg-handler :some/history
  [{:keys [event]}]
  (log/info :some/history-view event))
