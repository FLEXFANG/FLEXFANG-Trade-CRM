#!/usr/bin/env python3
"""Hard installation gate for the FLEXFANG CRM Builder Skill package."""
from __future__ import annotations
import json, py_compile, subprocess, sys
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
REQUIRED = ["SKILL.md","README.md","agents/openai.yaml","references/domain-rules.md","references/input-schema.json","references/evidence-schema.json","references/output-contract.md","scripts/validate_input.py","scripts/build_outputs.py","scripts/ready_check.py","assets/output-assets/README.md","evals/evals.json","tests/test_skill.py","tests/fixtures/valid_input.json","tests/fixtures/valid_packet.json","workflows/research.md","workflows/decision-gate.md"]
README_SECTIONS = ["What this Skill does","Required and optional prerequisites","GitHub storage vs real installation","Simplest call","Input fields","Evidence rules","Outputs","Validation and tests","Five copyable prompts","READY rule"]
def fail(message: str) -> int:
    print(f"NOT READY: {message}", file=sys.stderr); return 2
def main() -> int:
    missing = [path for path in REQUIRED if not (ROOT / path).is_file()]
    if missing: return fail("missing required files: " + ", ".join(missing))
    skill = (ROOT / "SKILL.md").read_text(encoding="utf-8")
    for needle in ("name: flexfang-skill-crm-builder", "description:", "## Trigger contract", "## Hard prerequisites", "## Evidence and data rules"):
        if needle not in skill: return fail(f"SKILL.md missing required contract marker: {needle}")
    agent_yaml = (ROOT / "agents/openai.yaml").read_text(encoding="utf-8")
    for needle in ("interface:", "display_name:", "short_description:", "default_prompt:"):
        if needle not in agent_yaml: return fail(f"agents/openai.yaml missing: {needle}")
    for rel in ("references/input-schema.json", "references/evidence-schema.json", "evals/evals.json"):
        try: json.loads((ROOT / rel).read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc: return fail(f"invalid JSON in {rel}: {exc}")
    evals = json.loads((ROOT / "evals/evals.json").read_text(encoding="utf-8")); cases = evals.get("cases")
    if not isinstance(cases, list) or len(cases) < 8: return fail("evals/evals.json must contain at least 8 cases")
    ids = [case.get("id") for case in cases]
    if len(ids) != len(set(ids)) or any(not case_id for case_id in ids): return fail("eval case ids must be non-empty and unique")
    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    for section in README_SECTIONS:
        if section not in readme: return fail(f"README.md missing section: {section}")
    for rel in ("scripts/validate_input.py", "scripts/build_outputs.py", "scripts/ready_check.py", "tests/test_skill.py"):
        try: py_compile.compile(str(ROOT / rel), doraise=True)
        except py_compile.PyCompileError as exc: return fail(f"python compile failed for {rel}: {exc}")
    proc = subprocess.run([sys.executable,"-m","unittest","discover","-s",str(ROOT / "tests"),"-p","test_*.py","-v"], cwd=ROOT, text=True, capture_output=True, check=False)
    sys.stdout.write(proc.stdout); sys.stderr.write(proc.stderr)
    if proc.returncode != 0: return fail("automatic tests failed")
    print("READY"); return 0
if __name__ == "__main__": raise SystemExit(main())
