(ns code-examples-generator.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [clojure.string :as str]
   [raml-clj-parser.core :as raml]
   [ring-curl.core :as ring-curl]
   [code-examples-generator.fs-utils :as fs]
   [code-examples-generator.resource-parser :refer [get-requests]]
   [code-examples-generator.validators :refer :all])
  (:gen-class))


;; TODO cover this w/ integration tests
(defn RAML->code-examples
  "Read RAML files from `source`, find all unique HTTP requests and save examples 
   in different languages to `dest` folder defined in `cli-args`."
  [{:keys [source dest sha] :as cli-args}]
  (doseq [file (fs/get-RAML-files source)]
    (doseq [{:keys [ring-request desc]}
            (get-requests (raml/read-raml file) cli-args)]
      (let [examples-dir (fs/get-dest cli-args file ring-request)
            curl (ring-curl/to-curl ring-request)
            context-map (conj ring-request {:curl curl :desc desc})]
        (fs/spit-raml-map examples-dir (raml/read-raml file))
        (fs/save-code-examples examples-dir context-map))))
  "raml->HTTP")

;; TODO add test that cli opts must not use any of the ring-request params!?
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
   ;; ["-c" "--sha SHA1" "Optional git commit hash to show in footer of code examples."]
   ["-h" "--help"]
   ["-v" "--version"]])

;; TODO report RAML parser linter!?
;; cannot output files. Proper error!
;; TODO non-fixed version!
(defn validate-args
  "Validate command line arguments and trigger either example generation 
   or show help and/or errors."
  [args]
  (let [{:keys [options summary errors] :as kk} (parse-opts args cli-options)]
    (cond
      (:version options) "v0.1.0"
      (not (nil? errors)) (str/join \newline errors)
      (or (:help options) (empty? (:source options))) summary 
      :else (RAML->code-examples options))))

(defn -main [& args] (println (validate-args args)))
