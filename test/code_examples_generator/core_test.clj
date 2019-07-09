(ns code-examples-generator.core-test
  (:require
   [clojure.test :refer :all]
   [clojure.string :as str]
   [clojure.tools.cli :refer [parse-opts]]
   [ring-curl.core :as ring-curl]
   [raml-clj-parser.core :as raml]
   [code-examples-generator.fs-utils :as fs]
   [code-examples-generator.resource-parser :refer [get-requests]]
   [code-examples-generator.test-utils :as u]
   [code-examples-generator.core :refer :all]))


(deftest test-RAML->code-examples
  (testing "context map of code examples"
    (let [ctx-map (atom false)
          request {:ring-request {:mock-ring-request "mock-ring-request"}
                   :desc "description"}]
      (with-redefs [fs/get-RAML-files (constantly [1])
                    raml/read-raml (constantly nil)
                    get-requests (constantly [request])
                    fs/get-dest (constantly nil)
                    ring-curl/to-curl (constantly "cURL")
                    fs/spit-raml-map (constantly nil)
                    fs/save-code-examples (fn [_ m] (reset! ctx-map m))]
        (RAML->code-examples {})
        (is (= "cURL"
               (:curl @ctx-map)))
        (is (= (:desc request)
               (:desc @ctx-map)))
        (is (= (:ring-request request)
               (dissoc @ctx-map :curl :desc)))))))    

(deftest test-validate-args
  (with-redefs [RAML->code-examples (constantly "stub")]
    (testing "displaying help"
      (are [args] (= (validate-args args) (:summary (parse-opts args cli-options)))
        nil '() '("-h") '("--help")))
    (testing "version should be displayed"
      (are [args] (= "v0.1.0" (validate-args args))
        '("-v") '("--version") '("-v" "--help")))
    (testing "missing or incorrect arguments should not trigger code generation"
      (are [args] (not= "stub" (validate-args args))
        '("--blaah") '("-s") '("-d")))
    (testing "code example generation"
      (are [args] (= "stub" (validate-args args))
        (list (str "-s" (u/create-temp-dir)))
        (list (str "-s" (u/create-temp-dir))
              (str "-d" (u/uuid)))))))
       
(deftest test-main
  (testing "outputs the result of argument validation"
    (with-redefs
      [println (fn [s] s)
       validate-args (fn [a] "MOCK_RESPONSE")]
      (is (= "MOCK_RESPONSE" (-main)))
      (are [args] (= "MOCK_RESPONSE" (-main args))
        nil '() '("test")))))
