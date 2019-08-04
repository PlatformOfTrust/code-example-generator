(ns code-examples-generator.fs-utils
  "Helper methods related to reading and writing files."
  (:require 
   [clojure.string :as str]
   [clojure.pprint :refer [write]]
   [clojure.java.io :as io]
   [selmer.parser :as selmer]
   [code-examples-generator.formatters :as f]))


;; TODO test that reading a single file works
(defn get-files
  "Read provided `path` and return a file sequence of `java.io.Files` 
   optionally filtered by suffix `s`."
  [path suffix]
  (->> (io/file path)
       file-seq
       (filter #(str/ends-with? (.getName %) suffix))))

;; TODO report if no templates found? Maybe meta?
;; TODO should be part of cli validation?
(defn get-templates
  "Read and return template files from either provided path or
   custom location."
  ([]
   (get-templates "templates"))
  ([path]
   (list "curl" "python.py" "unirest.node.js" "slate.md" "2xx-response.json")))
   ;; (prn path)
   ;; (prn (io/resource path))
   ;; (prn (->> path io/resource .getFile))
   ;; (prn (->> path io/resource .getPath))
   ;; (prn (->> path io/resource io/file type))
   ;; (->> path
   ;;      io/resource
   ;;      io/file
   ;;      file-seq
   ;;      (remove #(or (.isDirectory %)
   ;;                   (str/starts-with? (.getName %) "."))))))

(defn get-dest
  "Return file path for code examples based on:
   a) destination from command line arguments
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
    (spit path (write m :stream nil))))


(defn save-code-examples
  "Read template files, render them with provided `context-map` and save
   as code examples to path provided as `code-examples-dir`. Return list 
   of paths."
  [examples-dir templates context-map]
  (let [examples (atom '())]
    (doseq [template templates]
     (let [code-example-path (str examples-dir "/" template)]
       (->> context-map
            (selmer/render-file (str "templates/" template))
            f/remove-extra-newlines
            (spit code-example-path))
       (swap! examples conj code-example-path)))
    @examples))
