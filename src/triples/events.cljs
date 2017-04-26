(ns triples.events
  (:require
   [re-frame.core :refer [reg-event-db after]]
   [clojure.spec :as s]
   [triples.deck :as deck]
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
 :start-game
 validate-spec
 (fn [db [_ value]]
   (merge db (triples.deck.deal-round) {:timestamps [(js/Date.now)]
                                        :win false})))

(reg-event-db
 :toggle-timer
  validate-spec
 (fn [db [_ value]]
   (let [timestamps (conj (:timestamps db) (js/Date.now))]
   (merge db {:timestamps timestamps
              :paused (even? (count timestamps))}))))

(defn toggle [col val]
  (if (contains? col val)
    (disj col val)
    (conj col val)))

(reg-event-db
 :set-selected
 validate-spec
 (fn [db [_ value]]
   (let [selected (toggle (get db :selected) value)
         cards (deck/get-many (get db :current-game) selected)
         ]
     (if (= 3 (count selected))
       (if (deck/valid-set? cards)
        (-> db
            (assoc :selected selected)
            deck/remove-valid-set
            deck/deselect
            deck/ensure-set
            deck/check-win
            )
        (deck/deselect db))
       (assoc db :selected selected)))))
