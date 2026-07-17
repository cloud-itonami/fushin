(ns fushin.infra-dynamics
  "Infrastructure-repair backlog as a System Dynamics (Forrester, *Industrial
  Dynamics*, 1961) stock-flow continuous model. Prior art: cloud-itonami's
  factory-process model (ADR-2607101558 / ADR-2607122100) is the same
  textbook Little's-Law + Sterman stock-management pattern; this is a fresh
  implementation translated to the infra-repair domain (no cross-org dep —
  ADR-2607176000 D1), not a copy.

  Two-stock model:
    (nothing) --[arrival-rate]--> backlog --[completion-rate]--> resolved

  `arrival-rate` (new defects/judged-III-IV facilities reported per day) is
  EXOGENOUS — a municipality does not control how many potholes appear or
  how many bridges age into judgment III/IV. The controllable lever is how
  fast backlog is worked down, i.e. `mean-repair-days` (open-loop) or the
  Sterman feedback target (closed-loop) below.

  `completion-rate = backlog / mean-repair-days` is the same first-order
  production-delay structure as cloud-itonami's `completion-rate` (Little's
  Law: backlog = throughput * mean-repair-days at steady state).

  G3/G4 (ADR-2607176000): this module never takes a real municipality's
  data as input. Callers supply `arrival-rate` / `mean-repair-days` /
  `backlog-target` as parameters of an illustrative archetype (see
  `fushin.scenario`), calibrated from aggregate distributions, not fitted
  to any named real entity."
  )

(defn ->state
  "Initial stock levels. `:t` (sim time, days) defaults to 0.0."
  [{:keys [backlog resolved t] :or {resolved 0.0 t 0.0}}]
  {:backlog backlog :resolved resolved :t t})

(defn ->params
  "Open-loop params: `:arrival-rate` (new backlog units/day), `:mean-repair-days`
  (average days a unit sits in backlog before completion), `:dt` (Euler step,
  defaults to 1.0 day)."
  [{:keys [arrival-rate mean-repair-days dt] :or {dt 1.0}}]
  {:arrival-rate arrival-rate :mean-repair-days mean-repair-days :dt dt})

(defn completion-rate
  "backlog / mean-repair-days — the first-order production-delay outflow."
  [{:keys [backlog]} {:keys [mean-repair-days]}]
  (/ backlog mean-repair-days))

(defn step
  "One fixed-dt Euler step, open-loop (fixed mean-repair-days). Backlog is
  clamped at zero (can't complete more than exists)."
  [state params]
  (let [dt (:dt params)
        ar (:arrival-rate params)
        cr (completion-rate state params)]
    {:backlog (max 0.0 (+ (:backlog state) (* (- ar cr) dt)))
     :resolved (+ (:resolved state) (* cr dt))
     :t (+ (:t state) dt)}))

(defn run
  "`n` steps from `state0`. Returns a vector of `n+1` states."
  [state0 params n]
  (vec (reductions (fn [s _] (step s params)) state0 (range n))))

(defn analytic-backlog
  "Closed-form solution of d(backlog)/dt = arrival-rate - backlog/mean-repair-days:
  backlog(t) = backlog_eq + (backlog(0) - backlog_eq) * e^(-t/mean-repair-days),
  backlog_eq = arrival-rate * mean-repair-days."
  [backlog0 {:keys [arrival-rate mean-repair-days]} t]
  (let [backlog-eq (* arrival-rate mean-repair-days)]
    (+ backlog-eq (* (- backlog0 backlog-eq) (Math/exp (- (/ t mean-repair-days)))))))

;; ---------------------------------------------------------------------- ;;
;; Sterman stock-management feedback: a policy that adjusts the completion
;; rate toward a backlog-target, rather than holding mean-repair-days fixed.
;; ---------------------------------------------------------------------- ;;

(defn ->feedback-params
  "`:arrival-rate` (exogenous, as above), `:backlog-target` (the policy's
  desired steady-state backlog level), `:adjustment-time` (days — how fast
  the policy apparatus closes the gap between actual and target backlog),
  `:dt`."
  [{:keys [arrival-rate backlog-target adjustment-time dt] :or {dt 1.0}}]
  {:arrival-rate arrival-rate :backlog-target backlog-target
   :adjustment-time adjustment-time :dt dt})

(defn feedback-completion-rate
  "desired = arrival-rate + (backlog - backlog-target) / adjustment-time.
  Clamped to [0, backlog/dt] (can't un-complete, can't complete more than
  exists this step)."
  [{:keys [backlog]} {:keys [arrival-rate backlog-target adjustment-time dt]}]
  (let [desired (+ arrival-rate (/ (- backlog backlog-target) adjustment-time))]
    (max 0.0 (min desired (/ backlog dt)))))

(defn feedback-step
  [state params]
  (let [dt (:dt params)
        ar (:arrival-rate params)
        cr (feedback-completion-rate state params)]
    {:backlog (max 0.0 (+ (:backlog state) (* (- ar cr) dt)))
     :resolved (+ (:resolved state) (* cr dt))
     :t (+ (:t state) dt)}))

(defn feedback-run
  [state0 params n]
  (vec (reductions (fn [s _] (feedback-step s params)) state0 (range n))))

(defn analytic-feedback-backlog
  "Closed-form solution of d(backlog)/dt = -(backlog - backlog-target) / adjustment-time:
  backlog(t) = backlog-target + (backlog(0) - backlog-target) * e^(-t/adjustment-time).
  Note the equilibrium is EXACTLY backlog-target regardless of arrival-rate —
  the policy fully compensates for whatever arrival-rate turns out to be,
  same closed-form-verifiable guarantee as cloud-itonami's feedback module."
  [backlog0 {:keys [backlog-target adjustment-time]} t]
  (+ backlog-target (* (- backlog0 backlog-target) (Math/exp (- (/ t adjustment-time))))))

;; ---------------------------------------------------------------------- ;;
;; Quality proxy — an analog of the real report's "commencement rate" /
;; "completion rate" percentages, for a single simulated cohort (not a
;; comparison across real municipalities).
;; ---------------------------------------------------------------------- ;;

(defn resolved-share
  "resolved / (resolved + backlog) at a given state — the model's analog of
  the real report's 措置着手率/完了率 percentage. A descriptive ratio, not an
  evaluative claim (G3)."
  [{:keys [backlog resolved]}]
  (if (zero? (+ backlog resolved))
    0.0
    (/ resolved (+ backlog resolved))))
