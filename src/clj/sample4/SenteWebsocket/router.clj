(ns sample4.SenteWebsocket.router
  (:require [compojure.core :as comp :refer (defroutes GET POST)]
            [compojure.route :as route]
            [sample4.SenteWebsocket.handler :as handlers]
            [sample4.SenteWebsocket.socket :as socket]
            [ring.middleware.defaults]
            [ring.util.response :as response]
            [taoensso.sente :as sente]))

(defroutes ring-routes
           (GET "/" _ring-req (response/content-type (response/resource-response "index.html") "text/html"))
           (GET  "/chsk"  ring-req (socket/ring-ajax-get-or-ws-handshake ring-req))
           (POST "/chsk"  ring-req (socket/ring-ajax-post                ring-req))
           (route/resources "/")
           (route/not-found "Not found"))

(defonce router_ (atom nil))

(defn stop! []
  (when-let [stop-fn @router_] (stop-fn)))

(defn start! []
  (stop!)
  (reset! router_ (sente/start-server-chsk-router! socket/ch-chsk handlers/event-msg-handler)))
