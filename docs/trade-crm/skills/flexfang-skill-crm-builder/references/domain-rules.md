# Domain Rules — FLEXFANG CRM Builder

## 1. Research before architecture
A CRM architecture may not be presented as evidence-backed until live GitHub research has been performed. Model memory can frame search queries but cannot replace current repository inspection.

## 2. Research breadth
Default target funnel:
- discovery: 15–30 candidates;
- screen: 8–12;
- focus: 5–8;
- deep inspection: 3–5.

Do not pad the list with irrelevant or unverifiable repositories.

## 3. Candidate record
For every retained candidate capture, when available:
- name and repository URL;
- language and stars;
- last update / release signal;
- license and reuse obligations;
- main purpose and CRM type;
- self-hosted/API/database/frontend/backend/agent support;
- strengths, weaknesses and reusable parts;
- health status;
- capability findings and evidence.

Unknown facts remain `unknown`/`null`.

## 4. Health assessment
Use `ACTIVE`, `STABLE`, `AGING`, `ABANDONED`, or `UNKNOWN` based on current evidence such as recent commits/releases, documentation, open issues/PR activity, tests, and repository maintenance signals. Star count is not a quality gate.

## 5. License gate
Open source visibility is not reuse permission.
- `ADOPT` / `ADAPT` requires verified license evidence plus obligations.
- Unknown, missing, proprietary, or incompatible licensing blocks direct code reuse.
- In those cases the capability may be `LEARN`, independently `BUILD`, or `REJECT`.
- Preserve attribution and license notices when required.

## 6. Evidence hierarchy
Strongest to weakest for implementation claims:
1. implementation source files + tests;
2. technical docs/schema/API definitions;
3. release notes / maintained examples;
4. README;
5. search-result snippets.

Search-result snippets are discovery-only. README alone cannot prove a deep implementation detail when source inspection is reasonably available.

## 7. Capability fusion
Do not force one project to solve every problem. Choose the strongest source by capability, resolve conflicts, unify data models and workflows, and state what FLEXFANG must build itself.

## 8. B2B fit
The final decision must account for B2B workflows such as market research, prospecting, company discovery, qualification, contacts, outreach history, follow-up, sample, quotation, negotiation, opportunity, order, and repeat purchase when relevant.

## 9. No coding before decision gate
The minimum gate is:
`GitHub Research + Capability Matrix + Architecture Decision = DECISION_READY`.
Until then, do not start a large implementation phase.

## 10. Security and authorization
Never expose secrets or credentials. Never upload confidential customer data without approval. Never execute production-impacting actions, external communication, financial decisions, or security-sensitive operations without explicit authorization.

Repository release name: `FLEXFANG-SKILL-CRM-BUILDER`.
