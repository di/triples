(ns triples.runner
  (:require [clojure.test :refer [run-all-tests]]
            [triples.core-test]))

(enable-console-print!)
(set! *main-cli-fn* #(run-all-tests #"triples.*-test"))
