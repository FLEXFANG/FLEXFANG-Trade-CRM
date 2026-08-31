# FLEXFANG Trade CRM — CRM Research Method

Project-local method for the `FLEXFANG-Trade-CRM` project. It researches mature open-source CRM, lead-management, sales-pipeline, automation, and customer-intelligence capabilities, validates evidence and licenses, and produces a Trade-CRM architecture decision before implementation.

## 1. What this method does — and does not do

`flexfang-skill-crm-builder` researches mature GitHub CRM, lead-management, sales-pipeline, automation, enrichment and AI-agent projects, extracts the strongest capabilities, validates evidence and licenses, and produces a FLEXFANG Trade CRM architecture decision.

It is **not** a runtime CRM manager. It does not add/edit customer records, send outreach, deploy production code, or implement the CRM.

## 2. Required prerequisites

- live GitHub search/repository access;
- ability to inspect repository metadata and relevant source/docs/license files;
- Python 3.10+ for deterministic validators/tests;
- filesystem output access when artifacts are requested.

If GitHub access is unavailable, the correct result is `BLOCKED_PRECONDITION`, not an architecture guessed from memory.

## 3. Repository location

This method lives in the `FLEXFANG-Trade-CRM` repository under `docs/trade-crm/skills/`. It is project-local guidance for the Trade-CRM project; it is not a globally installed FLEXFANG Skill and requires no Skill runtime, installer, or registry.

## 4. Simplest call

> Research mature GitHub CRM projects for a B2B export business, compare the strongest data model, pipeline, automation and AI-agent capabilities, and give me an evidence-backed FLEXFANG Trade CRM architecture decision before any coding.

## 5. Input fields

| Field | Required | Default | Meaning |
|---|---:|---|---|
| `business_context` | yes | — | Business/product/buyer/sales context |
| `crm_goal` | yes | — | CRM outcome or capability to design |
| `target_markets` | no | `[]` | Markets relevant to the workflow |
| `priority_capabilities` | no | `[]` | Capabilities to emphasize |
| `candidate_target` | no | `20` | Discovery target, 15–30 |
| `shortlist_target` | no | `10` | Screened target, 5–12 |
| `deep_dive_target` | no | `5` | Deep-inspection target, 3–8 |
| `language` | no | `zh-CN` | Report language |
| `reuse_policy` | no | `adopt_adapt_learn_build` | Reuse decision framework |
| `special_constraints` | no | `[]` | Extra constraints |

Cross-field rule: `deep_dive_target <= shortlist_target <= candidate_target`.

If the two required fields can be inferred from the conversation, do not ask again. If they cannot, ask once in one combined question.

## 6. Evidence rules

- Every retained candidate needs a GitHub repository URL and evidence.
- Search snippets are discovery-only.
- Deep feature claims should be supported by implementation/docs/tests where reasonably available.
- Never fabricate stars, maintenance status, license, feature support or reuse permission.
- Unknown facts stay unknown.
- `ADOPT` / `ADAPT` requires verified license evidence and obligations.
- Missing/ambiguous license blocks direct reuse.
- Record observation dates for time-sensitive facts.

See `references/domain-rules.md` and `references/evidence-schema.json`.

## 7. Outputs

A valid research packet can be converted into:
- `FLEXFANG_CRM_OPEN_SOURCE_RESEARCH.md`
- `FLEXFANG_CRM_CAPABILITY_MATRIX.csv`
- `FLEXFANG_CRM_ARCHITECTURE_DECISION.md`
- `FLEXFANG_CRM_RESEARCH_PACKET.json`

Run:

```bash
python scripts/build_outputs.py --input research_packet.json --output-dir ./output
```

## 8. Validation and tests

Input validation:

```bash
python scripts/validate_input.py --input input.json
```

Method package validation:

```bash
python scripts/ready_check.py
```

The validator checks structure, metadata, JSON files, README coverage, Python compilation, eval definitions, automatic tests, and output read-back.

## 9. Five copyable prompts

### Prompt 1 — Full CRM landscape
> Use @flexfang-skill-crm-builder. We are a B2B export company. Search GitHub globally for mature CRM, lead, pipeline, automation and AI-agent projects. Do not filter by stars alone. Build a capability matrix, verify licenses, and give me a FLEXFANG Trade CRM architecture decision. No coding before the decision gate.

### Prompt 2 — Lead + pipeline focus
> Use @flexfang-skill-crm-builder to compare open-source lead management, lead scoring and B2B sales pipeline projects. I care most about qualification, owner/status, opportunity stages and follow-up activities. Show evidence and decide ADOPT / ADAPT / LEARN / BUILD for each capability.

### Prompt 3 — AI-native CRM focus
> Use @flexfang-skill-crm-builder to research AI-native CRM agents plus mature traditional CRMs. Compare natural-language actions, customer timeline, next-best-action logic, automation and tool/MCP architecture. Verify implementation evidence before recommending reuse.

### Prompt 4 — Existing architecture challenge
> Use @flexfang-skill-crm-builder to challenge my proposed Trade CRM architecture against mature GitHub projects. Identify where existing projects have stronger data models, APIs, pipeline or automation, which parts should be learned rather than copied, and what FLEXFANG should build itself.

### Prompt 5 — Research-to-decision package
> Use @flexfang-skill-crm-builder. Produce the complete CRM open-source research packet: candidate inventory, shortlist, deep inspections, health, license/reuse analysis, capability matrix, rejected projects, recommended stack, FLEXFANG differentiation and final architecture decision. Stop if evidence is insufficient; do not pad results.

## 10. Trigger boundary examples

Should trigger:
- "Research GitHub and design a B2B CRM architecture for us."
- "Compare Twenty/ERPNext/other CRM projects and tell me what to adopt or adapt."

Should not trigger:
- "Add these 20 Vietnamese leads to my CRM."
- "Find 20 motorcycle helmet importers in Vietnam."
- "Write a Zalo follow-up message."

## 11. Validation gate

Only this command reports the method's validation state:

```bash
python scripts/ready_check.py
```

Expected terminal result:

```text
READY
```

Any failure means `NOT READY` — do not rely on the method until it passes.
