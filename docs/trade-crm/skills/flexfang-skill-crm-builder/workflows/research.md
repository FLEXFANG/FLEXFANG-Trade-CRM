# Research Workflow

1. Resolve `business_context` and `crm_goal` from the conversation.
2. Validate normalized input with `scripts/validate_input.py`.
3. Verify live GitHub access. If unavailable: `BLOCKED_PRECONDITION`.
4. Search globally across relevant CRM categories and all star levels.
5. Build discovery pool (target 15–30).
6. Screen repository health, relevance and licensing.
7. Inspect 5–8 focused candidates beyond README where material.
8. Deep-inspect 3–5 candidates: docs, schemas, models, services, API, workflow, agents/skills/prompts, tests, releases as applicable.
9. Record evidence per claim with observation date.
10. Build capability matrix and reject rationale.
11. Decide `ADOPT / ADAPT / LEARN / BUILD / REJECT` by capability.
12. Produce recommended stack and FLEXFANG differentiation.
13. Pass packet to `workflows/decision-gate.md`.
