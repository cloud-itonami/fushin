(ns fushin.cells
  "Path-reserved R0 stubs (ADR-2607176000) for capabilities that are
  DEFERRED to Council ratification — mirrors danjo's own R0 cell stubs
  (`RuntimeError(\"danjo R0 scaffold: activate via Council ADR + R1
  ratification\")`). Calling either function raises; nothing here executes
  until the corresponding ADR phase lands.

  `fushin.infra-dynamics` / `fushin.scenario` / `fushin.methods.autorun`
  are NOT gated — they are real, working, offline-only code (aggregate
  seed data + illustrative archetypes, G1-G4). Only the two capabilities
  below — ingesting beyond the R0 seed, and running a scenario against a
  real named municipality — are gated.")

(defn infra-benchmark-ingest
  "R1+ capability: ingest additional aggregate government reports beyond the
  registry/benchmark-seed.edn R0 seed. Gated on: Council Lv6+ ratification
  of this ADR's R1 phase (ADR-2607176000 :adr/phases :r1)."
  [& _args]
  (throw (ex-info "fushin R0 scaffold: activate via Council ADR + R1 ratification"
                  {:fushin/gate :r1 :fushin/adr "2607176000"})))

(defn named-party-scenario
  "R2+ capability: run fushin.scenario against a REAL named municipality
  (as opposed to the fast-cohort/slow-cohort illustrative archetypes).
  Gated on: Council Lv6+ ratification + SBT vote on a named-party
  publication path, the same discipline danjo's G10/G11 apply to
  oversightReport (ADR-2607176000 :adr/phases :r2)."
  [& _args]
  (throw (ex-info "fushin R0 scaffold: activate via Council ADR + R2 ratification (named-party path, SBT-gated)"
                  {:fushin/gate :r2 :fushin/adr "2607176000"})))
