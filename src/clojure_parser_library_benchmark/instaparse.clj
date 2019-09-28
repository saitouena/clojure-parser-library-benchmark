(ns clojure-parser-library-benchmark.instaparse
  (:require [instaparse.core :as insta]))

(def as-and-bs
  (insta/parser
    "S = AB*
     AB = A B
     A = 'a'+
     B = 'b'+"))

(def json
  (insta/parser
   "element = ws value ws
    value = object
          | array
          | number
          | true
          | false
          | null
          | string
    object = <'{'> ws <'}'>
           | <'{'> member (<','> member)* <'}'>
    member = ws string ws <':'> element
    <ws> = w+
    <w> = <''> | <'\t'> | <' '> | <'\r'> | <'\n'>
    array = <'['> element (<','> element)* <']'>
    number = integer
    integer = posint
            | negint
    posint = digit | onenine digit+
    negint = <'-'> digit | <'-'> onenine digit+
    <digit> = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    <onenine> = '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    true = 'true'
    false = 'false'
    null = 'null'
    string = <'\"'> character+ <'\"'>
    <character> = #'[a-zA-Z]'"))

;; TODO: character

(defn transform-return-itself [& [x]] x)

(defn transform-posint
  [& children]
  (read-string (apply str children)))

(defn transform-negint
  [& children]
  (- (apply transform-posint children)))

(def transform-digit (comp read-string str))

(def transform-onenine (comp read-string str))

(def transform-true (comp read-string str))

(def transform-false (comp read-string str))

(defn transform-null [& _] nil)

(defn transform-character [& [c]] c)

(def transform-string str)

(defn transform-integer [& [n]] n)

(defn transform-number [& [n]] n)

(defn transform-member [& [key val]] {key val})

(defn transform-object [& members] (apply merge members))

(defn transform-array [& elements] (vec elements))

(def how-to-transform
  {:element transform-return-itself
   :value transform-return-itself
   :object transform-object
   :member transform-member
   :array transform-array
   :number transform-number
   :integer transform-integer
   :posint transform-posint
   :negint transform-negint
   :digit transform-digit
   :onenine transform-onenine
   :true transform-true
   :false transform-false
   :null transform-null
   :character transform-character
   :string transform-string
   })

(def transform-json
  (partial insta/transform how-to-transform))

(def parse (comp transform-json json))

(json "1234")

(json "-1234")

(parse "1234")

(parse "-1234")

(parse "true")

(parse "false")

(parse "null")

(parse "\"hoge\"")

(json "{}")
(json "{          }")
(json "{     \"hoge\" : 1, \"fuga\" : -10}")

(parse "{     \"hoge\" : 1, \"fuga\" : -10}")

(json "        " :start :ws)

(json "[ 1 , 2, 3, 4]")

(parse "[ 1 , 2, 3, 4]")

"\u0021" ;; unicode
