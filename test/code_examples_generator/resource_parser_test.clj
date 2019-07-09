(ns code-examples-generator.resource-parser-test
  (:require
   [clojure.test :refer :all]
   [clojure.edn :as edn]
   [code-examples-generator.resource-parser :refer :all]
   [code-examples-generator.test-utils :as u]
   [ring.util.codec :refer [form-encode]]))


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

(deftest test-get-ring-request
  (testing "ring-request keys"
    (with-redefs [coerce-examples->values (constantly "stub")]
      (is (= '(:request-method
               :server-name
               :scheme
               :uri
               :query-string
               :body
               :headers)
             (keys (get-ring-request {} :get "pot.org" "https" "/"))))))
  (testing "no empty or nil values"
    (is (= '(:request-method :uri :query-string)
           (keys (get-ring-request {:queryParameters {:test {:example "ok"}}
                                    :body {}}
                                   :get nil "" "/")))))
  (testing "headers exist"
    (with-redefs [coerce-examples->values (fn [m] m)]
      (let [h {:test "headers"}]
        (is (= h (:headers (get-ring-request {:headers h} "" "" "" "")))))))
  (testing "body parameters exist"
    (with-redefs [coerce-examples->values (fn [m] m)]
      (let [b {:test "body"}]
        (is (= b (:body (get-ring-request {:body b} "" "" "" "")))))))
  (testing "query parameters should get url encoded"
    (with-redefs [coerce-examples->values (fn [m] m)]                                  
      (let [q {:test "query"}
            r (get-ring-request {:queryParameters q} "" "" "" "")]
        (is (= (form-encode q) (:query-string r)))))))
;; TODO also test body and headers!

(deftest test-get-methods
  (testing "always return a map"
    (are [f] (= clojure.lang.LazySeq (type (get-methods f {} "")))
      {} nil))
  (testing "return map structure"
    (let [request (get-methods {:get nil :post nil} {} "")
          response-keys '(:ring-request  :desc)]
      (is (= response-keys (keys (first request))))
      (is (= response-keys (keys (second request))))))
  (testing "valid methods are: GET, PATCH, PUT, POST, DELETE and OPTIONS and HEAD"
    (let [ok '(:get :patch :put :post :delete :options :head)
          all (conj ok "get" "" nil "/test")
          m (get-methods (u/create-map all) {:host "" :scheme ""}  "pot.org")]
      (is (= (sort ok)
             (sort (map #(:request-method (:ring-request %)) m)))))))

(deftest test-get-requests
  (testing "always returns a sequence"
    (are [f] (= clojure.lang.LazySeq (type (get-requests f {})))
      {} nil)
    (are [f s] (= clojure.lang.LazySeq (type (get-requests f {} s)))
      {} ""
      nil "pot.org"))
  (testing "message-api"
    (let [requests (->> (get-requests
                         (edn/read-string
                          (slurp "test-resources/message-api.edn"))
                         {:host "" :scheme ""})
                        (map #(:ring-request %)))]
          
      (is (= 6 (count requests)))
      (is (= '(:post :get :put :delete :post :get)
             (map #(:request-method %) requests)))
      (is (= '("/messages/{version}"
               "/messages/{version}/{id}"
               "/messages/{version}/{id}"
               "/messages/{version}/{id}"
               "/messages/{version}/{id}/read"
               "/messages/{version}/{toIdentity}/list")
             (map #(:uri %) requests))))))
