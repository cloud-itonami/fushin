# fushin (普請) — Infra-Maintenance Response Benchmark + System-Dynamics Scenario Actor

**DID**: `did:web:fushin.etzhayyim.com`
**Namespace**: `com.etzhayyim.fushin.*`
**ADR**: ADR-2607176000 (R0 scaffold, 2026-07-17) + ADR-2608176500 (global scope broadening, 2026-07-17)
**Status**: R0 — benchmark seed (Japan + global) + SD core + tests + offline heartbeat are real; named-municipality/city analysis is Council-gated

## Overview

fushin started as a Japan-domestic benchmark (ADR-2607176000) and was then
broadened, per owner direction, to prioritize severe, already
well-documented **overseas** infrastructure-response gaps over further
Japan-domestic refinement (ADR-2608176500) — the international gaps found
are far starker than anything in the Japan registry (e.g. a ~24x spread in
building-permit processing days between countries).

fushin answers, honestly and narrowly: *how does public infrastructure
maintenance/repair response (road/bridge, water, building-permit) vary in
aggregate — nationally, and across Japan/US states or prefectures — and
what does a system-dynamics scenario of "a cohort that responds faster"
look like?*

It does **not** answer "which specific municipality/city is slow" — that
would be a named-party claim, and this workspace already gates named-party
publication about government bodies behind Council Lv6+ ratification + SBT
vote (the same discipline the sibling actor `danjo` applies to its own
oversight reports). Country- and state/prefecture-level citation IS within
scope (see `CLAUDE.md` G1). See `CLAUDE.md` for the full constitutional
boundary with `danjo` / `ooyake` / `yosoku`.

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
    794 (prefectures) down to 156-158 (small cities/Tokyo wards). Additionally,
    Aichi Prefecture publishes its own prefectural-authority vs.
    private-designated-body review-day comparison (FY2025 cumulative: 62 vs
    35 days for structural-conformity-required cases) — the one real,
    specific processing-DAY comparison found, at prefecture level (G1
    permits this granularity). Nagoya City publishes an equivalent
    municipality-level statistic, but it is deliberately NOT ingested here
    (only listed as a known source) since comparing it against Aichi's
    number would be an individual-municipality comparison, which G1 reserves
    for a Council-ratified R1+ path.
- **`src/fushin/infra_dynamics.cljc`** — a Little's-Law + Sterman
  stock-management system-dynamics core (backlog → completion-rate →
  resolved), the same textbook pattern as `cloud-itonami`'s factory model
  (ADR-2607101558/2607122100), freshly implemented for this domain.
- **`registry/benchmark-seed-global.edn`** — real, cited, country/state-level
  international statistics (ADR-2608176500):
  - **Building permits**: World Bank *Time required to build a warehouse*
    (`IC.WRH.DURS`, 2019, indicator discontinued 2021-09-16) — global average
    154 days; fastest South Korea 27.5 days; slowest Cambodia 652 days (~24x
    South Korea).
  - **Water (non-revenue water / NRW)**: World Bank/IBNET — global average
    35% water loss, developing-country average 60%; worst documented: Iraq
    60%, Jordan 50%, Lebanon 48%; Sub-Saharan Africa worst Nigeria 61.8% vs
    best-in-region Niger 15.86%.
  - **Roads (US)**: ASCE 2025 Infrastructure Report Card — national grade D+,
    13.1% of major roadways in poor condition, $2.2T/$3.5T investment gap
    (2024-2033); worst state Rhode Island (36.2% poor-condition), 9 states
    with a worsening trend.
- **`src/fushin/scenario.cljc`** — illustrative archetypes whose
  `:mean-repair-days` is real, cited data (not invented) where available:
  `fast-cohort`/`slow-cohort` (directionally calibrated from the Japan
  bridge-repair distribution) and `global-best-practice`/`global-average`/
  `global-crisis` (South Korea/World-Bank-average/Cambodia's real
  building-permit day counts). None of these are real municipalities or
  countries being ranked against each other by name in a claim — they are
  scenario parameter sets, with the day-count itself being the one real,
  cited number.
- **`src/fushin/methods/autorun.cljc`** — an offline heartbeat that runs
  both Japan-domestic archetypes and appends a content-addressed, hash-chained
  record to a local, gitignored log. No external I/O, no publication.

```bash
cd orgs/etzhayyim/com-etzhayyim-fushin
clojure -M:test            # 13 tests, 23 assertions, green
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
- `/90-docs/adr/2608176500-fushin-global-priority-scope-broadening.edn` (superproject)
