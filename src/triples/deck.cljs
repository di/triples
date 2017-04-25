(ns triples.deck
  (:require
    [clojure.math.combinatorics :as combo]))

;; Predicates
(def shapes #{:circle :square :triangle})
(def colors #{:red :green :blue})
(def numbers #{1 2 3})
(def shadings #{:solid :striped :outline})

(def min-deck 12)

(defn count-multiple-of [n]
  #(= 0 (mod (count %) n)))

(defn unique-or-distinct [feature]
  (fn [s]
    ;; could be faster by exiting early
    (not= 2 (count (into #{} (map feature) s)))))

(defn deal-round []
  (let [deck (shuffle all-cards)]
    (ensure-set {:current-game (subvec deck 0 min-deck)
                 :draw-pile (subvec deck min-deck)})))

(defn ensure-set [{:keys [current-game draw-pile]}]
  (loop [current-game current-game draw-pile draw-pile] ; this is ugly
    (if (valid-set? current-game)
      {:current-game current-game
       :draw-pile draw-pile}
      (recur (concat current-game (take 3 draw-pile))
             (subvec draw-pile 3)))))

(defn unique-or-distinct [col feature]
  (not= 2 (count (into #{} (map feature) col))))

(defn is-set? [cards]
  (and
    (= 3 (count cards))
    (unique-or-distinct cards ::shape)
    (unique-or-distinct cards ::color)
    (unique-or-distinct cards ::number)
    (unique-or-distinct cards ::shading)))

(defn valid-set? [cards]
  (boolean (some is-set? (combo/combinations cards 3))))

(def all-cards
  "Full 81 card deck"
  (vec (for [shape shapes
             color colors
             number numbers
             shading shadings]
   {::shape shape
    ::color color
    ::number number
    ::shading shading})))
