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

;; ---------------------------------------------------------------------- ;;
;; Global building-permit archetypes (ADR-2608176500, scope broadening per
;; owner direction: prioritize severe, already-documented OVERSEAS cases
;; over further Japan-domestic refinement). Unlike fast-archetype/
;; slow-archetype above, `:mean-repair-days` here is REAL — the World
;; Bank's last-collected (2019, indicator discontinued 2021) "time to build
;; a warehouse" figure for the named country. `:arrival-rate` /
;; `:backlog-target` / `:adjustment-time` remain illustrative (no public
;; arrival-rate time series exists for any of these), same discipline as
;; the domestic archetypes: only the day-count is a real, cited number.
;; ---------------------------------------------------------------------- ;;

(def global-best-practice-archetype
  "Real mean-repair-days: South Korea, 27.5 days (World Bank, 2019 — see
  registry/benchmark-seed-global.edn :building-permit-global :fastest)."
  {:label "global-best-practice (South Korea, real days; arrival/target illustrative)"
   :arrival-rate 1.0
   :mean-repair-days 27.5
   :backlog-target 27.5
   :adjustment-time 20.0})

(def global-average-archetype
  "Real mean-repair-days: World Bank global average, 154 days (2019)."
  {:label "global-average (World Bank global average, real days; arrival/target illustrative)"
   :arrival-rate 1.0
   :mean-repair-days 154.0
   :backlog-target 154.0
   :adjustment-time 100.0})

(def global-crisis-archetype
  "Real mean-repair-days: Cambodia, 652 days (World Bank, 2019) — ~24x
  South Korea's figure. The starkest, most policy-relevant gap found in
  either the domestic or global registry so far."
  {:label "global-crisis (Cambodia, real days; arrival/target illustrative)"
   :arrival-rate 1.0
   :mean-repair-days 652.0
   :backlog-target 652.0
   :adjustment-time 400.0})

(defn what-if-crisis-adopts-best-practice
  "Scenario: take the global-crisis-archetype's open-loop backlog after
  `warmup-days`, then switch it onto global-best-practice-archetype's
  backlog-target/adjustment-time, and run `horizon-days` further under
  feedback. Same shape as what-if-adopt-fast-policy, applied to the global
  (not Japan-domestic) archetype pair. Descriptive simulation output only
  (G3) — a scenario shape, not a claim or recommendation addressed at any
  real country."
  [warmup-days horizon-days]
  (let [warmed (run-open-loop global-crisis-archetype warmup-days)
        backlog-at-switch (:backlog (last warmed))
        policy (select-keys global-best-practice-archetype [:arrival-rate :backlog-target :adjustment-time])
        params (sd/->feedback-params policy)
        state0 (sd/->state {:backlog backlog-at-switch})]
    {:before backlog-at-switch
     :after (sd/feedback-run state0 params horizon-days)}))

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
