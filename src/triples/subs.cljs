(ns triples.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
  :get-current-game
  (fn [db _]
    (:current-game db)))

(reg-sub
  :get-draw-pile
  (fn [db _]
    (:draw-pile db)))

(reg-sub
  :get-selected
  (fn [db _]
    (:selected db)))
