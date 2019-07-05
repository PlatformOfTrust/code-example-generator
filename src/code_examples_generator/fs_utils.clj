(ns code-examples-generator.fs-utils
  "Helper methods related to reading and writing files."
  (:require 
   [clojure.string :as str]
   [clojure.java.io :as io]))


;; TODO Maybe output information about reading files
;; e.g. what is the path, how many files. Use with-meta?
;; TODO maybe single file should not have .raml suffix?
(defn get-RAML-files
  "Read provided `path` and return a file sequence of `java.io.Files` 
   filtered by `.raml` suffix."
  [path]
  (->> (io/file path)
       file-seq
       (filter #(str/ends-with? (.getName %) ".raml"))))

;; TODO make it dynamic!
;; TODO implement multiple templates
;; TODO implement template override from CLI
(defn get-templates
  "TODO"
  []
  '("curl" "unirest.node.js" "urllib.py"))

(defn get-dest
  "Return file path for code examples based on:
   a) destinatio from command line arguments
   b) RAML file path relative to `:source`
   c) HTTP resource, name and method"
  [{:keys [source dest]} raml-file {:keys [uri request-method]}]
  (let [resource (str/replace uri "/" "_")
        method (str/upper-case (name request-method))
        file (str/replace-first (.getPath raml-file) source dest)]
    (format "%s/%s/%s" file resource method)))

(defn spit-raml-map
  "Save parsed RAML map (for debugging purposes)"
  [dest m]
  (let [path (str/join "/" (list dest "debug.edn"))]
    (io/make-parents path)
    (spit path (clojure.pprint/write m :stream nil))))
      
