(ns triples.deck)

;; Predicates
(def shapes #{:circle :square :triangle})
(def colors #{:red :green :blue})
(def numbers #{1 2 3})
(def shadings #{:solid :striped :outline})

(defn count-multiple-of [n]
  #(= 0 (mod (count %) n)))

(defn unique-or-distinct [feature]
  (fn [s]
    ;; could be faster by exiting early
    (not= 2 (count (into #{} (map feature) s)))))

(defn deal-round []
  (let [deck (shuffle all-cards)]
    {:current-game (subvec deck 0 12)
    :draw-pile (subvec deck 12)})
  )

(defn unique-or-distinct [col feature]
  (not= 2 (count (into #{} (map feature) col))))

(defn valid-set? [cards]
  (and
    (= 3 (count cards))
    (unique-or-distinct cards ::shape)
    (unique-or-distinct cards ::color)
    (unique-or-distinct cards ::number)
    (unique-or-distinct cards ::shading)))

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
