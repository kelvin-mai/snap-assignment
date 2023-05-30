(ns snap.cart.route-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system
                                     test-system
                                     request]]))

(use-fixtures :once (use-system))

(deftest cart-crud-test
  (let [{router :reitit/routes} @test-system]
    (testing "can create cart"
      (let [test-cart (-> (request router
                                   :post "/api/cart")
                          :data)
            uri (str "/api/cart/" (:cart/id test-cart))]
        (is (some? test-cart))

        (testing "can get new cart"
          (let [read-cart (-> (request router
                                       :get uri)
                              :data)]
            (is (= (:cart/id test-cart)
                   (:cart/id read-cart)))))

        (testing "can get all carts"
          (let [all-carts (-> (request router
                                       :get "/api/cart")
                              :data)]
            (is (= (:cart/id (first all-carts))
                   (:cart/id test-cart)))))

        (testing "can delete cart"
          (let [response (request router :delete uri)]
            (is (= (:success response) true))))))))
