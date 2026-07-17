(ns fushin.scenario
  "Illustrative archetype scenarios for fushin.infra-dynamics (ADR-2607176000
  G4: no-named-party-scenario). `fast-archetype` / `slow-archetype` are NOT
  real municipalities — they are two parameter sets whose RELATIVE spread
  (slow archetype's mean-repair-days / adjustment-time are longer than
  fast's) is chosen to be directionally consistent with the real,
  source-cited cohort split in registry/benchmark-seed.edn:
  `:road-bridge-repair :commencement-rate-distribution :bridge` — 52% of the
  1,712 reporting municipalities had reached a 100% commencement rate on
  judgment-III/IV bridge repairs, while 9% (159 municipalities) remained
  under a 50% commencement rate, as of FY2023 (道路メンテナンス年報 令和5年度版).

  This is a DIRECTIONAL illustration, not an econometric fit: the seed data
  is a cross-sectional count of municipalities per bucket, not a
  per-municipality backlog time series, so there is no unique 'true'
  mean-repair-days to recover from it. Treat archetype outputs as
  what-if scenario shapes, never as a factual claim about backlog size or
  identity of any real body (G3/G4)."
  (:require [fushin.infra-dynamics :as sd]))

(def fast-archetype
  "Illustrative: a cohort resembling the 52% of municipalities already at a
  100% commencement rate — short mean-repair-days, tight backlog-target."
  {:label "fast-cohort (illustrative)"
   :arrival-rate 2.0
   :mean-repair-days 30.0
   :backlog-target 60.0
   :adjustment-time 45.0})

(def slow-archetype
  "Illustrative: a cohort resembling the 9% of municipalities under a 50%
  commencement rate — long mean-repair-days, slow policy adjustment."
  {:label "slow-cohort (illustrative)"
   :arrival-rate 2.0
   :mean-repair-days 180.0
   :backlog-target 300.0
   :adjustment-time 270.0})

(defn run-open-loop
  "Run `archetype` open-loop (fixed mean-repair-days) from an empty backlog
  for `days`. Returns the trajectory (vector of states)."
  [archetype days]
  (let [params (sd/->params (select-keys archetype [:arrival-rate :mean-repair-days]))
        state0 (sd/->state {:backlog 0.0})]
    (sd/run state0 params days)))

(defn run-feedback
  "Run `archetype` under Sterman stock-management feedback toward its own
  `:backlog-target`, starting from `initial-backlog` (e.g. the open-loop
  trajectory's current backlog, to answer 'if this cohort adopted a
  backlog-target policy today, how would it converge'). Returns the
  trajectory."
  [archetype initial-backlog days]
  (let [params (sd/->feedback-params (select-keys archetype [:arrival-rate :backlog-target :adjustment-time]))
        state0 (sd/->state {:backlog initial-backlog})]
    (sd/feedback-run state0 params days)))

(defn what-if-adopt-fast-policy
  "Scenario: take `archetype`'s current open-loop backlog after `warmup-days`,
  then switch it onto `fast-archetype`'s backlog-target/adjustment-time (i.e.
  'what if a slow-cohort municipality adopted the fast-cohort's response
  policy'), and run `horizon-days` further under feedback. Returns
  {:before <backlog at switch> :after <trajectory>} — descriptive
  simulation output only (G3), not a recommendation addressed at any real
  entity."
  [archetype warmup-days horizon-days]
  (let [warmed (run-open-loop archetype warmup-days)
        backlog-at-switch (:backlog (last warmed))
        policy (select-keys fast-archetype [:arrival-rate :backlog-target :adjustment-time])
        params (sd/->feedback-params policy)
        state0 (sd/->state {:backlog backlog-at-switch})]
    {:before backlog-at-switch
     :after (sd/feedback-run state0 params horizon-days)}))
