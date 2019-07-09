(ns code-examples-generator.fs-utils-test
  (:require
   [clojure.test :refer :all]
   [code-examples-generator.fs-utils :refer :all]
   [code-examples-generator.test-utils :as u]
   [selmer.parser :as selmer]
   [clojure.string :as str]
   [clojure.java.io :as io]))
            
;; TODO better testing with nested directories
;; ... invalid files etc..
;; TODO test that reading invalid folder does not
;; ...result in a crash etc.
(deftest test-get-RAML-files
  (testing "empty directory"
    (is (empty? (get-RAML-files (u/create-temp-dir)))))
  (testing "a non existing path"
    (is (empty? (get-RAML-files (u/random-path)))))
  (testing "a single RAML file"
    (let [file (u/create-temp-file ".raml")
          filenames (map #(.getName %) (get-RAML-files file))]
      (is (= 1 (count filenames)))
      (is (= (.getName file)
             (first filenames)))))
             
  (testing "multiple `.raml` files from a directory"
    (let [f1 (u/create-temp-file ".raml")
          f2 (u/create-temp-file ".raml")
          f3 (u/create-temp-file ".notAraml")
          path (.getParent f1)
          files (get-RAML-files path)
          filenames (map #(.getName %) files)]
      (is (= (.getParent f1) (.getParent f2) (.getParent f3)))
      (is (some #(= %(.getName f1)) filenames))
      (is (some #(= % (.getName f2)) filenames))
      (is (not-every? #(= % (.getName f3)) filenames)))))

(deftest test-get-templates
  (testing "from project resources by default"
    (is (= (get-templates) (get-templates "./resources/templates/"))))
  (testing "from custom location"
    (let [f1 (u/create-temp-file)
          f2 (u/create-temp-file)
          templates (get-templates (.getParent f1))]
      (is (= (.getParent f1) (.getParent f2)))
      (is (some #(= f1 %) templates))
      (is (some #(= f2 %) templates)))))

(deftest test-get-dest
  (testing "path composition"
    (let [file (u/create-temp-file)
          cli-args {:source (.getParent file) :dest (u/uuid)}]
      (are [m] (= (str/join "/" (list 
                                 (:dest cli-args)
                                 (.getName file)
                                 (str/replace (:uri m) "/" "_")
                                 (str/upper-case (name (:request-method m)))))
                  (get-dest cli-args file m))
        {:uri "/v1/test" :request-method :get}
        {:uri "/v1/test" :request-method :post}
        {:uri "/v1/test" :request-method :delete}
        {:uri "/v1/test2" :request-method :get}))))

;; TODO skip testing for now. Maybe this will be excluded from releasae
;;(deftest test-spit-raml-map)

(deftest test-save-code-examples
  (testing "each template results in one saved and parsed code example"
    (let [template1 (u/create-temp-file)
          template2 (u/create-temp-file)
          context-map {:test1 "first!" :test2 "second!"}
          dest (u/create-temp-dir)]
      (spit template1 "{{test1}}")
      (spit template2 "{{test2}}")
      (with-redefs [get-templates (constantly (list template1 template2))]
        (is (= 0 (->> dest io/file file-seq (remove #(.isDirectory %)) count)))
        (save-code-examples dest context-map)
        (let [code-examples (->> dest io/file file-seq (remove #(.isDirectory %)))]
          (is (= 2 (count code-examples)))
          (is (= "first!"
                 (->> code-examples
                      (filter #(= (.getName template1) (.getName %)))
                      first
                      slurp)))
          (is (= "second!"
                 (->> code-examples
                      (filter #(= (.getName template2) (.getName %)))
                      first
                      slurp))))))))
