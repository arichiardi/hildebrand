(ns hildebrand.streams
  (:require #?@ (:clj
                 [[glossop.core :refer [go-catching <? <?!]]
                  [clojure.core.async :as async]]
                 :cljs
                 [[cljs.core.async :as async]])
            [clojure.set :as set]
            [eulalie.dynamo-streams]
            [eulalie.support]
            [hildebrand.core :as hildebrand]
            [hildebrand.internal :as i]
            [hildebrand.internal.request :as request]
            [hildebrand.internal.response :as response]
            [hildebrand.internal.streams])
  #? (:cljs (:require-macros [glossop.macros :refer [go-catching <?]]
                             [hildebrand.streams :refer [defissuer]])))

#? (:clj
    (defmacro defissuer [target-name args & [doc]]
      `(eulalie.support/defissuer :dynamo-streams
         ~target-name ~args
         request/restructure-request
         response/restructure-response
         ~doc)))

(defissuer describe-stream    [stream-arn])
(defissuer get-records        [shard-iterator])
(defissuer get-shard-iterator [stream-arn shard-id shard-iterator-type])
(defissuer list-streams       [])

(defn latest-stream-arn! [creds table & args]
  (go-catching
    (-> (apply hildebrand/describe-table! creds table args)
        <?
        :latest-stream-arn)))
#? (:clj (def latest-stream-arn!! (comp <?! latest-stream-arn!)))
