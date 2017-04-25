(ns triples.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [triples.events]
            [triples.components]
            [triples.deck]
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
  (dispatch [:set-game (triples.deck.deal-round)]))

(defn restart-game []
  (let [current-game (subscribe [:get-current-game])
        draw-pile (subscribe [:get-draw-pile])
        ]
  [scrollview {:contentContainerStyle {:flex 1 :flex-direction "column" :marginTop 40 :align-items "center"}}
    [text {:style {:text-align "center" :font-weight "bold"}} "Current Game"]
    [triples.components/coll-of-cards @current-game]
    [triples.components/remaining-component (count @draw-pile)]
   ]))

(defn new-game []
  [scrollview {:contentContainerStyle {:flex 1 :flex-direction "column" :align-items "center" :justify-content "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "No game in progress."]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(start)}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "New Game"]]])

(defn app-root []
  (let [current-game (subscribe [:get-current-game])]
    (if (nil? @current-game)
      (new-game)
      (restart-game)
      )
    ))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "Triples" #(r/reactify-component app-root)))
