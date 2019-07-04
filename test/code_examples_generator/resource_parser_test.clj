(ns code-examples-generator.resource-parser-test
  (:require
   [clojure.test :refer :all]
   [code-examples-generator.resource-parser :refer :all]))


(deftest test-coercing-example-to-value
  (are [f] (= clojure.lang.PersistentArrayMap (type (coerce-example-to-value f)))
    {} nil)
  (is (= (keys (coerce-example-to-value {:X-Pot-1 {} :X-Pot-2 {:X-Pot-3 nil}}))
         '(:X-Pot-1 :X-Pot-2)))
  (is (= (keys (coerce-example-to-value {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))
         '(:X-Pot-1)))
  (is (= (vals (coerce-example-to-value {:X-Pot-1 {:example "example1"} :X-Pot-2 {:type "String"}}))
         '("example1" nil)))
  (is (= (vals (coerce-example-to-value {:X-Pot-1 {:example "{:X-Pot-2 {:example 1}}"}}))
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


;; TODO requests
;; TODO methods
;; (deftest test-
;;   (is (true? true)))


