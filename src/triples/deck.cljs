;; the SET deck and game logic in clojure.spec
;; inspired by https://gist.github.com/cgrand/4985a7ef80c8c85291213437d06d9169

(ns triples.deck
  (:require [clojure.spec :as s]
            [clojure.test.check.generators :as gen]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

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

;; Specs
(s/def ::shape shapes)
(s/def ::color colors)
(s/def ::number numbers)
(s/def ::shading shadings)

(s/def ::card (s/keys :req [::shape ::color ::number ::shading]))

(s/def ::deal
  (s/and
    ; This is wrong, it should check if there is a valid set as well
    (s/coll-of ::card :distinct true :max-count 21 :min-count 12)
    (count-multiple-of 3)))

(s/def ::deck (s/coll-of ::card :distinct true :max-count 81 :min-count 81))

(s/def ::set
  (s/and
    (s/coll-of ::card :min-count 3 :max-count 3 :distinct true)
    (unique-or-distinct ::shape)
    (unique-or-distinct ::color)
    (unique-or-distinct ::number)
    (unique-or-distinct ::shading)))

;; Generators
(defn deal-round []
  "Returns a one off deal randomly generated from specs"
  (gen/generate (s/gen ::deal)))

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

(defn shuffle-cards []
  (-> all-cards count range shuffle vec))

(defn get-many [v, indices]
  (map #(get v %) indices))

(defn valid-set? [indices]
  "Returns the true if the set is valid, false otherwise"
  (s/valid? ::set (set (get-many all-cards indices))))

(defn sets-in [indices]
  "Given a collection of index triples, return sets
  of indices for which ::cards form a valid ::set"
  (into #{}
    (for [c (combo/combinations indices 3)
          :let [combination (set c)]
          :when (valid-set? combination)]
      combination)))

