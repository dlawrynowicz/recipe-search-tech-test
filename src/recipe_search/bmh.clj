(ns recipe-search.bmh)

;; https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore%E2%80%93Horspool_algorithm

(defn- char-code-at [s i]
  (int (get s i)))

(defn- same? [string1 string2 len]
  (loop [i (dec len)]
    (cond
      (= (char-code-at string1 i) (char-code-at string2 i))
      (if (= i 0)
        true
        (recur (dec i)))
      :else false)))

(defn normalize-chars
  [text]
  (->> (clojure.string/replace text #"[^a-zA-Z0-9_]" " ")
       (#(clojure.string/replace % #"  +" " "))
       clojure.string/trim
       clojure.string/lower-case))

(defn needle->shift-table [needle]
  ;; make character offset table from needle
  (let [len (count needle)]
    (loop [i 0
           table (transient (vec (repeat 256 len)))]
      (if-not (< i (dec len))
        (persistent! table)
        (recur
         (inc i)
         (assoc!
          table
          (char-code-at needle i)
          (- len 1 i)))))))

(defn search-bmh [needle haystack shift-table]
  ;; search haystack and return needle position
  (let [needle-length (count needle)
        haystack-length (count haystack)]
    (loop [skip 0]
      (cond
        (>= (+ skip (dec needle-length)) haystack-length)
        false
        (>= (- haystack-length skip) needle-length)
        (if (same? (subs haystack skip) needle needle-length)
          skip
          (recur (+ skip (nth shift-table (char-code-at haystack (+ skip (dec needle-length)))))))
        :else false))))
