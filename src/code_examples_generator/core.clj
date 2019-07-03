(ns code-examples-generator.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.string :as str]
   [code-examples-generator.validators :refer :all])
  (:gen-class)) 


;; (defn format-summary
;;   "Split tools.deps.cli summary to "
;;   [s]
;;   (str/join (str/split s (re-pattern "\n")) \newline))


(defn RAML->HTTP-examples
  [s]
  "raml->HTTP")


;; TODO add test that cli opts must not use any of the ring-request params!?
;; TODO break up into cli-validators for easier testing?
(def cli-options
  "Specs for CLI options according to https://github.com/clojure/tools.cli"
  [["-s" "--source PATH" "Required RAML file or a directory that contains RAML files."
    :validate [is-valid-source? "Not a directory or file!"]]
   ["-d" "--dest PATH" "Optional Directory for generated code examples."
    :default "./pot-examples"
    :validate [is-valid-dest? "Not writeable or not a directory!"]]
   ["-H" "--host HOST" "Required URI host e.g. `pot.org`."
    :default "pot.org"]
   ["-S" "--scheme SCHEME" "Optional URI scheme (`https` or `http`)."
    :default "https"
    :validate [is-valid-scheme? "Invalid scheme. Allowed values are `https`, `http`"]]
   ;; TODO enable that
   ;; ["-t" "--templates PATH" "Optional path for custom templates directory. Can be used
   ;;                           to override built in templates."]
   ["-c" "--sha SHA1" "Optional git commit hash to show in footer of code examples."]
   ["-h" "--help"]
   ["-v" "--version"]])


;; TODO report RAML parser linter!?
;; cannot output files. Proper error!
(defn validate-args
  "Validate command line arguments and trigger either example generation 
   or show help and/or errors."
  [args]
  (let [{:keys [options summary errors] :as kk} (parse-opts args cli-options)]
    (cond
      (:version options) "v0.1.0"
      (not (nil? errors)) (str/join \newline errors)
      (or (:help options) (empty? (:source options))) summary 
      :else (RAML->HTTP-examples options))))
  

(defn -main [& args] (println (validate-args args)))
