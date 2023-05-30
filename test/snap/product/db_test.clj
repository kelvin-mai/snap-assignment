(ns snap.product.db-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system test-system]]
            [snap.api.product.db :as product.db]))

(use-fixtures :once (use-system))

(deftest product-crud-test
  (let [{datasource :postgres/db} @test-system]
    (testing "can create product"
      (let [test-product (product.db/create datasource {:name "test"})]
        (is (= (:product/name test-product) "test"))

        (testing "can get new product"
          (let [read-product (product.db/get-by-id datasource (:product/id test-product))]
            (is (= (:product/name test-product)
                   (:product/name read-product)))))

        (testing "can update product"
          (let [updated-product (product.db/update datasource (:product/id test-product)
                                                   {:quantity 10
                                                    :price 12.50})]
            (is (= (:product/quantity updated-product) 10))
            (is (= (:product/price updated-product) 12.50M))))

        (testing "can delete product"
          (product.db/delete-by-id datasource (:product/id test-product))
          (is (= (count (product.db/get-all datasource {})) 0)))))))

(deftest product-types-test
  (let [{datasource :postgres/db} @test-system
        free-product (product.db/create datasource {:name "free"
                                                    :quantity 10})
        empty-product (product.db/create datasource {:name "empty"
                                                     :price 12.50})
        available-product (product.db/create datasource {:name "available"
                                                         :quantity 10
                                                         :price 12.50})]
    (testing "can create free product"
      (is (= (:product/price free-product) 0.00M)))
    (testing "can create empty product"
      (is (= (:product/quantity empty-product) 0)))
    (testing "can create available product"
      (is (= (:product/price available-product) 12.50M))
      (is (= (:product/quantity available-product) 10)))
    (testing "can get all available"
      (let [all-avaliable (product.db/get-all datasource {:available true})]
        (is (= (count all-avaliable) 2))))
    (testing "can get all products"
      (let [all-products (product.db/get-all datasource {})]
        (is (= (count all-products) 3))))

    (product.db/delete-by-id datasource (:product/id free-product))
    (product.db/delete-by-id datasource (:product/id empty-product))
    (product.db/delete-by-id datasource (:product/id available-product))))
