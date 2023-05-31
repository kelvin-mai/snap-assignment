(ns snap.cart.route-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system
                                     test-system
                                     request]]
            [snap.product.route-utils :refer [create-product-fn
                                              delete-product-fn]]))

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

(deftest add-to-cart-test
  (let [{router :reitit/routes} @test-system
        create-product (create-product-fn router)
        delete-product (delete-product-fn router)
        empty-product (create-product {:name "empty"
                                       :price 12.50})
        available-product (create-product  {:name "available"
                                            :quantity 10
                                            :price 12.50})
        test-cart (-> (request router
                               :post "/api/cart")
                      :data)
        uri (str "/api/cart/" (:cart/id test-cart))]

    (testing "can add to cart"
      (let [test-cart-product (-> (request router
                                           :post uri
                                           {:body-params {:product-id (:product/id available-product)
                                                          :quantity 2}})
                                  :data)
            cart-product-uri (str uri "/" (:cart-product/product-id test-cart-product))]
        (is (some? test-cart-product))

        (testing "should throw if add same product"
          (let [repeat-test-cart-product (-> (request router
                                                      :post uri
                                                      {:body-params {:product-id (:product/id available-product)
                                                                     :quantity 2}}))]
            (is (= (:success repeat-test-cart-product) false))
            (is (= (get-in repeat-test-cart-product [:data :cause]) "product already in cart"))))

        (testing "should throw if quantity larger than available inventory"
          (let [oversized-cart-product (-> (request router
                                                    :post uri
                                                    {:body-params {:product-id (:product/id empty-product)
                                                                   :quantity 2}}))]
            (is (= (:success oversized-cart-product) false))
            (is (= (get-in oversized-cart-product [:data :cause]) "quantity too large"))))

        (testing "can remove from cart"
          (is (some?
               (request router :delete cart-product-uri))))))

    (delete-product (:product/id empty-product))
    (delete-product (:product/id available-product))
    (request router :delete uri)))

(deftest checkout-cart-test
  (let [{router :reitit/routes} @test-system
        create-product (create-product-fn router)
        delete-product (delete-product-fn router)
        test-product (create-product {:name "test"
                                      :quantity 1
                                      :price 12.50})
        good-cart (-> (request router
                               :post "/api/cart")
                      :data)
        good-cart-uri (str "/api/cart/" (:cart/id good-cart))
        bad-cart (-> (request router
                              :post "/api/cart")
                     :data)
        bad-cart-uri (str "/api/cart/" (:cart/id bad-cart))
        _setup (do (request router :post good-cart-uri
                            {:body-params {:product-id (:product/id test-product)
                                           :quantity 1}})
                   (request router :post bad-cart-uri
                            {:body-params {:product-id (:product/id test-product)
                                           :quantity 1}}))]

    (testing "can checkout cart"
      (let [test-checkout (request router :put good-cart-uri)
            after-checkout (-> (request router :get (str "/api/product/" (:product/id test-product)))
                               :data)]
        (is (= (:success test-checkout) true))
        (is (= (get-in test-checkout [:data :cart/checked-out]) true))
        (is (not= (:product/quantity after-checkout)
                  (:product/quantity test-product)))
        (is (= (:product/quantity after-checkout) 0)))

      (testing "can only checkout cart once"
        (let [repeat-test-checkout (request router :put good-cart-uri)]
          (is (= (:success repeat-test-checkout) false))
          (is (= (get-in repeat-test-checkout [:data :cause]) "cart already checked out")))))

    (testing "can not checkout cart with bad inventory"
      (let [test-checkout (request router :put bad-cart-uri)]
        (is (= (:success test-checkout) false))
        (is (= (get-in test-checkout [:data :cause]) "some product quantity too large"))))
    (request router :delete (str good-cart-uri "/" (:product/id test-product)))
    (request router :delete (str bad-cart-uri "/" (:product/id test-product)))
    (delete-product (:product/id test-product))
    (request router :delete good-cart-uri)
    (request router :delete bad-cart-uri)))
