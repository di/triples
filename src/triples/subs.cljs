(ns triples.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
  :get-game
  (fn [db _]
    (:current-game db)))

(reg-sub
  :get-selected
  (fn [db _]
    (:selected db)))
