(ns recipe-search.inverted-index-test
  (:require [clojure.test :refer :all]
            [recipe-search.inverted-index :refer :all]))

(def index (assoc {}
                  "baked" #{"baked potato recipe"}
                  "carrot" #{"carrot recipe"}
                  "recipe" #{"carrot recipe" "potato recipe" "baked potato recipe"}
                  "potato" #{"potato recipe" "baked potato recipe"}))

(deftest search-inverted-index-test
  (testing "Should find one carrot recipe"
    (is (= #{"carrot recipe"} (search-index index "carrot recipe"))))
  (testing "Should find two potato recipies"
    (is (= #{"potato recipe" "baked potato recipe"} (search-index index "potato recipe"))))
  (testing "Should find zero roasted potato recipies"
    (is (nil? (search-index index "roasted potato recipe")))))
