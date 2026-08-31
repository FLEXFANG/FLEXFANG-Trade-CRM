# FLEXFANG CRM Builder Skill

Status: package status must be determined by `python scripts/ready_check.py`. Do not install a package that reports `NOT READY`.

## 1. What this Skill does — and does not do

`flexfang-skill-crm-builder` researches mature GitHub CRM, lead-management, sales-pipeline, automation, enrichment and Agent/Skill projects, extracts the strongest capabilities, validates evidence and licenses, and produces a FLEXFANG-specific CRM architecture decision.

It is **not** the runtime CRM manager. It does not add/edit customer records, send outreach, deploy production code, or replace the downstream CRM implementation agent.

## 2. Required and optional prerequisites

### Required
- live GitHub search/repository access;
- ability to inspect repository metadata and relevant source/docs/license files;
- Python 3.10+ for deterministic validators/tests;
- filesystem output access when artifacts are requested.

If GitHub access is unavailable, the correct result is `BLOCKED_PRECONDITION`, not an architecture guessed from memory.

### Optional / downstream
- `FLEXFANG-SKILL-FORGE`: for creating a downstream Skill after the architecture decision;
- `FLEXFANG-GITHUB-REPOSITORY-MANAGER`: for a later authorized build phase.

## 3. GitHub storage vs real installation

Keeping this folder in GitHub is canonical source storage. Availability must be checked through the actual target Agent or a standard Agent Skills discovery mechanism; repository presence alone is not proof, but a checkout may already be discoverable.

Before installation:
1. run `python scripts/ready_check.py`;
2. require final status `READY`;
3. install/copy the validated Skill folder using the target environment's supported Skill installation mechanism;
4. run at least one positive trigger and one negative-trigger smoke test in that environment.

Do not infer target-agent availability only from a GitHub URL, ZIP upload, or registry entry.

## 4. Simplest call

> Use @flexfang-skill-crm-builder to research mature GitHub CRM projects for a B2B export business, compare the strongest data model, pipeline, automation and AI-agent capabilities, and give me an evidence-backed FLEXFANG CRM architecture decision before any coding.

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

Full package validation:

```bash
python scripts/ready_check.py
```

The READY checker validates structure, metadata, JSON files, README coverage, Python compilation, eval definitions, automatic tests, and output read-back.

## 9. Five copyable prompts

### Prompt 1 — Full CRM landscape
> Use @flexfang-skill-crm-builder. We are a B2B export company. Search GitHub globally for mature CRM, lead, pipeline, automation and AI-agent projects. Do not filter by stars alone. Build a capability matrix, verify licenses, and give me a FLEXFANG CRM architecture decision. No coding before the decision gate.

### Prompt 2 — Lead + pipeline focus
> Use @flexfang-skill-crm-builder to compare open-source lead management, lead scoring and B2B sales pipeline projects. I care most about qualification, owner/status, opportunity stages and follow-up activities. Show evidence and decide ADOPT / ADAPT / LEARN / BUILD for each capability.

### Prompt 3 — AI-native CRM focus
> Use @flexfang-skill-crm-builder to research AI-native CRM agents and CRM Skills plus mature traditional CRMs. Compare natural-language actions, customer timeline, next-best-action logic, automation and tool/MCP architecture. Verify implementation evidence before recommending reuse.

### Prompt 4 — Existing architecture challenge
> Use @flexfang-skill-crm-builder to challenge my proposed CRM architecture against mature GitHub projects. Identify where existing projects have stronger data models, APIs, pipeline or automation, which parts should be learned rather than copied, and what FLEXFANG should build itself.

### Prompt 5 — Research-to-decision package
> Use @flexfang-skill-crm-builder. Produce the complete CRM open-source research packet: candidate inventory, shortlist, deep inspections, health, license/reuse analysis, capability matrix, rejected projects, recommended stack, FLEXFANG differentiation and final architecture decision. Stop if evidence is insufficient; do not pad results.

## 10. Trigger boundary examples

Should trigger:
- “Research GitHub and design a B2B CRM architecture for us.”
- “Compare Twenty/ERPNext/other CRM projects and tell me what to adopt or adapt.”

Should not trigger:
- “Add these 20 Vietnamese leads to my CRM.”
- “Find 20 motorcycle helmet importers in Vietnam.”
- “Write a Zalo follow-up message.”

## 11. READY rule

Only this command can open the installation gate:

```bash
python scripts/ready_check.py
```

Expected terminal result:

```text
READY
```

Any failure means `NOT READY / DO NOT INSTALL`.

Legacy runtime id: `flexfang-crm-builder`. Current runtime id: `flexfang-skill-crm-builder`.
