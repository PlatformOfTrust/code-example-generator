(ns code-examples-generator.core-test
  (:require
   [clojure.test :refer :all]
   [clojure.string :as str]
   [clojure.tools.cli :refer [parse-opts]]
   [code-examples-generator.core :refer :all]))



(deftest test-help
  (testing "displaying help"
    (are [args] (= (validate-args args) (:summary (parse-opts args cli-options)))
      nil '() '("-h") '("--help"))))

(deftest test-errors
  (testing "passing incorrect parameters should result in error message"
    (are [args] (str/starts-with? (validate-args args) "Unknown option:")
      '("--blaah"))))


(deftest test-version
  (testing "version should be displayed"
    (are [args] (= "v0.1.0" (validate-args args))
      '("-v") '("--version") '("-v" "--help"))))


(deftest test-main-method
  (testing "outputs the result of argument validation"
    (with-redefs
      [println (fn [s] s)
       validate-args (fn [a] "MOCK_RESPONSE")]
      (is (= "MOCK_RESPONSE" (-main)))
      (are [args] (= "MOCK_RESPONSE" (-main args))
        nil '() '("test")))))
