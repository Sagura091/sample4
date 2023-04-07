(ns sample4.SenteWebsocket.server
  (:require [sample4.SenteWebsocket.router :as router]
            [org.httpkit.server :as http-kit]
            [ring.middleware.defaults :as middleware]))

(def main-ring-handler
  (middleware/wrap-defaults router/ring-routes middleware/site-defaults))

(defonce web-server_ (atom nil))

(defn stop-server! []
  (when-let [stop-fn @web-server_] (stop-fn)))

(defn start-server! [port]
  (stop-server!)
  (let [ring-handler (var main-ring-handler)
        [port stop-fn]
        (let [stop-fn (http-kit/run-server ring-handler {:port port})]
          [(:local-port (meta stop-fn)) (fn [] (stop-fn :timeout 100))])]
    (println (str "Running Blake at port " port))
    (reset! web-server_ stop-fn)))

(defn start! []
  (router/start!)
  (start-server! 5000))

(defn stop! []
  (router/stop!)
  (stop-server!))

(defn -main []
  (start!))

(comment
  (println web-server_)
  (start!)
  (stop!))
