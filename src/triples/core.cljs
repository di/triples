(ns triples.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [triples.events]
            [triples.components :as components]
            [triples.styles :as styles]
            [triples.utils :as utils]
            [triples.subs]))

(def ReactNative (js/require "react-native"))
(def ReactNativeSvg (js/require "react-native-svg"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def scrollview (r/adapt-react-class (.-ScrollView ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def svg (r/adapt-react-class (.-Svg ReactNativeSvg)))
(def circle (r/adapt-react-class (.-Circle ReactNativeSvg)))
(def line (r/adapt-react-class (.-Line ReactNativeSvg)))
(def rect (r/adapt-react-class (.-Rect ReactNativeSvg)))
(def polygon (r/adapt-react-class (.-Polygon ReactNativeSvg)))

(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn restart-game []
  (let [current-game (subscribe [:get-current-game])
        draw-pile (subscribe [:get-draw-pile]) ]
  [view {:style styles/view}
    [text {:style styles/toptext} "Current Game"]
    [components/coll-of-cards @current-game]
    [components/remaining-component (count @draw-pile)]
    [components/timer-component]]))

(defn message-screen [message button action & [subtext]]
  [scrollview {:contentContainerStyle styles/scrollview}
    [text {:style styles/headertext} message]
    (if subtext [text {:style styles/subtext} subtext])
    [touchable-highlight {:style styles/button
                          :on-press #(dispatch [action])}
      [text {:style styles/buttontext} button]]])

(defn app-root []
  (let [current-game (subscribe [:get-current-game])
        win (subscribe [:get-win])
        paused (subscribe [:get-paused])
        timestamps (subscribe [:get-timestamps])]
    (cond
      (nil? @current-game) (message-screen "No game in progress." "New Game" :start-game)
      @paused (message-screen "Game is paused." "Resume" :toggle-timer)
      @win (message-screen "Gameover." "New Game" :start-game (str "Elapsed time: " (utils/elapsed-time-str @timestamps)))
      :else (restart-game))))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "Triples" #(r/reactify-component app-root)))
