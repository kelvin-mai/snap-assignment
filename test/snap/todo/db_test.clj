(ns snap.todo.db-test
  (:require [clojure.test :refer :all]
            [snap.test-utils :refer [use-system test-system]]
            [snap.api.todo-list.db :as todo-list.db]
            [snap.api.todo-item.db :as todo-item.db]))

(use-fixtures :once (use-system))

(deftest todo-list-crud-test
  (let [{datasource :postgres/db} @test-system]
    (testing "can create todo-list"
      (let [test-todo-list (todo-list.db/create datasource {:name "test"})]
        (is (= (:todo-list/name test-todo-list) "test"))

        (testing "can get new todo-list"
          (let [read-todo-list (todo-list.db/get-by-id datasource (:todo-list/id test-todo-list))]
            (is (= (:todo-list/name read-todo-list)
                   (:todo-list/name test-todo-list)))))

        (testing "can get all todo-lists"
          (let [all-todo-list (todo-list.db/get-all datasource)]
            (is (= (count all-todo-list) 1))
            (is (= (:todo-list/name (first all-todo-list))
                   (:todo-list/name test-todo-list)))))

        (testing "can delete new todo-list"
          (todo-list.db/delete-by-id datasource (:todo-list/id test-todo-list))
          (is (= (count (todo-list.db/get-all datasource)) 0)))))))

(deftest todo-item-crud-test
  (let [{datasource :postgres/db} @test-system
        test-todo-list (todo-list.db/create datasource {:name "test"})]
    (testing "can create todo-item"
      (let [test-todo-item (todo-item.db/create datasource {:name "test"
                                                            :todo-list-id (:todo-list/id test-todo-list)})]
        (is (= (:todo-item/name test-todo-item) "test"))
        (is (= (:todo-item/completed test-todo-item) false))
        (testing "can get todo-item by id"
          (let [read-todo-item (todo-item.db/get-by-id datasource (:todo-item/id test-todo-item))]
            (is (= (:todo-item/id read-todo-item) (:todo-item/id test-todo-item)))))
        (testing "can get todo-item by todo-list-id"
          (let [all-todo-item (todo-item.db/get-all-by-todo-list-id datasource (:todo-list/id test-todo-list))]
            (is (= (:todo-item/id (first all-todo-item)) (:todo-item/id test-todo-item)))))
        (testing "can update todo-item"
          (let [updated-todo-item (todo-item.db/update datasource (:todo-item/id test-todo-item) {:completed true})]
            (is (= (:todo-item/id updated-todo-item) (:todo-item/id test-todo-item)))
            (is (= (:todo-item/name updated-todo-item) "test"))
            (is (= (:todo-item/completed updated-todo-item) true))))
        (testing "can delete todo-item"
          (todo-item.db/delete-by-id datasource (:todo-item/id test-todo-item))
          (is (= (count (todo-item.db/get-by-id datasource (:todo-item/id test-todo-item))) 0)))))

    (todo-list.db/delete-by-id datasource (:todo-list/id test-todo-list))))

(deftest todo-list-virtual-rows-test
  (let [{datasource :postgres/db} @test-system
        test-todo-list (todo-list.db/create datasource {:name "test"})]
    (testing "empty todo-list"
      (let [all-result (first (todo-list.db/get-all datasource))
            single-result (todo-list.db/get-by-id datasource (:todo-list/id test-todo-list))]
        (is (= (:todo-list/todo-items single-result) []))
        (is (= (:todo-items all-result) 0))
        (is (= (:todo-items-completed all-result) 0))))
    (testing "with todo-items"
      (let [test-todo-item (todo-item.db/create datasource {:name "test"
                                                            :todo-list-id (:todo-list/id test-todo-list)})
            all-result (first (todo-list.db/get-all datasource))
            single-result (todo-list.db/get-by-id datasource (:todo-list/id test-todo-list))]

        (is (= (:todo-list/todo-items single-result) [test-todo-item]))
        (is (= (:todo-items all-result) 1))
        (is (= (:todo-items-completed all-result) 0))
        (testing "with completed todo-items"
          (let [updated-todo-item (todo-item.db/update datasource (:todo-item/id test-todo-item) {:completed true})
                all-result (first (todo-list.db/get-all datasource))
                single-result (todo-list.db/get-by-id datasource (:todo-list/id test-todo-list))]
            (is (= (:todo-list/todo-items single-result) [updated-todo-item]))
            (is (= (:todo-items all-result) 1))
            (is (= (:todo-items-completed all-result) 1))))
        (todo-item.db/delete-by-id datasource (:todo-item/id test-todo-item))))
    (todo-list.db/delete-by-id datasource (:todo-list/id test-todo-list))))
