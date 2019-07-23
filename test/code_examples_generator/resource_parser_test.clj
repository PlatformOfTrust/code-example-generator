(ns code-examples-generator.resource-parser-test
  (:require
   [clojure.test :refer :all]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [code-examples-generator.resource-parser :refer :all]
   [code-examples-generator.test-utils :as u]
   [cheshire.core :as json]
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
  (testing "? identifies an optional parameter and should be removed"
    (is (= {:test "ok"}
           (coerce-examples->values {:test? {:example "ok"}}))))
  (testing "value can be a stringified map"
    (is (= {:test "my-example"}
           (coerce-examples->values {:test "description: ok\ntype: object\nexample: my-example"}))))
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


;; TODO also create an integration test
(deftest test-get-2xx-response
  (testing "status code is 2xx"
    (are [m] (str/starts-with? (:status (get-2xx-response m)) ":2")        
      {"404" {:body {:example "test-404"}}
       "200" {:body {:example "test-200"}}}
      {"201" {:body {:example "test-201"}}}))
  (testing "response body"
    (is (= "test-200"
           (:body (get-2xx-response {"404" {:body {:example "test-404"}}
                                     "200" {:body {:example "test-200"}}}))))
    (is (= nil (get-2xx-response {})))))

(deftest test-get-ring-request
  (testing "requried ring-request keys are always present when 
            proper data has been provided "
    (is (= '(:request-method
             :server-name
             :scheme
             :uri
             :query-string
             :body
             :headers)
           (keys (get-ring-request
                  {:body {:example (json/generate-string {:ok "yes"})}
                   :queryParameters {:t1 {:example 1}}
                   :headers {:h1 {:example 2}}}
                  :get
                  "pot.org"
                  "https"
                  "/")))))
  (testing "no empty or nil values"
    (is (= '(:request-method :uri :query-string)
           (keys (get-ring-request {:queryParameters {:test {:example "ok"}}
                                    :body {}}
                                   :get nil "" "/")))))
  (testing "headers exist"
    (with-redefs [coerce-examples->values (fn [m] m)]
      (let [h {:test "headers"}]
        (is (= h (:headers (get-ring-request {:headers h} "" "" "" "")))))))
  (testing "JSON formatted body parameters exist"
    (with-redefs [coerce-examples->values (fn [m] m)]
      (let [b {:type "test" :example (json/generate-string {:ok "yes!"})}]
        (is (= (json/parse-string (:example b))
               (:body (get-ring-request {:body b} "" "" "" "")))))))
  (testing "query parameters should get url encoded"
    (with-redefs [coerce-examples->values (fn [m] m)]                                  
      (let [q {:test "query"}
            r (get-ring-request {:queryParameters q} "" "" "" "")]
        (is (= (form-encode q) (:query-string r)))))))

(deftest test-get-methods
  (testing "always return a map"
    (are [f] (= clojure.lang.LazySeq (type (get-methods f {} "" nil)))
      {} nil))
  (testing "return map structure"
    (let [request (get-methods {:get nil :post nil} {} "" nil)
          response-keys '(:ring-request  :ok :desc)]
      (is (= response-keys (keys (first request))))
      (is (= response-keys (keys (second request))))))
  (testing "valid methods are: GET, PATCH, PUT, POST, DELETE and OPTIONS and HEAD"
    (let [ok '(:get :patch :put :post :delete :options :head)
          all (conj ok "get" "" nil "/test")
          m (get-methods (u/create-map all) {:host "" :scheme ""}  "pot.org" nil)]
      (is (= (sort ok)
             (sort (map #(:request-method (:ring-request %)) m)))))))

;; TODO should maybe move this to integration tests?
(deftest test-get-requests
  (testing "always returns a sequence"
    (are [f] (= clojure.lang.LazySeq (type (get-requests f {})))
      {} nil)
    (are [f s] (= clojure.lang.LazySeq (type (get-requests f {} s)))
      {} ""
      nil "pot.org"))
  (testing "requests of message-api"
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
             (map #(:uri %) requests)))))
  (testing "auth header of message-api"
    (let [raml (edn/read-string (slurp "test-resources/message-api.edn"))
          request-headers (->> (get-requests raml {:host "" :scheme ""})
                               (map #(:ring-request %))
                               first
                               :headers)
          source-headers (-> raml
                             (get "/messages/{version}")
                             :post
                             :headers)]
      (is (str/includes? source-headers (:Authorization request-headers)))))
  (testing "body parameters of broker-api"
    (let [raml (edn/read-string (slurp "test-resources/broker-api.edn"))
          requests (->> (get-requests raml {:host "" :scheme ""})
                        (map #(:ring-request %)))]
          
      (is (= (-> requests first :body)
             (-> raml
                 (get "/broker/{version}/fetch-data-product")
                 :post
                 :body
                 :example
                 (json/parse-string)))))))
