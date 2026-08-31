#!/usr/bin/env python3
"""Validate a CRM research packet and emit real Markdown/CSV/JSON artifacts."""
from __future__ import annotations

import argparse, csv, io, json, os, re, sys, tempfile
from datetime import date
from pathlib import Path
from typing import Any

ARCH_SECTIONS = ["Data Model","Pipeline","Activity System","Automation","AI Layer","API","Database","Frontend","Integrations","Skill Architecture","Agent Architecture"]
REUSE_DECISIONS = {"ADOPT","ADAPT","LEARN","BUILD","REJECT"}
HEALTH = {"ACTIVE","STABLE","AGING","ABANDONED","UNKNOWN"}
DATE_RE = re.compile(r"^\d{4}-\d{2}-\d{2}$")
EVIDENCE_KINDS = {"repo_metadata","readme","docs","implementation","tests","license","release","issue","pull_request"}
class PacketValidationError(ValueError): pass

def _nonempty(value: Any, label: str) -> str:
    if not isinstance(value, str) or not value.strip(): raise PacketValidationError(f"{label} must be a non-empty string")
    return value.strip()
def _valid_date(value: Any, label: str) -> str:
    text = _nonempty(value, label)
    if not DATE_RE.match(text): raise PacketValidationError(f"{label} must use YYYY-MM-DD")
    try: date.fromisoformat(text)
    except ValueError as exc: raise PacketValidationError(f"{label} is not a valid calendar date") from exc
    return text

def validate_packet(packet: dict[str, Any]) -> None:
    if not isinstance(packet, dict): raise PacketValidationError("research packet must be a JSON object")
    for key in ("as_of","business_context","crm_goal","candidates","capability_matrix","recommended_stack","flexfang_differentiation","architecture_decision"):
        if key not in packet: raise PacketValidationError(f"missing required packet field: {key}")
    _valid_date(packet["as_of"], "as_of"); _nonempty(packet["business_context"], "business_context"); _nonempty(packet["crm_goal"], "crm_goal")
    candidates = packet["candidates"]
    if not isinstance(candidates, list) or not candidates: raise PacketValidationError("candidates must be a non-empty array")
    seen_urls: set[str] = set()
    for idx, candidate in enumerate(candidates):
        prefix=f"candidates[{idx}]"
        if not isinstance(candidate, dict): raise PacketValidationError(f"{prefix} must be an object")
        name=_nonempty(candidate.get("name"),f"{prefix}.name"); repo_url=_nonempty(candidate.get("repo_url"),f"{prefix}.repo_url")
        if not repo_url.startswith("https://github.com/"): raise PacketValidationError(f"{prefix}.repo_url must be a github.com URL")
        if repo_url in seen_urls: raise PacketValidationError(f"duplicate repository URL: {repo_url}")
        seen_urls.add(repo_url); _valid_date(candidate.get("observed_at"),f"{prefix}.observed_at")
        if candidate.get("health_status") not in HEALTH: raise PacketValidationError(f"{prefix}.health_status is invalid")
        language=candidate.get("language")
        if language is not None and not isinstance(language,str): raise PacketValidationError(f"{prefix}.language must be string or null")
        stars=candidate.get("stars")
        if stars is not None and (isinstance(stars,bool) or not isinstance(stars,int) or stars<0): raise PacketValidationError(f"{prefix}.stars must be a non-negative integer or null")
        last_update=candidate.get("last_update")
        if last_update is not None and not isinstance(last_update,str): raise PacketValidationError(f"{prefix}.last_update must be string or null")
        capabilities=candidate.get("capabilities",[])
        if not isinstance(capabilities,list) or any(not isinstance(v,str) or not v.strip() for v in capabilities): raise PacketValidationError(f"{prefix}.capabilities must be an array of non-empty strings")
        evidence=candidate.get("evidence")
        if not isinstance(evidence,list) or not evidence: raise PacketValidationError(f"{prefix} ({name}) must contain at least one evidence record")
        for eidx,item in enumerate(evidence):
            ep=f"{prefix}.evidence[{eidx}]"
            if not isinstance(item,dict): raise PacketValidationError(f"{ep} must be an object")
            _nonempty(item.get("url"),f"{ep}.url"); kind=_nonempty(item.get("kind"),f"{ep}.kind")
            if kind not in EVIDENCE_KINDS: raise PacketValidationError(f"{ep}.kind is invalid")
            _nonempty(item.get("claim"),f"{ep}.claim")
            if item.get("support_level") not in {"direct","supporting","discovery_only"}: raise PacketValidationError(f"{ep}.support_level is invalid")
            _valid_date(item.get("observed_at"),f"{ep}.observed_at")
        license_info=candidate.get("license")
        if not isinstance(license_info,dict): raise PacketValidationError(f"{prefix}.license must be an object")
        status=license_info.get("status")
        if status not in {"verified","unknown"}: raise PacketValidationError(f"{prefix}.license.status must be verified or unknown")
        obligations=license_info.get("obligations")
        if not isinstance(obligations,list) or any(not isinstance(v,str) or not v.strip() for v in obligations): raise PacketValidationError(f"{prefix}.license.obligations must be an array of non-empty strings")
        decision=candidate.get("reuse_decision")
        if decision not in REUSE_DECISIONS: raise PacketValidationError(f"{prefix}.reuse_decision is invalid")
        _nonempty(candidate.get("decision_reason"),f"{prefix}.decision_reason")
        if decision in {"ADOPT","ADAPT"}:
            if status!="verified": raise PacketValidationError(f"{prefix}: {decision} requires verified license evidence")
            if not license_info.get("spdx") or not license_info.get("source_url"): raise PacketValidationError(f"{prefix}: {decision} requires license SPDX/name and source_url")
    matrix=packet["capability_matrix"]
    if not isinstance(matrix,list) or not matrix: raise PacketValidationError("capability_matrix must be a non-empty array")
    for idx,row in enumerate(matrix):
        if not isinstance(row,dict): raise PacketValidationError(f"capability_matrix[{idx}] must be an object")
        for key in ("capability","best_source","decision","rationale"): _nonempty(row.get(key),f"capability_matrix[{idx}].{key}")
        if row["decision"] not in REUSE_DECISIONS: raise PacketValidationError(f"capability_matrix[{idx}].decision is invalid")
    for key in ("recommended_stack","flexfang_differentiation"):
        value=packet[key]
        if not isinstance(value,list) or not value or any(not isinstance(v,str) or not v.strip() for v in value): raise PacketValidationError(f"{key} must be a non-empty array of strings")
    architecture=packet["architecture_decision"]
    if not isinstance(architecture,dict): raise PacketValidationError("architecture_decision must be an object")
    missing=[section for section in ARCH_SECTIONS if not isinstance(architecture.get(section),str) or not architecture[section].strip()]
    if missing: raise PacketValidationError("architecture_decision missing sections: "+", ".join(missing))

def _atomic_write(path:Path,text:str)->None:
    path.parent.mkdir(parents=True,exist_ok=True); fd,tmp=tempfile.mkstemp(prefix=path.name+".",dir=str(path.parent),text=True)
    try:
        with os.fdopen(fd,"w",encoding="utf-8",newline="") as handle: handle.write(text)
        os.replace(tmp,path)
    except Exception:
        try: os.unlink(tmp)
        except FileNotFoundError: pass
        raise

def render_research(packet):
    candidates=packet["candidates"]; adopted=[c for c in candidates if c["reuse_decision"] in {"ADOPT","ADAPT","LEARN","BUILD"}]; rejected=[c for c in candidates if c["reuse_decision"]=="REJECT"]
    lines=["# CRM OPEN-SOURCE RESEARCH","",f"**As of:** {packet['as_of']}","","## Candidate Projects",""]
    for c in candidates:
        license_label=c["license"].get("spdx") or "Unknown"; lines.append(f"- **{c['name']}** — {c['repo_url']} — {c['health_status']} — License: {license_label} — Decision: {c['reuse_decision']}")
    lines += ["","## Shortlist / Focus Candidates",""] + ([f"- {c['name']}: {c['decision_reason']}" for c in adopted] or ["- None"])
    lines += ["","## Best Components",""]
    for c in adopted: lines.append(f"- **{c['name']}**: {', '.join(c.get('capabilities') or []) or 'Not specified'}")
    lines += ["","## Rejected / Not Selected",""] + ([f"- {c['name']}: {c['decision_reason']}" for c in rejected] or ["- None"])
    lines += ["","## License & Reuse",""]
    for c in candidates:
        lic=c["license"]; obligations="; ".join(lic.get("obligations") or []) or "None recorded / unknown"; lines.append(f"- **{c['name']}**: status={lic.get('status')}; license={lic.get('spdx') or 'Unknown'}; obligations={obligations}")
    lines += ["","## Capability Matrix Summary",""]
    for row in packet["capability_matrix"]: lines.append(f"- **{row['capability']}** → {row['best_source']} / {row['decision']}: {row['rationale']}")
    lines += ["","## Recommended Stack",""]+[f"- {x}" for x in packet["recommended_stack"]]+["","## FLEXFANG Differentiation",""]+[f"- {x}" for x in packet["flexfang_differentiation"]]
    return "\n".join(lines)+"\n"
def render_matrix(packet):
    output=io.StringIO(newline=""); writer=csv.DictWriter(output,fieldnames=["capability","best_source","decision","rationale"]); writer.writeheader()
    for row in packet["capability_matrix"]: writer.writerow({key:row[key] for key in writer.fieldnames})
    return output.getvalue()
def render_architecture(packet):
    lines=["# FLEXFANG CRM ARCHITECTURE DECISION","",f"**As of:** {packet['as_of']}",""]
    for section in ARCH_SECTIONS: lines += [f"## {section}","",packet["architecture_decision"][section].strip(),""]
    return "\n".join(lines)
def build_outputs(packet,output_dir:Path):
    validate_packet(packet); output_dir.mkdir(parents=True,exist_ok=True)
    paths=[output_dir/"FLEXFANG_CRM_OPEN_SOURCE_RESEARCH.md",output_dir/"FLEXFANG_CRM_CAPABILITY_MATRIX.csv",output_dir/"FLEXFANG_CRM_ARCHITECTURE_DECISION.md",output_dir/"FLEXFANG_CRM_RESEARCH_PACKET.json"]
    payloads=[render_research(packet),render_matrix(packet),render_architecture(packet),json.dumps(packet,ensure_ascii=False,indent=2)+"\n"]
    for path,payload in zip(paths,payloads,strict=True): _atomic_write(path,payload)
    return paths
def main():
    parser=argparse.ArgumentParser(); parser.add_argument("--input",required=True,type=Path); parser.add_argument("--output-dir",required=True,type=Path); args=parser.parse_args()
    try: packet=json.loads(args.input.read_text(encoding="utf-8")); paths=build_outputs(packet,args.output_dir)
    except (OSError,json.JSONDecodeError,PacketValidationError) as exc: print(f"NOT READY: {exc}",file=sys.stderr); return 2
    for path in paths: print(path)
    return 0
if __name__=="__main__": raise SystemExit(main())
