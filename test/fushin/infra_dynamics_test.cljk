(ns fushin.infra-dynamics-test
  (:require [clojure.test :refer [deftest is testing]]
            [fushin.infra-dynamics :as sd]))

(deftest open-loop-converges-to-analytic-equilibrium
  (testing "simulated backlog converges toward analytic-backlog as dt shrinks"
    (let [params (sd/->params {:arrival-rate 2.0 :mean-repair-days 30.0 :dt 0.1})
          state0 (sd/->state {:backlog 0.0})
          n 1000 ;; 100 days at dt=0.1
          trajectory (sd/run state0 params n)
          simulated (:backlog (last trajectory))
          analytic (sd/analytic-backlog 0.0 params 100.0)]
      (is (< (Math/abs (- simulated analytic)) 0.5)))))

(deftest open-loop-equilibrium-is-arrival-times-mean-repair-days
  (testing "Little's Law: backlog_eq = arrival-rate * mean-repair-days"
    (let [params (sd/->params {:arrival-rate 2.0 :mean-repair-days 30.0})]
      (is (= 60.0 (sd/analytic-backlog 60.0 params 50.0))))))

(deftest backlog-never-goes-negative
  (testing "a large mean-repair-days step never drives backlog below zero"
    (let [params (sd/->params {:arrival-rate 0.0 :mean-repair-days 1.0 :dt 5.0})
          state0 (sd/->state {:backlog 1.0})
          trajectory (sd/run state0 params 3)]
      (is (every? #(>= (:backlog %) 0.0) trajectory)))))

(deftest feedback-converges-to-backlog-target-regardless-of-arrival-rate
  (testing "Sterman feedback equilibrium is exactly backlog-target"
    (doseq [arrival [0.5 2.0 10.0]]
      (let [params (sd/->feedback-params {:arrival-rate arrival :backlog-target 100.0
                                           :adjustment-time 20.0 :dt 0.1})
            state0 (sd/->state {:backlog 500.0})
            trajectory (sd/feedback-run state0 params 2000) ;; 200 days
            simulated (:backlog (last trajectory))]
        (is (< (Math/abs (- simulated 100.0)) 1.0))))))

(deftest feedback-analytic-matches-simulated
  (testing "closed-form analytic-feedback-backlog tracks the Euler simulation"
    (let [params (sd/->feedback-params {:arrival-rate 2.0 :backlog-target 60.0
                                         :adjustment-time 45.0 :dt 0.05})
          state0 (sd/->state {:backlog 300.0})
          trajectory (sd/feedback-run state0 params 2000) ;; 100 days
          simulated (:backlog (last trajectory))
          analytic (sd/analytic-feedback-backlog 300.0 params 100.0)]
      (is (< (Math/abs (- simulated analytic)) 0.5)))))

(deftest resolved-share-is-descriptive-ratio
  (testing "resolved-share is 0 with no resolved units, and rises as backlog clears"
    (is (= 0.0 (sd/resolved-share {:backlog 10.0 :resolved 0.0})))
    (is (= 0.0 (sd/resolved-share {:backlog 0.0 :resolved 0.0})))
    (is (= 0.5 (sd/resolved-share {:backlog 10.0 :resolved 10.0})))))
