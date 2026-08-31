# Output Contract

Formal output generation is deterministic and must create real files, not renamed plain text with fake extensions.

## Required artifacts

### `FLEXFANG_CRM_OPEN_SOURCE_RESEARCH.md`
Must contain: Candidate Projects; Shortlist / Focus Candidates; Best Components; Rejected / Not Selected; License & Reuse; Capability Matrix Summary; Recommended Stack; FLEXFANG Differentiation.

### `FLEXFANG_CRM_CAPABILITY_MATRIX.csv`
UTF-8 CSV with required columns: `capability, best_source, decision, rationale`.

### `FLEXFANG_CRM_ARCHITECTURE_DECISION.md`
Must contain headings: Data Model; Pipeline; Activity System; Automation; AI Layer; API; Database; Frontend; Integrations; Skill Architecture; Agent Architecture.

### `FLEXFANG_CRM_RESEARCH_PACKET.json`
UTF-8 JSON preserving structured research evidence and decisions.

## Output gate
No formal package may be marked complete if a retained candidate lacks evidence; an `ADOPT`/`ADAPT` candidate has an unverified license; the capability matrix is empty; the architecture decision lacks a required section; or generated files cannot be parsed/read back by the test suite.
