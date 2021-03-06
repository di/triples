(ns triples.components
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [triples.deck :as deck]
            [triples.styles :as styles]
            [triples.utils :as utils]))

(def ReactNative (js/require "react-native"))
(def ReactNativeSvg (js/require "react-native-svg"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def svg (r/adapt-react-class (.-Svg ReactNativeSvg)))
(def circle (r/adapt-react-class (.-Circle ReactNativeSvg)))
(def line (r/adapt-react-class (.-Line ReactNativeSvg)))
(def rect (r/adapt-react-class (.-Rect ReactNativeSvg)))
(def polygon (r/adapt-react-class (.-Polygon ReactNativeSvg)))

(def color-map
  {:red "#E79F26"
   :green "#069F73"
   :blue "#5CB4E4"})

(def shape-map
  {:circle circle
   :square rect
   :triangle polygon})

(def size-map
  {:circle {:cx 13 :cy 13 :r 10}
   :square {:x 4 :y 4 :width 18 :height 18}
   :triangle {:points "3,20, 13,3, 23,20"}})

(def stripe-map
  {:circle [{:x1 5 :y1 7 :x2 5 :y2 19}
            {:x1 9 :y1 4 :x2 9 :y2 22}
            {:x1 13 :y1 4 :x2 13 :y2 22}
            {:x1 17 :y1 4 :x2 17 :y2 22}
            {:x1 21 :y1 7 :x2 21 :y2 19}]
   :square [{:x1 9 :y1 5 :x2 9 :y2 21}
            {:x1 13 :y1 4 :x2 13 :y2 21}
            {:x1 17 :y1 5 :x2 17 :y2 21}]
   :triangle [{:x1 9 :y1 10 :x2 9 :y2 20}
              {:x1 13 :y1 4 :x2 13 :y2 20}
              {:x1 17 :y1 10 :x2 17 :y2 20}]})

(defn map-component [component coll]
  "Adds keys to component"
  (map (fn [item n] ^{:key n} [component item n])
       coll (range)))

(defn select-card [index]
  (dispatch [:set-selected index]))

(defn toggle-timer []
  (dispatch [:toggle-timer]))


(defn stripe-components [card]
  (let [color (::deck/color card)
        hex (get color-map (::deck/color card))
        shape (::deck/shape card)]
    (map-component
      line
      (map #(merge % {:stroke hex :strokeWidth 2}) (get stripe-map shape)))))

(defn shape-component [card]
  (let [color (::deck/color card)
        hex (get color-map (::deck/color card))
        shape (::deck/shape card)
        shading (::deck/shading card)]
    [svg {:height 25 :width 25}
      [(get shape-map shape)
       (merge
         (get size-map shape)
         {:fill (if (= shading :solid) hex "none")}
         {:strokeWidth 2 :stroke hex})]
       (if (= shading :striped)
         (stripe-components card))]))

(defn card-component [card index]
  (let [number (::deck/number card)
        selected (subscribe [:get-selected])]
    [touchable {:style {:flexDirection "row"
                        :justifyContent "center"
                        :borderWidth 1
                        :borderColor (if (contains? @selected index) "#f00" "#ddd")
                        :width "30%"
                        :padding 10
                        :margin 2}
                :on-press #(select-card index)}
     (map-component shape-component (repeat number card))]))

(defn remaining-component [n]
  [text (clojure.string/join " " [n "cards remaining"])])

(defn timer-component []
  (let [this (r/current-component)
        id (js/setInterval #(r/force-update this true) 1000)
        timestamps (subscribe [:get-timestamps])]
    (r/create-class
      {:reagent-render
        (fn []
          [view
            [text "Elapsed time: " (utils/elapsed-time-str @timestamps)]
            [touchable-highlight {:style styles/button :on-press #(toggle-timer)}
              [text {:style styles/buttontext} "Pause"]]])
        :component-will-unmount #(js/clearInterval id)})))

(defn coll-of-cards [coll]
  [view {:style {:flexDirection "row" :flexWrap "wrap"
                 :alignItems "flex-start"}}
   (map-component card-component coll)])
