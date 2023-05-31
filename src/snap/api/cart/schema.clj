(ns snap.api.cart.schema
  (:require [snap.utils.schema :refer [nanoid?]]))

(def path-param
  [:map
   [:id (nanoid? 12)]])

(def nested-path-param
  [:map
   [:id (nanoid? 12)]
   [:product-id (nanoid? 12)]])

(def add-to-cart-body
  [:map
   [:product-id (nanoid? 12)]
   [:quantity pos-int?]])
