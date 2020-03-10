(ns recipe-search.inverted-index
  (:require [clojure.set :refer [intersection]]))

(defn- text->words [text] (->> (re-seq #"\w+" text)
                               (map clojure.string/lower-case)))
(defn- index-file 
  ;; add file to index
  ([index file]
   (index-file index file identity))
  ([index file extract-content]
   (with-open [reader (clojure.java.io/reader file)]
     (reduce
      #(assoc %1 %2 (conj (get %1 %2 #{}) file))
      index
      (mapcat text->words (extract-content (line-seq reader)))))))

(defn index-files 
  ;; create inverted index from files
  ([files]
   (reduce index-file {} files))
  ([files line-seq-filter]
   (reduce #(index-file %1 %2 line-seq-filter) {} files)))

(defn search-index 
  ;; return file results for query
  [index query]
  (apply intersection (map index (text->words query))))
