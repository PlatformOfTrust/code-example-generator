(ns code-examples-generator.fs-utils
  "Helper methods related to reading and writing files."
  (:require 
   [clojure.string :as str]
   [clojure.pprint :refer [write]]
   [clojure.java.io :as io]
   [selmer.parser :as selmer]))


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

;; TODO report if no templates found? Maybe meta?
;; TODO should be part of cli validation?
(defn get-templates
  "Read and return template files from either provided path or
   custom location."
  ([]
   (get-templates "./resources/templates/"))
  ([path]
   (->> (io/file path)
        file-seq
        (remove #(or (.isDirectory %)
                     (str/starts-with? (.getName %) "."))))))

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
    (spit path (write m :stream nil))))

(defn- render-template
  "Render template"
  [file context-map]
  (prn file)
  ;; Selmer template engine reads files relative to ClassLoader URL by default - 
  ;; https://github.com/yogthos/Selmer#resource-path.
  ;; Overwrite resource path to make it possible to read templates from custom
  ;; location.
  (-> file .getParentFile .getAbsolutePath selmer.parser/set-resource-path!)
  (selmer/render-file (.getName file) context-map))

;; TODO implement custom path!
(defn save-code-examples
  "Read template files, render them with provided `context-map` and save
   as code examples to path provided as `code-examples-dir`."
  [examples-dir context-map]
  (doseq [template (get-templates)]
    (let [code-example-path (str examples-dir "/" (.getName template))
          content (render-template template context-map)]
      (spit code-example-path content))))
