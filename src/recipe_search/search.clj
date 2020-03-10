(ns recipe-search.search
  (:require [clojure.set :refer [difference]]
            [recipe-search.inverted-index :refer [index-files search-index]]
            [recipe-search.bmh :refer [search-bmh needle->shift-table]]))

(defn string->pattern [string]
  ;; regex pattern from string
  (re-pattern (str "(?ism).*?" string ".*?")))

(defn search-results [title-results content-results limit]
  ;; function to return flat list of results, most relevant ones are at the top
  (let [title-results (take limit (title-results))]
    (if (>= (count title-results) limit)
      title-results
      (concat title-results 
              (take (- limit (count title-results))
                    (difference (set (content-results)) (set title-results)))))))

(defprotocol Search
  (search [this query limit]))

(deftype BoyerMooreHorspoolSearch [index]
  Search
  (search [this query limit]
    (let [shift-table (needle->shift-table query)
          query-call (fn [key] #(search-bmh query (key %) shift-table))]
      (map :file (search-results (fn [] (filter (query-call :title) index))
                                 (fn [] (filter (query-call :content) index))
                                 limit)))))

(deftype RegexExactSearch [index]
  Search
  (search [this query limit]
    (let [query-call (fn [key] #(re-matches (string->pattern query) (key %)))]
      (map :file (search-results #(filter (query-call :title) index)
                                 #(filter (query-call :content) index)
                                 limit)))))

(deftype RegexSearch [index]
  Search
  (search [this query limit]
    (map :file (search-results (fn [] (filter #(re-matches (string->pattern (clojure.string/replace query #" " ".*?")) (:title %)) index))
                               (fn [] (filter #(re-matches (string->pattern (clojure.string/replace query #" " ".*?")) (:content %)) index))
                               limit))))

(deftype InvertedIndexSearch [title-index content-index]
  Search
  (search [this query limit]
    (search-results #(search-index title-index query)
                    #(search-index content-index query)
                    limit)))
(defn make-search
  ;; function to call search on selected search engine
  [search-engine search-query limit]
   (search search-engine search-query limit))
