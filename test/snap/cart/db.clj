(ns snap.cart.db
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system test-system]]
            [snap.api.cart.db :as cart.db]))

(use-fixtures :once (use-system))

(deftest cart-crud-test
  (let [{datasource :postgres/db} @test-system]
    (testing "can create cart"
      (let [test-cart (cart.db/create datasource)]
        (is (some? test-cart))

        (testing "can get cart"
          (let [read-cart (cart.db/get-by-id datasource (:cart/id test-cart))]
            (is (= (:cart/id test-cart)
                   (:cart/id read-cart)))))

        (testing "can delete cart"
          (cart.db/delete-by-id datasource (:cart/id test-cart))
          (is (= (cart.db/get-all datasource) 0)))))))
