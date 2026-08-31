---
name: flexfang-skill-crm-builder
version: "1.1.0"
description: >-
  Evidence-first B2B CRM research and architecture Skill. Use when the user asks to research,
  compare, select, combine, or architect CRM, lead-management, sales-pipeline, sales-automation,
  customer-intelligence, or CRM Agent/Skill solutions from GitHub before building a FLEXFANG CRM.
  Do not use for routine CRM CRUD, one-off prospecting, outreach copywriting, or direct production changes.
---

# FLEXFANG CRM Builder

## Mission
Research proven CRM and adjacent open-source capabilities before proposing a FLEXFANG CRM architecture. The operating loop is:

`DISCOVER -> ANALYZE -> EXTRACT -> COMPARE -> SELECT -> FUSE -> IMPROVE -> DECIDE`

This Skill is a **research + architecture decision** Skill. It does not silently become a production implementation agent.

## Trigger contract

### Trigger when
Use this Skill when the user asks to:
- design or architect a CRM system or CRM Skill using mature GitHub projects;
- compare CRM / sales pipeline / lead management / automation / enrichment projects;
- decide `ADOPT / ADAPT / LEARN / BUILD / REJECT` for CRM capabilities;
- produce an evidence-backed CRM open-source research report, capability matrix, or architecture decision;
- combine multiple mature implementations into a FLEXFANG-specific B2B CRM design.

### Do not trigger when
Do not use this Skill for:
- routine CRM operations such as “add these 20 leads”, “change this lead to high intent”, or “show overdue follow-ups”;
- one-off B2B prospecting or market-lead discovery;
- drafting sales emails, WhatsApp/Zalo/LinkedIn messages, or quotations;
- generic coding tasks that do not require CRM landscape research;
- direct production deployment, destructive repository changes, or external communication.

## Required inputs
Resolve from the current conversation before asking the user.

Required:
- `business_context`: what the business sells, who it sells to, and the CRM context.
- `crm_goal`: the CRM outcome/capability/system the user wants to design or evaluate.

Optional fields and defaults are defined in `references/input-schema.json`.

If a required field cannot be inferred, ask **one combined clarification question**. Never ask for optional fields merely to fill a form.

## Hard prerequisites
Before research begins, verify:
1. live GitHub repository/search access is available;
2. repository metadata and relevant repository files can be inspected;
3. output file writing is available if formal artifacts are requested.

If GitHub access is unavailable, return `BLOCKED_PRECONDITION`. Do **not** substitute model memory for the required GitHub research.

Soft/downstream dependencies:
- FLEXFANG-SKILL-FORGE: used only after the architecture decision when creating a downstream Skill.
- FLEXFANG-GITHUB-REPOSITORY-MANAGER: used only if a later build phase is explicitly authorized.

## Research gate
The first research pass must aim for:
- 15–30 discovered candidates;
- 8–12 initially screened candidates;
- 5–8 focused candidates;
- 3–5 deep inspections.

These are quality-guided targets, not permission to pad results. If the public GitHub landscape yields fewer qualified projects after reasonable coverage, report the shortfall.

Before `DECISION_READY`, the packet must contain:
- candidate inventory;
- health and maintenance assessment;
- license assessment;
- implementation evidence for material capability claims;
- capability matrix;
- reject rationale;
- `ADOPT / ADAPT / LEARN / BUILD / REJECT` decisions;
- recommended stack;
- FLEXFANG differentiation;
- architecture decision.

No implementation phase may start before `DECISION_READY`.

## Evidence and data rules
Load `references/domain-rules.md` and `references/evidence-schema.json`.

Hard rules:
- Never invent stars, last-update dates, licenses, APIs, schemas, features, contacts, adoption claims, or project health.
- Every retained project must have a repository URL and at least one evidence record.
- README-only evidence may support positioning but cannot by itself prove deep implementation behavior.
- `ADOPT` or `ADAPT` requires a verified license source and recorded obligations.
- Unknown or ambiguous license means the code is not approved for reuse; use `LEARN`, `BUILD`, or `REJECT` as appropriate.
- Record `observed_at` for time-sensitive repository facts.
- If a claim cannot be verified, mark it `unknown` rather than guessing.
- Never expose secrets, credentials, private customer data, or take production-impacting actions without authorization.

## Deterministic scripts
Use scripts for repeatable validation and packaging:

- `python scripts/validate_input.py --input <input.json> [--output <normalized.json>]`
- `python scripts/build_outputs.py --input <research_packet.json> --output-dir <dir>`
- `python scripts/ready_check.py`

Scripts are local-only and perform no network calls or external writes outside the requested output directory.

## Outputs
When formal artifacts are requested, the deterministic builder generates real files:
- `FLEXFANG_CRM_OPEN_SOURCE_RESEARCH.md`
- `FLEXFANG_CRM_CAPABILITY_MATRIX.csv`
- `FLEXFANG_CRM_ARCHITECTURE_DECISION.md`
- `FLEXFANG_CRM_RESEARCH_PACKET.json`

The output contract is in `references/output-contract.md`.

## Completion states
Use explicit states:
- `INPUT_READY`
- `RESEARCH_IN_PROGRESS`
- `RESEARCH_GATE_PASS`
- `DECISION_READY`
- `BLOCKED_PRECONDITION`
- `NOT_READY`
- `READY`

`READY` is reserved for the **Skill package validation state** and may only be reported when `python scripts/ready_check.py` exits successfully.

## Safety and authorization
Human approval is required before production deployment, external communication, financial decisions, or security-sensitive operations. The Skill itself does not send outreach, change production CRM data, or deploy code.
