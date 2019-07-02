(ns code-examples-generator.core
  (:gen-class)) 

(defn foo
  "I don't do a whole lot."
  []
  (println "Hello, World!"))

(defn -main [& args] (foo))
