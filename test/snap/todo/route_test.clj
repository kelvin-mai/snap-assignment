(ns snap.todo.route-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system
                                     test-system
                                     request]]))

(use-fixtures :once (use-system))

(deftest todo-list-crud-test
  (let [{router :reitit/routes} @test-system]
    (testing "can create todo-list"
      (let [test-todo-list (-> (request router
                                        :post "/api/todo-list"
                                        {:body-params {:name "test"}})
                               :data)]
        (is (= (:todo-list/name test-todo-list) "test"))

        (testing "can get new todo-list"
          (let [uri (str "/api/todo-list/" (:todo-list/id test-todo-list))
                read-todo-list (-> (request router :get uri)
                                   :data)]
            (is (= (:todo-list/name read-todo-list)
                   (:todo-list/name test-todo-list)))))

        (testing "can get all todo-list"
          (let [all-todo-list (-> (request router :get "/api/todo-list")
                                  :data)]
            (is (= (count all-todo-list) 1))
            (is (= (:todo-list/name (first all-todo-list))
                   (:todo-list/name test-todo-list)))))

        (testing "can delete new todo-list"
          (let [uri (str "/api/todo-list/" (:todo-list/id test-todo-list))
                response (request router :delete uri)]
            (is (= (:success response) true))))))))

(deftest todo-item-crud-test
  (let [{router :reitit/routes} @test-system
        test-todo-list (-> (request router
                                    :post "/api/todo-list"
                                    {:body-params {:name "test"}})
                           :data)]
    (testing "can create todo item"
      (let [uri (str "/api/todo-list/" (:todo-list/id test-todo-list))
            test-todo-item (-> (request router
                                        :post uri
                                        {:body-params {:name "test"}})
                               :data)]
        (is (= (:todo-item/name test-todo-item) "test"))
        (is (= (:todo-item/completed test-todo-item) false))

        (testing "can get todo-item by id"
          (let [uri (str "/api/todo-item/" (:todo-item/id test-todo-item))
                read-todo-item (-> (request router :get uri)
                                   :data)]
            (is (= (:todo-item/id read-todo-item)
                   (:todo-item/id test-todo-item)))
            (is (= (:todo-item/name read-todo-item)
                   (:todo-item/name test-todo-item)))))

        (testing "can update todo-item"
          (let [uri (str "/api/todo-item/" (:todo-item/id test-todo-item))
                updated-todo-item (-> (request router
                                               :put uri
                                               {:body-params {:completed true}})
                                      :data)]
            (is (= (:todo-item/id updated-todo-item)
                   (:todo-item/id test-todo-item)))
            (is (= (:todo-item/name updated-todo-item)
                   (:todo-item/name test-todo-item)))
            (is (= (:todo-item/completed updated-todo-item) true))))

        (testing "can delete todo-item"
          (let [uri (str "/api/todo-item/" (:todo-item/id test-todo-item))
                response (request router :delete uri)]
            (is (= (:success response) true))))))

    (request router :delete (str "/api/todo-list/" (:todo-list/id test-todo-list)))))
