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


(defn print-info
  [examples, templates, source, requests, fcount]
  (let [example-count (count examples)
        template-count (count templates)]
    (printf "Found %s templates (%s) at provided path: %s.\n"
            template-count
            (str/join ", " templates)
            source)
    (printf "Parsed %s unique requests from %s files. "
            requests
            fcount)
    (printf "Expecting to generate %s code examples.\n"
            (* requests template-count))
    (newline)
    (printf "Saved %s code examples in %s different languages:\n"
            example-count
            template-count)))

(defn RAML->code-examples
  "Read RAML files from `source`, find all unique HTTP requests and save examples 
   in different languages to `dest` folder defined in `cli-args`."
  [{:keys [source dest] :as cli-args}]
  (let [templates (fs/get-templates)
        suffix ".raml"
        files (fs/get-files source suffix)
        fcount (count files)
        requests (atom 0)
        examples (atom '())]
    (printf "Found %s files matching suffix '%s' at %s.\n" fcount suffix source)
    (doseq [file files]
      (doseq [{:keys [ring-request desc ok]}
              (get-requests (raml/read-raml file) cli-args)]
        (swap! requests inc)
        (let [examples-dir (fs/get-dest cli-args file ring-request)
              curl (ring-curl/to-curl ring-request)
              context-map (conj ring-request {:curl curl :desc desc :ok ok})]
          (fs/spit-raml-map examples-dir (raml/read-raml file))
          (swap! examples conj (fs/save-code-examples examples-dir templates context-map)))))
    (let [total-examples (flatten @examples)
          template-count (count templates)]
      (print-info total-examples templates, source @requests fcount)
      (str/join \newline total-examples))))

;; TODO add test that cli opts must not use any of the ring-request params!?
(def cli-options
  "Specs for CLI options according to https://github.com/clojure/tools.cli"
  [["-s" "--source PATH" "Required RAML file or a directory that contains RAML files."
    :validate [is-valid-source? "Not a directory or file!"]]
   ["-d" "--dest PATH" "Optional Directory for generated code examples."
    :default "./pot-examples"
    :validate [is-valid-dest? "Not writeable or not a directory!"]]
   ["-H" "--host HOST" "Optional URI host."
    :default "api.oftrust.net"
    :validate [is-valid-host? "Host missing or invalid!"]]
   ["-S" "--scheme SCHEME" "Optional URI scheme (`https` or `http`)."
    :default "https"
    :validate [is-valid-scheme? "Invalid scheme. Allowed values are `https`, `http`"]]
   ["-h" "--help"]
   ["-v" "--version"]])

;; TODO report RAML parser linter!?
;; cannot output files. Proper error!?
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
