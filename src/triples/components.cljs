(ns triples.components
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [triples.deck :as deck]
            [goog.string :as gstring]
            [goog.string.format]))

(def ReactNative (js/require "react-native"))
(def ReactNativeSvg (js/require "react-native-svg"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def scrollview (r/adapt-react-class (.-ScrollView ReactNative)))
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

(defn card-component [card index]
  (let [color (::deck/color card)
        hex (get color-map (::deck/color card))
        shape (::deck/shape card)
        number (::deck/number card)
        shading (::deck/shading card)
        selected (subscribe [:get-selected])
        ]
    [touchable {:style {:flexDirection "row"
                        :justifyContent "center"
                        :borderWidth 1
                        :borderColor (if (contains? @selected index) "#f00" "#ddd")
                        :width "30%"
                        :padding 10
                        :margin 2}
                :on-press #(select-card index)}
     (repeat number
             [svg {:height 25 :width 25}
              (cons [
                     (get shape-map shape)
                     (merge
                       (get size-map shape)
                       {:fill (if (= shading :solid) hex "none")}
                       {:strokeWidth 2 :stroke hex}
                       )]
                    (if (= shading :striped)
                      (map
                        (fn [params] [line (merge params {:stroke hex :strokeWidth 2})])
                        (get stripe-map shape))))])]
      ;[text (clojure.string/join " " [color hex shape number shading])]
))

(defn remaining-component [n]
  [text (clojure.string/join " " [n "cards remaining"])])


(defn count-elapsed-time [timestamps]
  (reduce + (map #(apply - (reverse %)) (partition 2 2 [(js/Date.now)]
                                                  timestamps))))

(defn timer-component []
  (defn zpad [i] (gstring/format "%02d" i))
  (defn minutes [milliseconds] (zpad (int (/ milliseconds 60000))))
  (defn seconds [milliseconds] (zpad (mod (int (/ milliseconds 1000)) 60)))
  (let [elapsed-time (r/atom 0)
        timestamps (subscribe [:get-timestamps])
        paused (subscribe [:get-paused])]

    (fn []
      (js/setTimeout #(reset! elapsed-time (count-elapsed-time @timestamps)) 1000)
      [scrollview {:contentContainerStyle {:flex 1 :flex-direction "column"
                                           :align-items "center"
                                           :justify-content "center"}}
        [text "Elapsed time: " (minutes @elapsed-time) ":" (seconds @elapsed-time)]
        [touchable-highlight {:style {:background-color "#999" :padding 10
                                      :border-radius 5} :on-press #(toggle-timer)}
          [text {:style {:color "white" :text-align "center" :font-weight
                         "bold"}}
                (if @paused "Resume" "Pause")]]])))


(defn coll-of-cards [coll]
  [view {:style
         {:flexDirection "row" :flexWrap "wrap" :alignItems "flex-start"}}
   (map-component card-component coll)])
