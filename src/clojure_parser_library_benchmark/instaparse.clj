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
   "number = integer
    integer = posint
            | negint
    posint = digit | onenine digit+
    negint = '-' digit | '-' onenine digit+
    digit = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    onenine = '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'"))

(json "1234")

(defn transform-posint
  [& children]
  (reduce (fn [x acc] (-> (* 10 acc) (+ x)))
          0
          children))

(defn transform-negint
  [& children]
  (- (transform-posint (rest children))))

(def transform-digit (comp read-string str))

(def transform-onenine (comp read-string str))

(->> (json "1234")
     (insta/transform {:posint transform-posint
                       :negint transform-negint
                       :digit transform-digit
                       :onenine transform-onenine}))
