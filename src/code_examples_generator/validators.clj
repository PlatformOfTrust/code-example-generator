(ns code-examples-generator.validators
  "Validators for command line arguments."
  (:require [clojure.java.io :as io]))


(defn is-valid-source?
  "Source must be a file or directory."
  [s]
  (let [file (io/file s)]
    (or (.isDirectory file) (.exists file))))

(defn is-valid-dest?
  "Destination must be a writable directory or a path that does not exist."
  [s]
  ;; TODO use macro for this!
  (let [file (io/file s)]
    (or (not (.exists file))
        (and (.isDirectory file)
             (.canWrite file)))))

(defn is-valid-scheme?
  "Valid schemes are `http` and `https`."
  [s]
  (or (= "http" s) (= "https" s)))

;; TODO test that directory has at least one file excluding .. and .
(defn is-valid-templates-dir?
  "Templates dir must be a directory containing at least one file."
  [s]
  (let [file (io/file s)]
    (.isDirectory file)))
