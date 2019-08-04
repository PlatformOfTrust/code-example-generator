(ns code-examples-generator.formatters
  (:require 
   [clojure.string :as str]
   [cheshire.core :as json]))

(defn remove-extra-newlines
  "Conditional is selmer templates can result in empty newlines.
   Remove them."
  [s]
  (->> (str/split-lines s)
       ;; really crude version of replacing the double newlines
       ;; it relies on indentation.
       ;; maybe should be if 1 + n spaces?
       (remove #(or (= "    " %) (= "  " %)))
       (str/join \newline)))

(defn pretty-print
  "Convert a map identified by kw to a pretty printed JSON string"
  [m kw]
  (if (empty? (get m kw))
    m
    (assoc m kw (json/generate-string (get m kw) {:pretty {:indentation "    "}}))))

(defn- prepend-newline
  "Match a string w/ preceding whitespace and prepend it with:
   - a backslash character
   - a newline
   - 5 whitespace characters for indendation purposes"
  [s match]
  (str/replace s (re-pattern (str " " match)) (str " \\\\\n     " match)))

(defn- append-newline
  "Match a string and append a newline."
  [s match]
  (str/replace s (re-pattern match) (str match "\\\\\n")))

(defn pretty-print-curl
  "Split cURL to multiple lines for better readability"
  [m kw]
  (let [curl (get m kw)
        data-str "--data-binary "
        formatted-curl (-> curl
                           (prepend-newline "-H")
                           (prepend-newline data-str)
                           (append-newline data-str))]
    (assoc m kw formatted-curl)))
    
