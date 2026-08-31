# FLEXFANG-Trade-CRM — Repository Instructions

## PROJECT

FLEXFANG Trade CRM — the active FLEXFANG business repository: a customized fork of the
RuoYi-Vue Pro (yudao) platform (Spring Boot + MyBatis Plus + Vue/Element admin system)
with trade/CRM-focused business customization.

This is **not** the raw upstream project. Upstream is the foundation; the FLEXFANG
customization is the product.

## SOURCE OF TRUTH

- GitHub default branch (`master`) and this repository's files.
- Current business code and completed customizations in this repository.
- Owner-approved decisions.

The upstream README content (below the FLEXFANG banner in `README.md`) is reference
documentation only. Chat history is not repository truth.

## INSPECT

Before editing, verify:

- current branch, HEAD, and working tree.
- what is upstream baseline versus FLEXFANG customization: check git history and diffs
  before changing business code.
- the module layout (`yudao-module-*`) before touching business logic.

## WORK — non-negotiable rules

- Preserve upstream base capabilities and the completed business customizations; do not
  redo the platform.
- Do not refactor business code outside the requested change.
- Keep changes compatible with the existing Maven multi-module build and the Vue UI.
- Use a Skill only when it directly helps the task; no mandatory Skill chain is required.

## VALIDATE

- Compile/test the affected Maven module(s) when the environment supports it.
- For UI changes, run the relevant `yudao-ui` lint/build when available.
- Verify no business regression in CRM/trade flows.
- Commit and push only Owner-approved stable states; local HEAD = `origin/master` with a
  clean workspace.

## HANDOFF

Report changed files, validation evidence, and blockers.
