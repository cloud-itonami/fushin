# com-etzhayyim-fushin — CLAUDE.md

## Identity

- **Name**: fushin (普請 — Edo-period term for assigned public-works duty:
  road/levee/castle construction and repair, sometimes ordered by the
  shogunate as 御手伝普請 and tracked by which domain fulfilled its
  assignment promptly vs. lagged. Here: a benchmark + scenario tool, never
  an enforcement mechanism.)
- **DID**: `did:web:fushin.etzhayyim.com`
- **ADR**: ADR-2607176000 (R0 scaffold, 2026-07-17) + ADR-2608176500
  (global scope broadening, 2026-07-17 — owner: prioritize severe overseas
  cases over further Japan-domestic refinement)
- **Parent/sibling ADRs**: ADR-2605301600 (danjo, discipline borrowed) ·
  ADR-2606021600 (ooyake, boundary) · ADR-2607072630 (yosoku, boundary) ·
  ADR-2605250800 (gov-municipality, template shape) · ADR-2607101558 /
  ADR-2607122100 (cloud-itonami system-dynamics, prior-art math pattern) ·
  ADR-2605192100 (Mission Charter) · ADR-2605192200 (Charter Rider) ·
  ADR-2605192300 (Council) · ADR-2605215000 (Murakumo-only, deferred)
- **Status**: R0 scaffold — SD core + seed data + tests + offline heartbeat
  are REAL and working; the two gated cells raise on call.
- **Form**: 任意団体 internal civic-infra benchmark + scenario substrate
  (NOT 一般社団/NPO/公益財団/宗教法人 法人格 — same non-entity framing as
  danjo/ooyake).

## Why this actor exists (origin)

The repo owner asked whether Japanese municipalities' infrastructure-repair
response (road, water, construction) had already been analyzed for
quality/speed, and whether a system-dynamics agent existed to make that
more efficient. Investigation found: real government data exists (MLIT's
道路メンテナンス年報, MHLW water-pipe statistics) but was not in this
workspace; the existing `ooyake` actor structurally cannot host this (G11:
"never rank governments, never a target-list"); the existing `danjo` actor
is the constitutionally-correct home for factual government-data
cross-reference but is itself still R0/unratified and gates any
named-party claim behind Council+SBT (G10/G11); `yosoku` is a
domain-agnostic scenario engine with no infra-repair model attached. fushin
fills that gap narrowly: aggregate-only benchmark + illustrative-archetype
scenario, never a named-municipality claim, until this workspace's own
Council ratifies further phases — see ADR-2607176000 for the full
reasoning.

## Constitutional Discipline (CRITICAL — IMMUTABLE at R0)

1. **Aggregate-only ingestion (G1)** — `registry/benchmark-seed.edn` /
   `registry/benchmark-seed-global.edn` never name an individual
   municipality or city. Only national/prefecture/state-level aggregate
   statistics from cited reports (this granularity — e.g. Aichi
   Prefecture, Rhode Island, South Korea, Cambodia — is explicitly within
   scope; individual-city naming, e.g. Nagoya, is not, and is recorded
   only as a known-but-uningested source per ADR-2608176500).
2. **Source-provenance mandatory (G2)** — every number carries agency,
   report title, published date, as-of date, URL.
3. **Non-adjudicating (G3)** — no claim that a municipality is negligent,
   at fault, or underperforming. Distributions and archetype scenarios are
   descriptive/illustrative only.
4. **No-named-party-scenario (G4)** — `fushin.scenario`'s
   `fast-archetype`/`slow-archetype` are NOT real municipalities. Running a
   scenario against a real named body is `fushin.cells/named-party-scenario`,
   which raises until R2 Council+SBT ratification (mirrors danjo G10/G11).
5. **Open method (G5)** — the SD equations in
   `src/fushin/infra_dynamics.cljk` are public, versioned, docstring-cited
   prior art (cloud-itonami ADR-2607101558/2607122100), not a closed model.
6. **Passive-only ingestion (G6)** — `registry/benchmark-seed.edn` cites
   already-published PDF/web reports only. No live portal scraping.
   Extending the seed is `fushin.cells/infra-benchmark-ingest`, which raises
   until R1 Council ratification.

## Boundary with sibling actors (READ BEFORE EDITING)

- **ooyake** (公) = civic wayfinding structural atlas. G11 forbids ranking
  governments or building a target-list. fushin is explicitly NOT built on
  or inside ooyake.
- **danjo** (弾正) = non-adjudicating factual cross-reference over the
  STATE's published open-data corpus (Diet statements, budget, procurement)
  — itself still R0/unratified, named-party publication SBT-gated (G10/G11).
  fushin's ingest corpus (infra-maintenance annual reports) is a distinct
  dataset genre danjo's 6 cells don't cover; if danjo reaches R1+ first, its
  ingest pattern is the one to reuse rather than duplicating machinery here.
- **yosoku** (予測) = domain-agnostic XMILE scenario-simulation actor
  (ScenarioGovernor), v1 mock-advisor only. fushin's SD core is a
  domain-specific instance of the same "scenario, not fact" posture; a
  future consolidation could have yosoku host fushin's model, but that is
  NOT done here (fushin ships its own small, testable core instead of
  taking a dependency on an unfinished sibling).
- **gov-municipality** (官) = building-permit workflow orchestration
  (submission → inspection → sign-off) for a SPECIFIC applicant's permit.
  Unrelated domain (process orchestration vs. aggregate benchmarking); the
  file-layout convention (manifest.edn/cells/*.edn/methods/) is borrowed
  from it, nothing else.

## What's real vs. gated at R0

| Path | Status |
|---|---|
| `registry/benchmark-seed.edn` | Real, cited MLIT/MHLW data (Japan, aggregate only) |
| `registry/benchmark-seed-global.edn` | Real, cited World Bank/ASCE/IBNET data (international, country/state only) |
| `src/fushin/infra_dynamics.cljk` | Real, tested SD core (Little's Law + Sterman feedback) |
| `src/fushin/scenario.cljk` | Real, tested illustrative archetypes (Japan + global building-permit) |
| `src/fushin/methods/autorun.cljk` | Real, working offline heartbeat → local chained log |
| `src/fushin/cells.cljk` | Both fns raise — gated on Council ratification |

## Build & Test

```bash
cd orgs/etzhayyim/com-etzhayyim-fushin
kbb -M:test              # 10 tests / 16 assertions, all green
kbb -M:autorun [days]    # offline heartbeat; appends to .fushin/log.edn (gitignored, local only)
```

## Non-Goals (N1–N5)

See `manifest.edn` `:actor/non-goals`. Key: NOT a real-municipality audit
or ranking (N1, Council+SBT gated at R2) · NOT civic wayfinding (N2) · NOT
live scraping (N3) · NOT a live LLM advisor yet (N4) · building-permit
domain data is `:unknown`, not fabricated (N5).

## Related Files

- `/90-docs/adr/2607176000-fushin-infra-repair-benchmark-system-dynamics-actor-r0.edn` — ADR (EDN tx-data, per ADR-2607171600 docs-EDN-only rule)
- `/registry/benchmark-seed.edn` — cited seed data
- `/src/fushin/{infra_dynamics,scenario,cells}.cljc`, `/src/fushin/methods/autorun.cljk`
- `/cells/*.edn` — declarative cell descriptors (R0 path-reserved)
