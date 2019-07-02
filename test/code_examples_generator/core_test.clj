(ns code-examples-generator.core-test
  (:require [clojure.test :refer :all]
            [code-examples-generator.core :refer :all]))

(deftest a-test
  (testing "foo"
    (with-redefs [println (fn [s] s)]
      (is (= "Hello, World!" (foo))))))
