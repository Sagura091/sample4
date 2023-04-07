(defproject sample4 "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [
                 [cljs-ajax "0.8.4"]
                 [clojure.java-time "1.1.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [com.cognitect/transit-clj "1.0.329"]
                 [com.cognitect/transit-cljs "0.8.280"]
                 [com.google.javascript/closure-compiler-unshaded "v20220803"]
                 [cprop "0.1.19"]
                 [day8.re-frame/http-fx "0.2.4"]
                 [day8.re-frame/http-fx "0.2.4"]
                 [day8.re-frame/tracing "0.6.2"]
                 [day8.re-frame/re-frame-10x "1.5.0"]
                 [day8.re-frame/tracing-stubs "0.5.3"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 ; No need to specify slf4j-api, it’s required by logback
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [expound "0.9.0"]
                 [funcool/struct "1.4.0"]
                 [com.lambdaisland/glogi "1.0.136"]
                 [io.pedestal/pedestal.log  "0.5.9"]
                 [ch.qos.logback/logback-classic "1.2.6"]
                 [json-html "0.4.7"]
                 [luminus-transit "0.1.5"]
                 [luminus-http-kit "0.2.0"]
                 [aleph "0.6.1"]
                 [luminus-undertow "0.1.16"]
                 [com.github.pkpkpk/cljs-node-io "2.0.332"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.11.3"]
                 [luminus-jetty "0.2.3"]
                 [info.sunng/ring-jetty9-adapter "0.19.0"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.5.18"]
                 [metosin/ring-http-response "0.9.3"]
                 [org.apache.kafka/kafka-streams-test-utils "2.8.0"]
                 [mount "0.1.16"]
                 [clj-commons/fs "1.6.310"]
                 [degree9/nodejs-cljs "0.1.0"]
                 [com.lambdaisland/glogi "1.3.169"]
                 [org.clojure/tools.logging "0.2.6"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [nrepl "1.0.0"]
                 [ring-cors "0.1.13"]
                 [compojure "1.7.0"]
                 [com.taoensso/sente "1.17.0"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.60" :scope "provided"]
                 [org.clojure/core.async "1.5.648"]
                 [org.clojure/tools.cli "1.0.214"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.webjars.npm/bulma "0.9.4"]
                 [org.webjars.npm/material-icons "1.10.8"]
                 [org.webjars/webjars-locator "0.45"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [re-frame "1.2.0"]
                 [fundingcircle/jackdaw "0.9.9"]
                 [willa "0.1.1-SNAPSHOT"]
                 [reagent "1.1.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-defaults "0.3.4"]
                 [selmer "1.12.55"]
                 [thheller/shadow-cljs "2.20.3" :scope "provided"]]

  :min-lein-version "2.0.0"
  :target :nodejs
  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot sample4.core
  :plugins []
  :clean-targets ^{:protect false}
  [:target-path "target/cljsbuild"]
  

  :profiles
  {:uberjar {:omit-source true
             
             :prep-tasks ["compile" ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]
             :aot :all
             :uberjar-name "sample4.jar"
             :source-paths ["env/prod/clj"  "env/prod/cljs"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "1.0.6"]
                                 [cider/piggieback "0.5.3"]
                                 [org.clojure/tools.namespace "1.3.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [org.clojure/tools.logging "0.3.1"]
                                 ; No need to specify slf4j-api, it’s required by logback
                                 [ch.qos.logback/logback-classic "1.1.3"]
                                 [com.github.pkpkpk/cljs-node-io "2.0.332"]
                                 [prone "2021-04-23"]
                                 [day8.re-frame/http-fx "0.2.4"]
                                 [org.clojure/tools.logging "0.2.6"]
                                 [clj-commons/fs "1.6.310"]
                                 [degree9/nodejs-cljs "0.1.0"]
                                 [com.lambdaisland/glogi "1.0.136"]
                                 [io.pedestal/pedestal.log  "0.5.9"]
                                 [ch.qos.logback/logback-classic "1.2.6"]
                                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                                    javax.jms/jms
                                                                    com.sun.jmdk/jmxtools
                                                                    com.sun.jmx/jmxri]]
                                 [ring/ring-jetty-adapter "1.10.0"]
                                 [ring-cors "0.1.13"]
                                 [compojure "1.7.0"]
                                 [aleph "0.6.1"]
                                 [com.lambdaisland/glogi "1.3.169"]
                                 [luminus-jetty "0.2.3"]
                                 [luminus-http-kit "0.2.0"]
                                 [info.sunng/ring-jetty9-adapter "0.19.0"]
                                 [day8.re-frame/tracing "0.6.2"]
                                 [day8.re-frame/re-frame-10x "1.5.0"]
                                 [day8.re-frame/tracing-stubs "0.5.3"]
                                 [org.clojure/java.jdbc "0.7.3"]
                                 [fundingcircle/jackdaw "0.9.9"]
                                 [com.taoensso/sente "1.17.0"]
                                 [org.apache.kafka/kafka-streams-test-utils "2.8.0"]
                                 [ring/ring-devel "1.9.6"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "1.2.4"]
                                 [cider/cider-nrepl "0.26.0"]] 
                  
                  
                  :source-paths ["env/dev/clj"  "env/dev/cljs" "test/cljs"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
                  
                  

   :profiles/dev {}
   :profiles/test {}})
