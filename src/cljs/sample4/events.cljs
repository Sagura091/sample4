(ns sample4.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [re-frame.db :as db]
    [lambdaisland.glogi :as log]
    [sample4.sockets :as client-socket]
    [reitit.frontend.controllers :as rfc]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))




(rf/reg-event-db
  :initialise-db                                            ;; usage: (dispatch [:initialise-db])
  (fn [_ _]                                                 ;; Ignore both params (db and event)
    {:Name              "Math Database"                   ;; return a new value for app-db
     :History-Equations [{:id (inc 0) :x 1 :equation-map {:eq "-" :text "minus"} :y 4 :Total -3}]
     :Temp-Equation     {:x 0 :equation-map {:eq "-" :text "minus"} :y 0 :Total 0}
     :Active-state      "calculator"
     :conncetion          "NVM"}
   ))



(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                               (rfc/apply-controllers (:controllers old-match) match))]
         (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

; this is a event to store the equation and total to the database for re-frame
; :set-eq is the name of the event you are going to call in another file to send the event
; db - is the atom and the non-refresh database to store items
; eq is a equation it is a hash map containing {id ,x,eq,y,total}
(rf/reg-event-db
  :set-eq
  (fn-traced [db [_ eq]]
             (conj (:History-Equations db) eq)))

; this is a re-frame handler to grab from the server then send it to the :set-eq event.
; :Get-Total-Eq-From-Server is the name of the handle that you pass in.
; xeqy is a hasmap consisting of (:x, :eq, :y), eventually this handler will add total to the hash
; map
; After we do a get and the server sends us back a total we assoc it to add it to the hash map.
; then with the handler of the get we send an event :set-eq to set that equation that was produced
; by the user to a database called db

(rf/reg-event-fx
  :Get-Total-Eq-From-Server
  (fn-traced [{:keys [db]} [_ a]]
             (println (get-in db [:Temp-Equation :equation-map :text]))
             (println (str "/api/math/" (get-in db [:Temp-Equation :equation-map :text])))
             ;   (println (str "/api/math/" (get-in arithmatic [1 :a])))
             {:http-xhrio {:method          :get
                           :uri             (str "/api/math/" (get-in db [:Temp-Equation :equation-map :text]))
                           :url-params      {:x (js/parseInt (get-in db [:Temp-Equation :x]))
                                             :y (js/parseInt (get-in db [:Temp-Equation :y]))}
                           :format          (ajax/json-request-format)
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success      [:setTempDataTotal]
                           :on-failure      (fn [r] js/alert r)}}))


(rf/reg-event-fx
  :setTempDataTotal
  (fn-traced [{:keys [db]} [_ a]]
             {:db       (assoc-in db [:Temp-Equation :Total] (:total a))
              :dispatch [:addToEquationHistory]}))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success      [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

(rf/reg-event-db
  :setTempDataX
  (fn-traced [db [_ x]]
             (assoc-in db [:Temp-Equation :x] x)))

(rf/reg-event-db
  :setTempDataY
  (fn-traced [db [_ y]]
             (assoc-in db [:Temp-Equation :y] y)))

(rf/reg-event-db
  :setTempDataEquation
  (fn-traced [db [_ eq]]
             (assoc-in db [:Temp-Equation :equation-map] eq)))

(rf/reg-event-db
  :switch-active-state
  (fn-traced [db [_ switch]]
             (assoc-in db [:Active-state] switch)))

(rf/reg-event-db
  :setHistoryFromServer
  (fn-traced [db [_ data]]
             (assoc-in db [:History-Equations (count (:History-Equations db))] data)))

(rf/reg-event-db
  :resetHistory
  (fn-traced [db [_ data]]
             (assoc-in db [:History-Equations] {})))


(rf/reg-event-db  ; grab data. Take out line 195
  :getHistoryFromServer
  (fn-traced [db [_ data]]
              ;TODO:: get data from socket


             (assoc-in db [:conncetion] data)))


(rf/reg-event-db
  :addToEquationHistory
  (fn-traced [db [_ _total]]
             (let [add (:Temp-Equation db)]
               ;TODO:: send the data using the socket

               (assoc-in db [:History-Equations (count (:History-Equations db))] add)
              )))

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :Equation
  (fn [db _]
    (-> db :id)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :Active-state
  (fn-traced [db _]
             (:Active-state db)))

(rf/reg-sub
  :get-history
  (fn-traced [db _]
             (:History-Equations db)))

(rf/reg-sub
  :Get-Total
  (fn-traced [db _]
             (:Total (:Temp-Equation db))))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
