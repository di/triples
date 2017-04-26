(ns triples.deck
  (:require
    [clojure.math.combinatorics :as combo]))

;; Predicates
(def shapes #{:circle :square :triangle})
(def colors #{:red :green :blue})
(def numbers #{1 2 3})
(def shadings #{:solid :striped :outline})

(def min-deck 3)

(defn count-multiple-of [n]
  #(= 0 (mod (count %) n)))

(defn get-many [col indices]
  (map #(nth col %) indices))

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

(defn remove-valid-set [state]
  (let [current-game (:current-game state)
        selected (:selected state)
        cards (get-many current-game selected)
        draw-pile (:draw-pile state)
        replace-cards (take 3 draw-pile)]

    (merge state (if (or (>= (count current-game) (+ min-deck 3)) (empty? draw-pile))
      {:current-game (remove (set cards) current-game)}
      {:current-game (replace (zipmap cards replace-cards)
                                            current-game)
       :draw-pile (remove (set replace-cards) draw-pile)}))))

(defn deselect [state]
  (merge state {:selected #{}}))

(defn ensure-set [state]
  (let [current-game (:current-game state)
        draw-pile (:draw-pile state)]
    (loop [current-game current-game draw-pile draw-pile] ; this is ugly
      (if (or (valid-set? current-game) (empty? draw-pile))
        (merge state {:current-game current-game :draw-pile draw-pile})
        (recur (concat current-game (take 3 draw-pile))
              (subvec draw-pile 3))))))

(defn check-win [state]
  (let [current-game (:current-game state)
        draw-pile (:draw-pile state)]
    (if (and (not (valid-set? current-game)) (empty? draw-pile))
      (merge state {:win true
                    :timestamps (conj (:timestamps state) (js/Date.now))})
      state)))

(defn deal-round []
  (let [deck (shuffle all-cards)]
    (ensure-set {:current-game (subvec deck 0 min-deck)
                 :draw-pile (subvec deck min-deck)})))
  ;(let [deck all-cards]
  ;  (ensure-set {:current-game (subvec deck 0 6)
  ;               :draw-pile (subvec deck 6 9)})))
