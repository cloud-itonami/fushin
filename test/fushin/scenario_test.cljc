(ns fushin.scenario-test
  (:require [clojure.test :refer [deftest is testing]]
            [fushin.scenario :as sc]))

(deftest fast-archetype-reaches-lower-steady-backlog-than-slow
  (testing "the illustrative fast-cohort settles at a lower backlog than the slow-cohort"
    (let [fast-traj (sc/run-open-loop sc/fast-archetype 3000)
          slow-traj (sc/run-open-loop sc/slow-archetype 3000)]
      (is (< (:backlog (last fast-traj)) (:backlog (last slow-traj)))))))

(deftest fast-archetype-has-shorter-mean-repair-days
  (testing "archetype spread is directionally consistent with the cited cohort split"
    (is (< (:mean-repair-days sc/fast-archetype) (:mean-repair-days sc/slow-archetype)))
    (is (< (:adjustment-time sc/fast-archetype) (:adjustment-time sc/slow-archetype)))))

(deftest what-if-adopting-fast-policy-reduces-backlog-over-horizon
  (testing "a slow-cohort backlog trends down after switching onto the fast-cohort's policy"
    (let [{:keys [before after]} (sc/what-if-adopt-fast-policy sc/slow-archetype 365 730)
          final-backlog (:backlog (last after))]
      (is (pos? before))
      (is (< final-backlog before)))))

(deftest run-feedback-respects-archetype-backlog-target
  (testing "running an archetype under its own feedback policy converges near its own backlog-target"
    (let [traj (sc/run-feedback sc/fast-archetype 500.0 5000)
          final-backlog (:backlog (last traj))]
      (is (< (Math/abs (- final-backlog (:backlog-target sc/fast-archetype))) 2.0)))))
