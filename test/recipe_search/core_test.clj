(ns recipe-search.core-test
  (:require [clojure.test :refer :all]
            [recipe-search.core :refer :all]))

(defn test-fixture [f]
        (init)
        (f))

(use-fixtures :once test-fixture)

(deftest acceptance-criteria
  (testing "Search results should be relevant, e.g. a search for broccoli stilton soup should return at least broccoli stilton soup."
    (let [results (search-recipe "broccoli stilton soup")] 
      (is (= "broccoli-soup-with-stilton.txt" (:id (first results)))))))
