(ns code-examples-generator.formatters-test
  (:require
   [clojure.test :refer :all]
   [code-examples-generator.formatters :refer :all]
   [clojure.string :as str]))
            
(deftest test-remove-extra-newlines
  (is (= "test1\ntest2"
         (remove-extra-newlines "test1\n  \n    \ntest2"))))

(deftest test-pretty-print
  (are [x] (= x (pretty-print x :test))
    {} {:test nil} {:test {}} {:test ""})
  (is (= "{\n    \"a\" : {\n        \"b\" : 1\n    }\n}"
         (-> {:body {:a {:b 1}}}
             (pretty-print :body)
             :body))))

(deftest test-pretty-print-curl
  (testing "that each header gets printed to a new line"
    (is (= "curl -i -X GET \\\n     -H \"Auth\" \"https://test\""
          (-> {:curl "curl -i -X GET -H \"Auth\" \"https://test\""}
              (pretty-print-curl :curl)
              :curl)))
    (is (= "curl -X GET \\\n     -H \"1\" \\\n     -H \"2\" \"://test\""
           (-> {:curl "curl -X GET -H \"1\" -H \"2\" \"://test\""}
               (pretty-print-curl :curl)
               :curl))))
  (testing "that payload gets printed to a new line"
    (is (= "curl -i -X PUT \\\n     --data-binary \\\n\"{}\" \"://\""
           (-> {:curl "curl -i -X PUT --data-binary \"{}\" \"://\""}
               (pretty-print-curl :curl)
               :curl)))))    

(deftest test-get-version
  (is (str/starts-with? (get-version) "v"))
  (is (not (str/includes? (get-version) "SNAPSHOT"))))
