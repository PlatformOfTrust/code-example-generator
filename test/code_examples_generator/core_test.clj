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
   [code-examples-generator.core :refer :all]
   [clojure.java.io :as io]))

;; TODO this is all a bit too much mocking and it would make
;; ;; sense to replace it with an integration test?
;; (deftest test-RAML->code-examples
;;   (testing "context map of code examples"
;;     (let [ctx-map (atom false)
;;           request {:ring-request {:mock-ring-request "mock-ring-request"}
;;                    :desc "description"}]
;;       (with-redefs [fs/get-templates (constantly '(1))
;;                     fs/get-RAML-files (constantly [1])
;;                     raml/read-raml (constantly nil)
;;                     get-requests (constantly [request])
;;                     fs/get-dest (constantly nil)
;;                     ring-curl/to-curl (constantly "cURL")
;;                     fs/spit-raml-map (constantly nil)
;;                     fs/save-code-examples (fn [_ _ m] (reset! ctx-map m))]
;;         (RAML->code-examples {})
;;         (is (= (conj (:ring-request request)
;;                      {:curl "cURL" :desc (:desc request)})
;;                @ctx-map)))))
;;   (testing "save code examples gets triggered for each resource+method"
;;     (let [ctx-map (atom false)
;;           counter (atom 0)
;;           request {:ring-request {:mock-ring-request "mock-ring-request"}
;;                    :desc "description"}]
;;       (with-redefs [fs/spit-raml-map (constantly nil)
;;                     fs/save-code-examples (fn [_ _ _] (swap! counter inc))]
;;         (RAML->code-examples {:source "./test-resources/message-api"
;;                               :dest "./test-doc"})
;;         (is (= 6 @counter))))))

;; This is maybe too detailed test?
;; Ideally it should just check that required data is printed
;; and that calculations are correct
(deftest test-print-info
  (let [lines (atom [])]
    (with-redefs [printf (fn [& args] (swap! lines conj args))]
      (print-info ["e1" "e2" "e3"] ["t1" "t2"] "src" 8 4)
      (is (= (nth @lines 0)
             '("Found %s templates (%s) at provided path: %s.\n" 2 "t1, t2" "src")))
      (is (= (nth @lines 1)
             '("Parsed %s unique requests from %s files. " 8 4)))
      (is (= (nth @lines 2)
             '("Expecting to generate %s code examples.\n" 16)))
      (is (= (nth @lines 3)
             '("Saved %s code examples in %s different languages:\n" 3 2))))))

(deftest test-validate-args
  (with-redefs [RAML->code-examples (constantly "stub")]
    (testing "displaying help"
      (are [args] (= (validate-args args) (:summary (parse-opts args cli-options)))
        nil '() '("-h") '("--help")))
    ;; (testing "version should be displayed"
    ;;   (are [args] (= "v0.1.0" (validate-args args))
    ;;     '("-v") '("--version") '("-v" "--help")))
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
