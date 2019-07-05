(ns code-examples-generator.resource-parser-test
  (:require
   [clojure.test :refer :all]
   [code-examples-generator.resource-parser :refer :all]))


(deftest test-coercing-examples->values
  (are [f] (= clojure.lang.PersistentArrayMap (type (coerce-examples->values f)))
    {} nil)
  (is (= (keys (coerce-examples->values {:X-Pot-1 {} :X-Pot-2 {:X-Pot-3 nil}}))
         '(:X-Pot-1 :X-Pot-2)))
  (is (= (keys (coerce-examples->values {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))
         '(:X-Pot-1)))
  (is (= (vals (coerce-examples->values {:X-Pot-1 {:example "example1"} :X-Pot-2 {:type "String"}}))
         '("example1" nil)))
  (is (= (vals (coerce-examples->values {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))
         '("{:X-Pot-2 {:example 1}}"))))


(deftest test-get-resources
  (is (= '(["/pot" true] ["/pot/{potId}" {:data "is good!"}])
         (get-resources {:title "Identity"
                         :version "v1"
                         :baseUri {}
                         "ok" {}
                         "/pot" true
                         "/pot/{potId}" {:data "is good!"}
                         "pot" false}))))


;; (deftest test-get-methods)

;; (deftest test-get-requests)
