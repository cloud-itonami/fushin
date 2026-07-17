# fushin (普請) — Infra-Maintenance Response Benchmark + System-Dynamics Scenario Actor

**DID**: `did:web:fushin.etzhayyim.com`
**Namespace**: `com.etzhayyim.fushin.*`
**ADR**: ADR-2607176000 (R0 scaffold, 2026-07-17)
**Status**: R0 — benchmark seed + SD core + tests + offline heartbeat are real; named-municipality analysis is Council-gated

## Overview

fushin answers, honestly and narrowly: *how does Japanese public
infrastructure-repair response (road/bridge, water pipe — building-permit
data not yet found) vary in aggregate, and what does a system-dynamics
scenario of "a cohort that responds faster" look like?*

It does **not** answer "which specific municipality is slow" — that would
be a named-party claim, and this workspace already gates named-party
publication about government bodies behind Council Lv6+ ratification + SBT
vote (the same discipline the sibling actor `danjo` applies to its own
oversight reports). See `CLAUDE.md` for the full constitutional boundary
with `danjo` / `ooyake` / `yosoku`.

## What's here (real, working, tested)

- **`registry/benchmark-seed.edn`** — aggregate, source-cited statistics:
  - 国土交通省道路局 *道路メンテナンス年報（令和5年度版, 2024-08）*: across
    1,712 municipalities managing judgment-III/IV bridges, 52% had reached
    a 100% repair-commencement rate, while 9% (159 municipalities) remained
    under 50%; as of FY2023-end, 24% of 1,781 municipalities had made zero
    progress on required bridge repairs.
  - 厚生労働省 FY2022 water-pipe statistics (via EY Japan / 日本水道セキュリティ戦略機構
    2024 analysis, 財務省政策研究所 2025 analysis): national aging rate 23.6%
    (past the 40-year statutory service life), renewal rate 0.64%/year,
    seismic-resistance rate ranging 24.8%–73.6% across prefectures.
  - Building-permit (建築確認): 国土交通省「効率的かつ実効性ある確認検査制度等のあり方の検討」
    (H23-25年度データ, older than the other two domains): statutory review
    limits are 35 days (larger/complex buildings) / 7 days (small wood-frame
    houses, ~70% of volume); national completion-inspection rate rose from
    38% (H10) to 91% (H23); average annual confirmations per body range from
    794 (prefectures) down to 156-158 (small cities/Tokyo wards) — a
    structural workload-capacity proxy, not a direct processing-day
    comparison. No source comparing actual processing DAYS across bodies
    was found — that remains a follow-up.
- **`src/fushin/infra_dynamics.cljc`** — a Little's-Law + Sterman
  stock-management system-dynamics core (backlog → completion-rate →
  resolved), the same textbook pattern as `cloud-itonami`'s factory model
  (ADR-2607101558/2607122100), freshly implemented for this domain.
- **`src/fushin/scenario.cljc`** — two **illustrative** archetypes,
  `fast-cohort` / `slow-cohort`, whose relative parameter spread is
  directionally calibrated from the real distribution above — NOT real
  municipalities.
- **`src/fushin/methods/autorun.cljc`** — an offline heartbeat that runs
  both archetypes and appends a content-addressed, hash-chained record to a
  local, gitignored log. No external I/O, no publication.

```bash
cd orgs/etzhayyim/com-etzhayyim-fushin
clojure -M:test            # 10 tests, 16 assertions, green
clojure -M:autorun 365     # run one offline heartbeat cycle
```

## What's gated (R0 path-reserved, NOT active)

- `fushin.cells/infra-benchmark-ingest` — ingesting more aggregate reports
  beyond the R0 seed.
- `fushin.cells/named-party-scenario` — running a scenario against a REAL
  named municipality.

Both raise `ex-info` on call. Activation requires Council ratification per
`ADR-2607176000` `:adr/phases` (`:r1` / `:r2`).

## Posture

Like `ooyake` ("civic wayfinding map, never a target-list") and `danjo`
("the censor's eye, never the censor's sword"): fushin is a **descriptive
benchmark + illustrative scenario tool**, not an auditor, not a
regulator, not a ranking product. It never claims a fact about any real
municipality's performance.

## Status

**R0.** No cells activated. Named-party analysis requires Council Lv6+
ratification (see `manifest.edn`, `CLAUDE.md`).

## Related Files

- `/CLAUDE.md` — full constitutional discipline + sibling-actor boundary
- `/manifest.edn` / `/manifest.jsonld` — actor manifest + DID manifest
- `/90-docs/adr/2607176000-fushin-infra-repair-benchmark-system-dynamics-actor-r0.edn` (superproject)
