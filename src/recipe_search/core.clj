(ns recipe-search.core
  (:gen-class)
  (:require [recipe-search.search :as search]
            [recipe-search.inverted-index :refer [index-files]]))


(defn files->search-response [files]
  (map #(assoc {} :id (.getName %) :recipe (slurp %)) files))

;; indexes
(defn normalize-chars
  [text]
  (->> (clojure.string/replace text #"[^a-zA-Z0-9_]" " ")
       (#(clojure.string/replace % #"  +" " "))
       clojure.string/trim
       clojure.string/lower-case))

(defn file->recipe-index [file]
  (->> (slurp file)
       clojure.string/split-lines
       (#(assoc {} :title (normalize-chars (first %))
                :content (normalize-chars (clojure.string/join (drop 1 %)))))
       (merge {:file file})))

(def recipe-files (atom '())) ;; all recipe files
(def fs (atom '())) ;; simple index for Regex and BMH search engine
(def title-index (atom {}))  ;; inverted-index most relevant - only title indexed
(def content-index (atom {})) ;; inverted-index all recipes - without title


(defn load-files
  ;; load recipe files
  []
  (do (reset! recipe-files (->> (clojure.java.io/file "recipes")
                               file-seq
                               (filter #(.isFile %))))
      
      true))

(defn create-indexes
  []
  (do (reset! title-index (index-files @recipe-files #(take 1 %)))
      (reset! content-index (index-files @recipe-files #(drop 1%)))
      (reset! fs
             (map
              file->recipe-index
              @recipe-files))
      true))

(defn init
  ;; load recipies and create indexes
  []
  (do (load-files)
      (create-indexes)))

(defn search-recipe
  ;; search recipie using InvertedIndexSearch
  ([query]
   (search-recipe query 10))
  ([query limit]
   (->> (search/make-search (search/->InvertedIndexSearch @title-index @content-index) query limit)
        files->search-response)))

(defn test-search-recipe
  ;; compare search methods, measure time
  ([query] (test-search-recipe query 10))
  ([query limit]
   (let [execute (fn [search-engine]
                   (time
                    (println (count (search/make-search search-engine query limit)))))]
     (println "BoyerMooreHorspoolSearch")
     (execute (search/->BoyerMooreHorspoolSearch @fs))
     (println "RegexExactSarch")
     (execute (search/->RegexExactSearch @fs))
     (println "RegexSarch")
     (execute (search/->RegexSearch @fs))
     (println "InvertedIndexSearch")
     (execute (search/->InvertedIndexSearch @title-index @content-index)))))

(defn -main
  []
  (println "Recipe search experiment!"))
