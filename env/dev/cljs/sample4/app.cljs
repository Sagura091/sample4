(ns ^:dev/once sample4.app
  (:require
    [sample4.core :as core]
    [cljs.spec.alpha :as s]
    [expound.alpha :as expound]
    [devtools.core :as devtools]
    [lambdaisland.glogi :as log]
    [lambdaisland.glogi.console :as glogi-console]))



(log/set-levels
  {:glogi/root   :debug    ;; Set a root logger level, this will be inherited by all loggers
   'my.app.thing :trace   ;; Some namespaces you might want detailed logging
   'my.app.other :error   ;; or for others you only want to see errors.
   })
(glogi-console/install!)

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(set! s/*explain-out* expound/printer)

(enable-console-print!)

(devtools/install!)

(core/init!)
