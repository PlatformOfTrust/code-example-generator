(ns code-examples-generator.resource-parser-test
  (:require
   [clojure.test :refer :all]
   [code-examples-generator.resource-parser :refer :all]))


(deftest test-coercing-examples->values
  (testing "always return a map"
    (are [f] (= clojure.lang.PersistentArrayMap (type (coerce-examples->values f)))
      {} nil))
  (testing "keys"
    (are [m]  (= (keys m) (keys (coerce-examples->values m)))
      {:X-Pot-1 {}}
      {:X-Pot-1 {} :X-Pot-2 {:X-Pot-3 nil}}
      {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))      
  (testing ":example key values are treated as return values"
    (is (= (vals (coerce-examples->values {:X-Pot-1 {:example "example1"} :X-Pot-2 {:type "String"}}))
           '("example1" nil)))
    (is (= (vals (coerce-examples->values {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))
           '("{:X-Pot-2 {:example 1}}")))))


(deftest test-get-resources
  (testing "valid resource keys are only strings prefixed with forward slash `/`"
    (is (= '(["/pot" true] ["/pot/{potId}" {:data "is good!"}])
           (get-resources {:title "Identity"
                           :version "v1"
                           :baseUri {}
                           "ok" {}
                           "/pot" true
                           "/pot/{potId}" {:data "is good!"}
                           "pot" false})))))
