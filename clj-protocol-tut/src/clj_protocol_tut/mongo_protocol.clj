(ns clj-protocol-tut.mongo-protocol
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.spec.alpha :as spec]))

;database connection
(def conn (mg/connect))

(def db (mg/get-db conn "tutorial"))

;collection names
(def obj-coll "inanimate")

(def color-coll "color")

; Abstraction
(defprotocol
  MongoAPI
  "Abstracting MongoDB Calls"
  (save [this prop])
  (delete [this prop])
  (retrieve [this prop])
  (retrieve-all [this]))

; Particular implementations
(deftype InanimateObject []
  MongoAPI
  (save [_ name]
    "saves objects to mongodb"
    {:pre [(spec/valid? string? name)]}
    (mc/insert db obj-coll {:object name}))
  (delete [_ name]
    "removes objects from mongodb"
    {:pre [(spec/valid? string? name)]}
    (mc/remove db obj-coll {:object name}))
  (retrieve [_ name]
    "pulls an object from mongodb"
    {:pre [(spec/valid? string? name)]}
    (mc/find-maps db obj-coll {:object name}))
  (retrieve-all [_]
    "pulls all objects from mongodb"
    {:pre [(spec/valid? string? name)]}
    (mc/find-maps db obj-coll)))

(deftype Color []
  MongoAPI
  (save [_ color]
    "saves objects to mongodb"
    {:pre [(spec/valid? string? color)]}
    (mc/insert db color-coll {:color color}))
  (delete [_ color]
    "removes objects from mongodb"
    {:pre [(spec/valid? string? color)]}
    (mc/remove db color-coll {:color color}))
  (retrieve [_ color]
    "pulls objects from mongodb"
    {:pre [(spec/valid? string? color)]}
    (mc/find-maps db color-coll {:color color}))
  (retrieve-all [_]
    "pulls all objects from mongodb"
    (mc/find-maps db color-coll)))