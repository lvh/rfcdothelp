(ns rfcdothelp.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react])
    (:import goog.History))

(def state
  (atom {:rfc nil}))

(defn lego-set-url
  [rfc]
  (str "http://brickset.com/sets/" rfc))

(defn lego-set-img-url
  [rfc]
  (str "http://images.brickset.com/sets/images/" rfc "-1.jpg"))

(defn lego-img-with-link
  [rfc]
  [:a {:href (lego-set-url rfc)}
   [:img {:width "100%"
          :src (lego-set-img-url rfc)}]])

(defn lego-set-iframe
  [rfc]
  [:iframe {:width "80%"
            :height "100%"
            :src (lego-set-url rfc)}])

(defn set-rfc!
  [_]
  (let [rfc-elem (.getElementById js/document "bad-rfc")
        rfc (.-value rfc-elem)]
    (swap! state assoc :rfc rfc)))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "rfc.help"]
   [:div "<rfc.help> hello friend"]
   [:div "<rfc.help> are you sad about rfc"]
   [:div "<you> yes :("]
   [:div "<rfc.help> which rfc are you sad about?"]
   [:div "<you> i am sad about rfc "
    [:input {:id "bad-rfc"
             :type "number"
             :style {"width" "10%"}
             :placeholder (:rfc @state)
             :on-key-up (fn [e]
                          (when (= (.-keyCode e) 13)
                            (set-rfc! nil)))}]]
   (let [rfc (:rfc @state)]
     (if (empty? rfc)
       [:button
        {:on-click set-rfc!}
        "acquire happies"]
       [:div
        "<rfc.help> i am so sorry :( wouldn't you rather have this:"
        (lego-img-with-link rfc)
        "<you> yes i would"]))])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
