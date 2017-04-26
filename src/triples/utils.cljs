(ns triples.utils
  (:require [goog.string :as gstring]
            [goog.string.format]))

(defn zpad [i] (gstring/format "%02d" i))

(defn minutes [milliseconds] (zpad (int (/ milliseconds 60000))))

(defn seconds [milliseconds] (zpad (mod (int (/ milliseconds 1000)) 60)))

(defn elapsed-time-str [timestamps]
  (let [ms (reduce + (map #(apply - (reverse %))
                          (partition 2 2 [(js/Date.now)] timestamps)))]
    (str (minutes ms) ":" (seconds ms))))
