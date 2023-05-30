(ns snap.product.route-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system
                                     test-system
                                     request]]))

(use-fixtures :once (use-system))

(deftest product-crud-test
  (let [{router :reitit/routes} @test-system]
    (testing "can create product"
      (let [test-product (-> (request router
                                      :post "/api/product"
                                      {:body-params {:name "test"}})
                             :data)
            uri (str "/api/product/" (:product/id test-product))]
        (is (= (:product/name test-product) "test"))
        (is (= (:product/quantity test-product) 0))
        (is (= (:product/price test-product) 0.0))

        (testing "can get new product"
          (let [read-product (-> (request router
                                          :get uri)
                                 :data)]
            (is (= (:product/name read-product)
                   (:product/name test-product)))))

        (testing "can update new product"
          (let [updated-product (-> (request router
                                             :put uri
                                             {:body-params {:quantity 10
                                                            :price 12.50}})
                                    :data)]
            (is (= (:product/quantity updated-product) 10))
            (is (= (:product/price updated-product) 12.5))))

        (testing "can delete new product"
          (let [response (request router :delete uri)]
            (is (= (:success response) true))))))))

(deftest product-types-test
  (let [{router :reitit/routes} @test-system
        create-product (fn [data]
                         (-> (request router
                                      :post "/api/product"
                                      {:body-params data})
                             :data))
        delete-product (fn [id]
                         (-> (request router
                                      :delete (str "/api/product/" id))))
        free-product (create-product {:name "free"
                                      :quantity 10})
        empty-product (create-product {:name "empty"
                                       :price 12.50})
        available-product (create-product {:name "available"
                                           :quantity 10
                                           :price 12.50})]

    (testing "can create free product"
      (is (= (:product/price free-product) 0.0)))
    (testing "can create empty product"
      (is (= (:product/quantity empty-product) 0)))
    (testing "can create available product"
      (is (= (:product/price available-product) 12.5))
      (is (= (:product/quantity available-product) 10)))
    (testing "can get all available"
      (let [all-avaliable (-> (request router
                                       :get "/api/product"
                                       {:query-params {:available true}})
                              :data)]
        (is (= (count all-avaliable) 2))))
    (testing "can get all products"
      (let [all-products (-> (request router
                                      :get "/api/product")
                             :data)]
        (is (= (count all-products) 3))))
    (delete-product (:product/id free-product))
    (delete-product (:product/id empty-product))
    (delete-product (:product/id available-product))))
