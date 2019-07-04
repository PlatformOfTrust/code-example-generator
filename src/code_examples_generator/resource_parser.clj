(ns code-examples-generator.resource-parser
  "Methods for retrieving API resources."
  (:require
   [clojure.string :as str]
   [ring.util.codec :refer [form-encode]]))


;; TODO maybe should URL encode query params somewhere here?
;; TODO maybe report that certain values are nil? It will be easier to fix the docs.
;; TODO maybe not the best name for this method?
(defn coerce-example-to-value
  "Parse node and return a map of key-value pairs where key is key and 
   value is taken from `:example`. Not recursive!"
  [m]
  (into {} (map (fn [[k {v :example}]] [k v]) m)))

(defn get-resources
  "Returns a sequence of vectors where each one consists of two values:
   a) resource name (identified as a `/`-prefixed string )
   b) resource node
   e.g.  '([s m] [s m])"
  [m]
  (let [keys (->> m
                  keys
                  (filter #(and (string? %)
                                (str/starts-with? % "/")))
                 set)]
    (filter (fn [[k _]] (contains? keys k)) m)))

  
