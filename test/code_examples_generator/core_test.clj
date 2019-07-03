(ns code-examples-generator.core-test
  (:require
   [clojure.test :refer :all]
   [clojure.string :as str]
   [clojure.tools.cli :refer [parse-opts]]
   [code-examples-generator.core :refer :all]))



(deftest test-help
  (are [args] (= (validate-args args) (:summary (parse-opts args cli-options)))
    nil '() '("-h") '("--help")))

(deftest test-errors
  (are [args] (str/starts-with? (validate-args args) "Unknown option:")
    '("--blaah")))


(deftest test-version
  (are [args] (= "v0.1.0" (validate-args args))
    '("-v") '("--version") '("-v" "--help")))


(deftest test-main-method
  (testing "Main method outputs the result of argument validation."
    (with-redefs
      [println (fn [s] s)
       validate-args (fn [a] "MOCK_RESPONSE")]
      (is (= "MOCK_RESPONSE" (-main)))
      (are [args] (= "MOCK_RESPONSE" (-main args))
        nil '() '("test")))))
