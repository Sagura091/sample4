(ns sample4.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [sample4.ajax :as ajax]
    [sample4.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "sample4"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]
                 [nav-link "#/calc" "Math" :calc]]]]))

(defn custom-button [button-name equation-key-re-frame hashmap]


[:div.button {
              :on-click #(rf/dispatch [:Get-Total-Eq-From-Server])}

 button-name])





(defn input [input-name equation-key-re-frame equation-data]
  [:input.input.is-primary {:type        "text"
                            :placeholder input-name
                            :style       {:width 100}
                            :on-change   (fn [event]
                                           (rf/dispatch [equation-key-re-frame (-> event .-target .-value)]))}])



(defn result []

  [:div.box
   {:style {:position "relative"
            :left     450
            :top      -45
            :width    100
            :height   50
            :shadow   true
            :color    (cond
                        (and (>= @(rf/subscribe [:Get-Total]) 0) (<= 19 @(rf/subscribe [:Get-Total])) "green")
                        (and (>= @(rf/subscribe [:Get-Total]) 20) (<= 49 @(rf/subscribe [:Get-Total])) "#1684fac9"))}}
   @(rf/subscribe [:Get-Total])])









(defn equation-tab []

  [:div.column {:class "column is-four-fifths"}


   [:h1
    [input "X" :setTempDataX]
    [drop-down-box :setTempDataEquation]
    [input "Y" :setTempDataY]
    [custom-button "=" :Get-Total-Eq-From-Server]
    [result]]])


(comment

  (def active-state (r/atom "calculator"))
  (if (= :calculator @active-state) "is-active" "")
  (reset! active-state "History")
  (def vec-map [{:x 0 :y 1 :equation {:eq "-"}}])
  (get-in vec-map [0 :equation :eq])
  (:eq vec-map)

  ())


(defn History-list []
  (println @(rf/subscribe [:get-history]))
  (println (get-in @(rf/subscribe [:get-history]) [0 :equation-map :eq]))
  [:ul
   ;; for returns a seq
   (for [his @(rf/subscribe [:get-history])]
     [:li {:key (:y his)}                                   ;; stable and unique key
      [:a (str (:x his) " " (str (:eq (:equation-map his))) " " (str (:y his)) " = " (str (:Total his)))]])])

(defn History-tab []

  [:div
   [:h2 {:style {:position "relative"
                 :left     80
                 :top      45}}
    [History-list]]])



(defn math-page []

  (println @(rf/subscribe [:Active-state]))
  (let [all-complete @(rf/subscribe [:Active-state])]
    [:div {:class "container"}
     [:div.tabs.is-Large
      [:ul
       [:li {:class    (if (= "calculator" all-complete) "is-active" "")
             :on-click (fn [response] (rf/dispatch [:switch-active-state "calculator"]))}
        [:a "calculator"]]

       [:li {:class    (if (= "Basic-Math" all-complete) "is-active" "")
             :on-click (fn [response]
                         (rf/dispatch [:switch-active-state "Basic-Math"]))}
        [:a "Basic-Math"]]
       [:li {:class    (if (= "History" all-complete) "is-active" "")
             :on-click (fn [response]
                         (rf/dispatch [:switch-active-state "History"]))}
        [:a "History"]]]]

     [:div.tabs-content

      {:class    (if (= "calculator" all-complete)
                   "tab-content is-active")
       :style    {:visibility (if (= "calculator" all-complete) "visible" "hidden")}
       :disabled (if (= "calculator" all-complete) true false)}
      [custom-button "1"]]
     [:div.tabs-content

      {:class    (if (= "Basic-Math" all-complete)
                   "tab-content is-active" "tab-content")
       :style    {:visibility (if (= "Basic-Math" all-complete) "visible" "hidden")}
       :disabled (if (= "Basic-Math" all-complete) true false)}
      [equation-tab]]
     [:div.tabs-content
      {:class    (if (= "History" all-complete)
                   "tab-content is-active" "tab-content")
       :style    {:visibility (if (= "History" all-complete) "visible" "hidden")
                  :position   "relative"
                  :left       "80"
                  :top        -200}
       :disabled (if (= "History" all-complete) true false)}
      [History-tab]]]))





(defn drop-down-box [equation-key-re-frame]
  [:div.select
   [:select {
             :id        "selector"
             ;:on-change (fn [event]

             ;(println event :selected)
             :on-change (fn [event]
                          (rf/dispatch [:setTempDataEquation
                                        {:eq (-> event .-target .-value)
                                         (keyword "text")
                                         (case (-> event .-target .-value)
                                           "+" "plus"
                                           "-" "minus"
                                           "*" "mul"
                                           "/" "divide")}]))}


    [:option "Select dropdown"]
    [:option {:id "add"} "+"]
    [:option {:id "minus"} "-"]
    [:option {:id "mul"} "*"]
    [:option {:id "divide"} "/"]]])

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]
     ["/calc" {:name :calc
               :view #'math-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))