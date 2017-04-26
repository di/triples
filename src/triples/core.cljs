(ns triples.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [triples.events]
            [triples.components :as components]
            [triples.styles :as styles]
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

(defn start []
  (dispatch [:start-game]))

(defn restart-game []
  (let [current-game (subscribe [:get-current-game])
        draw-pile (subscribe [:get-draw-pile]) ]
  [scrollview {:contentContainerStyle styles/scrollview}
    [text {:style styles/toptext} "Current Game"]
    [components/coll-of-cards @current-game]
    [components/remaining-component (count @draw-pile)]
    [components/timer-component]]))

(defn new-game []
  [scrollview {:contentContainerStyle styles/scrollview}
    [text {:style styles/headertext} "No game in progress."]
    [touchable-highlight {:style styles/button :on-press #(start)}
      [text {:style styles/buttontext} "New Game"]]])

(defn app-root []
  (let [current-game (subscribe [:get-current-game])]
    (if (nil? @current-game)
      (new-game)
      (restart-game))))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "Triples" #(r/reactify-component app-root)))
