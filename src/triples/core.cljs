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
  (dispatch [:set-game (triples.deck.deal-round)])
  (alert "HELLO FRIEND"))

(defn restart-game [game]
  [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
    [text {:style {:text-align "center" :font-weight "bold"}} "Current Game"]
    ; Solid circle
    [svg {:height 25 :width 25} [circle {:cx 13 :cy 13 :r 10 :fill "red"}]]

    ; Empty circle
    [svg {:height 25 :width 25} [circle {:cx 13 :cy 13 :r 9 :stroke "red" :strokeWidth 2 :fill "none"}]]

    ; Dashed circle
    [svg {:height 25 :width 25}
     [circle {:cx 13 :cy 13 :r 9 :stroke "red" :strokeWidth 2 :fill "none"}]
     [line {:x1 5 :y1 8 :x2 5 :y2 18 :stroke "red" :strokeWidth 2}]
     [line {:x1 9 :y1 5 :x2 9 :y2 21 :stroke "red" :strokeWidth 2}]
     [line {:x1 13 :y1 4 :x2 13 :y2 22 :stroke "red" :strokeWidth 2}]
     [line {:x1 17 :y1 5 :x2 17 :y2 21 :stroke "red" :strokeWidth 2}]
     [line {:x1 21 :y1 8 :x2 21 :y2 18 :stroke "red" :strokeWidth 2}]
     ]

    ; Solid square
    [svg {:height 25 :width 25} [rect {:x 4 :y 4 :width 18 :height 18 :fill "red"}]]

    ; Empty square
    [svg {:height 25 :width 25} [rect {:x 5 :y 5 :width 16 :height 16 :stroke "red" :strokeWidth 2 :fill "none"}]]

    ; Dashed square
    [svg {:height 25 :width 25}
     [rect {:x 5 :y 5 :width 16 :height 16 :stroke "red" :strokeWidth 2 :fill "none"}]
     [line {:x1 9 :y1 5 :x2 9 :y2 21 :stroke "red" :strokeWidth 2}]
     [line {:x1 13 :y1 4 :x2 13 :y2 21 :stroke "red" :strokeWidth 2}]
     [line {:x1 17 :y1 5 :x2 17 :y2 21 :stroke "red" :strokeWidth 2}]]

    ; Solid triangle
    [svg {:height 25 :width 25}
     [polygon {:points "3,20 13,3 23,20" :fill "red"}]]

    ; Empty triangle
    [svg {:height 25 :width 25}
     [polygon {:points "4,20 13,4 22,20" :fill "none" :stroke "red" :strokeWidth 2}]]

    ; Dashed triangle
    [svg {:height 25 :width 25}
     [polygon {:points "4,20 13,4 22,20" :fill "none" :stroke "red" :strokeWidth 2}]
     [line {:x1 9 :y1 10 :x2 9 :y2 20 :stroke "red" :strokeWidth 2}]
     [line {:x1 13 :y1 4 :x2 13 :y2 20 :stroke "red" :strokeWidth 2}]
     [line {:x1 17 :y1 10 :x2 17 :y2 20 :stroke "red" :strokeWidth 2}]]

    [triples.components/coll-of-cards @game]
   ])

(defn new-game []
  [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       ;[text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       ;[image {:source logo-img
       ;        :style  {:width 80 :height 80 :margin-bottom 30}}]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(start)}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "New Game"]]])

(defn app-root []
  (let [game (subscribe [:get-game])]
    ;(if (nil? @game)
    ;  #(new-game)
      #(restart-game game)
    ;  )
    ))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "Triples" #(r/reactify-component app-root)))
