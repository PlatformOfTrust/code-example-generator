(ns code-examples-generator.formatters
  (:require 
   [clojure.string :as str]
   [cheshire.core :as json]))

(defn remove-extra-newlines
  ""
  [s]
  (->> (str/split-lines s)
       ;; really crude version of replacing the double newlines
       ;; it relies on indentation.
       ;; maybe should be if 1 + n spaces?
       (remove #(or (= "    " %) (= "  " %)))
       (str/join \newline)))

(defn pretty-print
  "TODO"
  [m kw]
  (if (empty? (get m kw))
    m
    (assoc m kw (json/generate-string (get m kw) {:pretty {:indentation "    "}}))))

(defn pretty-print-curl
  "TODO"
  [m kw]
  (let [curl (get m kw)]
    m))
