(ns triples.components
  (:require [reagent.core :as r]
            [triples.deck :as deck]))

(def ReactNative (js/require "react-native"))

(def view (r/adapt-react-class (.-View ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))

(def color-map
  {:red "#F44948"
   :green "#71F21F"
   :blue "#4FCCEF"})

(defn border-styles [shading color shape]
  (cond
    (= shading :outline)
      { :border (str "1px solid " color)}
    (= shading :striped)
      { :border (str "1px solid " color)
        :background (str
                    "repeating-linear-gradient("
                    (if (= shape :triangle) "0" "45")
                    "deg, "
                    color ", "
                    color " 1px, "
                    "#fff 1px, "
                    "#fff 3px)")}

    (= shading :solid) {:background-color color}))

(defn map-component [component coll]
  "Adds keys to component"
  (map (fn [item n] ^{:key n} [component item])
       coll (range)))

(defn shape [card]
  (let [color ((::deck/color card) color-map)
        shape (::deck/shape card)
        number (::deck/number card)
        shading (::deck/shading card)
        border (border-styles shading color shape)
        style (merge border)]
    [text (clojure.string/join " " [(name shape) (name color) number (name shading)]);{:class (str "shape" " " (name shape)) :style style} ""
    ]))

(defn card [card]
  [view ;{:class "card"}
    (for [n (range (::deck/number card))]
      ^{:key n} [shape card])])

(defn coll-of-cards [coll]
  [view ;{:class "card-collection"}
    (map-component card coll)])
