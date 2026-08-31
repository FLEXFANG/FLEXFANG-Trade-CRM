# Decision Gate

A research run becomes `DECISION_READY` only when all conditions pass:

- required business inputs are resolved;
- live GitHub research was performed;
- retained candidates include repository URLs and evidence;
- project health is assessed or explicitly UNKNOWN;
- license status is recorded for each retained candidate;
- `ADOPT` / `ADAPT` candidates have verified license evidence;
- a capability matrix exists;
- rejected/not-selected projects have rationale;
- recommended stack is explicit;
- FLEXFANG-owned differentiation is explicit;
- architecture decision contains every required section;
- deterministic output builder validates the packet.

If any condition fails, status is `NOT_READY` or `BLOCKED_PRECONDITION`. Do not proceed to implementation.
