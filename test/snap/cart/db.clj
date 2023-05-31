(ns snap.cart.db
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system test-system]]
            [snap.api.cart.db :as cart.db]
            [snap.api.product.db :as product.db]))

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

(deftest add-to-cart-test
  (let [{datasource :postgres/db} @test-system
        empty-product (product.db/create datasource {:name "empty"
                                                     :price 12.50})
        available-product (product.db/create datasource {:name "available"
                                                         :quantity 10
                                                         :price 12.50})
        test-cart (cart.db/create datasource)]
    (testing "can add to cart"
      (let [data {:cart-id (:cart/id test-cart)
                  :product-id (:product/id available-product)
                  :quantity 2}
            cart-product (cart.db/add-to-cart datasource data)]
        (is (some? cart-product))

        (testing "can remove from cart"
          (cart.db/remove-from-cart datasource data)
          (is (= (count (cart.db/get-cart-product datasource data))
                 0)))))

    (product.db/delete-by-id datasource (:product/id empty-product))
    (product.db/delete-by-id datasource (:product/id available-product))
    (cart.db/delete-by-id datasource (:cart/id test-cart))))

(deftest cart-virtual-rows-test
  (let [{datasource :postgres/db} @test-system
        test-product-1 (product.db/create datasource {:name "one"
                                                      :price 69.99
                                                      :quantity 10})
        test-product-2 (product.db/create datasource {:name "two"
                                                      :price 22.22
                                                      :quantity 10})
        test-cart (cart.db/create datasource)]
    (testing "empty cart"
      (let [all-result (first (cart.db/get-all datasource))
            single-result (cart.db/get-by-id datasource (:cart/id datasource))]
        (is (nil? (:items-in-cart all-result)))
        (is (nil? (:products-in-cart all-result)))
        (is (nil? (:total-price all-result)))
        (is (nil? (:total-price single-result)))))

    (testing "with products"
      (let [_setup (do
                     (cart.db/add-to-cart datasource {:cart-id (:cart/id test-cart)
                                                      :product-id (:product/id test-product-1)
                                                      :quantity 1})
                     (cart.db/add-to-cart datasource {:cart-id (:cart/id test-cart)
                                                      :product-id (:product/id test-product-2)
                                                      :quantity 2}))
            total-price (+ (:product/price test-product-1) (* 2 (:product/price test-product-2)))
            all-result (cart.db/get-all datasource)
            single-result (cart.db/get-by-id datasource (:cart/id datasource))]
        (is (= (:items-in-cart all-result) 3))
        (is (= (:products-in-cart all-result) 2))
        (is (= (:total-price all-result) total-price))
        (is (= (:total-price single-result) total-price))
        (cart.db/remove-from-cart datasource {:cart-id (:cart/id test-cart)
                                              :product-id (:product/id test-product-1)})
        (cart.db/remove-from-cart datasource {:cart-id (:cart/id test-cart)
                                              :product-id (:product/id test-product-2)})))
    (product.db/delete-by-id datasource (:product/id test-product-1))
    (product.db/delete-by-id datasource (:product/id test-product-2))
    (cart.db/delete-by-id datasource (:cart/id test-cart))))
