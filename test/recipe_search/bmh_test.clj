(ns recipe-search.bmh-test
  (:require [clojure.test :refer :all]
            [recipe-search.bmh :refer :all]))

(deftest search-bmh-test
  (testing "Should find position of string in same string"
    (is (= 0 (search-bmh "string" "string" (needle->shift-table "string")))))
  (testing "Should find position of string in string"
    (is (= 5 (search-bmh "string" "here string another" (needle->shift-table "string")))))
  (testing "Should not find position of string in string without match"
    (is (= false (search-bmh "string" "this is totaly random" (needle->shift-table "string")))))
  (testing "Should not find position of string in empty string"
    (is (= false (search-bmh "string" "" (needle->shift-table "string"))))))
