(ns triples.events
  (:require
   [re-frame.core :refer [reg-event-db after]]
   [clojure.spec :as s]
   [triples.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :set-greeting
 validate-spec
 (fn [db [_ value]]
   (assoc db :greeting value)))

(reg-event-db
 :set-game
 validate-spec
 (fn [db [_ value]]
   (assoc db :current-game value)))

(defn toggle [col val]
  (if (contains? col val)
    (disj col val)
    (conj col val)))

(defn get-many [col indices]
  (map #(nth col %) indices))

(defn unique-or-distinct [col feature]
  (not= 2 (count (into #{} (map feature) col))))

(defn valid-set? [cards]
  (and
    (unique-or-distinct cards :triples.deck/shape)
    (unique-or-distinct cards :triples.deck/color)
    (unique-or-distinct cards :triples.deck/number)
    (unique-or-distinct cards :triples.deck/shading)))

(reg-event-db
 :set-selected
 validate-spec
 (fn [db [_ value]]
   (let [selected (toggle (get db :selected) value)
         cards (get-many (get db :current-game) selected)
         ]
     (prn (valid-set? cards)) ; Remove this
     (if (= 3 (count selected))
      (assoc db :selected #{})
      (assoc db :selected selected)))))
