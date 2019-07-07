(defproject code-examples-generator "0.1.0-SNAPSHOT"
  :description "Command line tool that is able to parse Platform of Trust API documentation and 
                generate HTTP requests based on provided templates."
  :url "https://github.com/PlatformOfTrust/code-examples-generator"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :main code-examples-generator.core
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [raml-clj-parser "0.1.1-SNAPSHOT"]
                 [ring/ring-codec "1.1.2"]
                 [ring-curl "1.0.1"]
                 [cheshire "5.8.1"]
                 [selmer "1.12.12"]]

  :plugins []

  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths ["resources"]
            

  :bikeshed {:long-lines false
             :trailing-whitespace false
             :blank-lines false}

  :cloverage {#_:fail-threshold #_50
              :low-watermark 70
              :high-watermark 90}

  :repl-options {:init-ns code-examples-generator.core}
  :profiles {:uberjar {:omit-source true
                       :aot :all
                       :uberjar-name "raml2http.jar"
                       :source-paths ["src"]
                       :resource-paths ["resources"]}
             :dev {:dependencies [[pjstadig/humane-test-output "0.9.0"]]
                   :plugins [[lein-kibit "0.1.6"]
                             [jonase/eastwood "0.3.5"]
                             [lein-ancient "0.6.15"]
                             [lein-cloverage "1.1.1"]
                             [lein-bikeshed "0.5.2"]
                             [lein-annotations "0.1.0"]
                             [com.jakemccrary/lein-test-refresh "0.24.1"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}
                             
                             
             :test [:dev]})

