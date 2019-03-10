# An Introduction to Clojure Protocols

## What is a Clojure Protocol ?
---
According to [clojure.org](https://clojure.org/reference/protocols)

> Clojure is written in terms of abstractions. There are abstractions for sequences, collections, callability, etc. In addition, Clojure supplies many implementations of these abstractions. The abstractions are specified by host interfaces, and the implementations by host classes. While this was sufficient for bootstrapping the language, it left Clojure without similar abstraction and low-level implementation facilities. The protocols and datatypes features add powerful and flexible mechanisms for abstraction and data structure definition with no compromises vs the facilities of the host platform.

Clojure protocols are simply a way to define Java interfaces. They provide abstraction while the purpose of
them is to solve the Expression Problem.

### Whats the Expression Problem ?

The expression problem is a problem of extensibility. We want our programs to be
able to work with new data types and new operations. Furthermore, we want to be able to
add new operations which work with existing data types and vice versa. However,
we want to solve this problem without recompiling existing code and without
casting.

## Prerequisites
---
* Clojure Programmming Experience
* MongoDB Instance setup with a database called `tutorial` and two collections `color` and `inanimate`.
  * See [Install MongoDB](https://docs.mongodb.com/manual/administration/install-community/)
* Clojure project created using Leiningen.
  * See [Leiningen](https://leiningen.org/)

### Dependencies

Add the following deps to your `project.clj`

`[org.novemberain/monger "3.1.0"]`

In your project namespace `require` the following:

```Clojure
(:require [monger.core :as mg]
  [monger.collection :as mc]
  [clojure.spec.alpha :as spec])
```

Start a database connection
```Clojure
(def conn (mg/connect))

(def db (mg/get-db conn "tutorial"))
```

Define collection names
```Clojure
(def obj-coll "inanimate")

(def color-coll "color")
```

## Implementating a simple Clojure Protocol
---
We are going to create a Clojure Protocol to abstract some database calls to MongoDB.

### Define a Clojure Protocol

```Clojure
(defprotocol
  MongoAPI
  "Abstracting MongoDB Calls"
  (save [this prop])
  (delete [this prop])
  (retrieve [this prop])
  (retrieve-all [this]))
```
This defines a protocol called MongoAPI containing four method signatures: `save`, `delete`, `retrieve`, and `retrieve-all`. These method signatures consist of a name, argument(s) and an optional docstring. Here we have created the abstraction with no actual implementation. These method signatures will dispatch on the type of the first argument, `this` passed to the function.

### Particular implementations
```Clojure
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
```
Our first implementation is created by `deftype` (see: [Datatypes](https://clojure.org/reference/datatypes)) with a name `InanimateObject`, no arguments and our protocol `MongoAPI`. For each of our method signatures we create an implementation for the type `InanimateObject`.

```Clojure
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
```

Our second implementation has a name `Color`, no arguments and our protocol `MongoAPI`. For each of our method signatures we create an implementation for the type `Color`.

### Dispatch

Firing up a REPL we can see our Protocol in action !

Lets save a couple inanimate objects and colors to MongoDB
```Clojure
(save (InanimateObject.) "Speakers")
;; => #object[com.mongodb.WriteResult 0x43a12075 "WriteResult{, n=0, updateOfExisting=false, upsertedId=null}"]

(save (InanimateObject.) "Chair")
;; => #object[com.mongodb.WriteResult 0x2ed04b4c "WriteResult{, n=0, updateOfExisting=false, upsertedId=null}"]

(save (Color.) "Red")
;; => #object[com.mongodb.WriteResult 0x16e4afba "WriteResult{, n=0, updateOfExisting=false, upsertedId=null}"]
(save (Color.) "Black")
;;
```
By passing in our type, `InanimateObject.` or `Color.` the function dispatches to the particular implementation we defined.

We can retrieve all of our objects to make sure our save implementation works
```Clojure
(retrieve-all (InanimateObject.))
;; => ({:_id #object[org.bson.types.ObjectId 0x1d2bcdf3 "5c851957bfbc40307a945861"], :object "Speakers"}
;;     {:_id #object[org.bson.types.ObjectId 0x38c495d7 "5c851cebbfbc40307a945863"], :object "Chair"})

(retrieve-all (Color.))
;; => ({:_id #object[org.bson.types.ObjectId 0x13281d94 "5c851d77bfbc40307a945864"], :color "Red"}
;;     {:_id #object[org.bson.types.ObjectId 0x33b9e198 "5c851d7abfbc40307a945865"], :color "Black"})
 ```

 Feel free to try out the other functions or create your own !
