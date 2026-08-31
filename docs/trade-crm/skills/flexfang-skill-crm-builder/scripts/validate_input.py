#!/usr/bin/env python3
"""Validate and normalize FLEXFANG CRM Builder input using only the stdlib."""
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Any

DEFAULTS = {
    "target_markets": [],
    "priority_capabilities": [],
    "candidate_target": 20,
    "shortlist_target": 10,
    "deep_dive_target": 5,
    "language": "zh-CN",
    "reuse_policy": "adopt_adapt_learn_build",
    "special_constraints": [],
}
ALLOWED_KEYS = {"business_context", "crm_goal", *DEFAULTS.keys()}

class InputValidationError(ValueError):
    pass

def _require_string(data: dict[str, Any], key: str, min_len: int) -> None:
    value = data.get(key)
    if not isinstance(value, str) or len(value.strip()) < min_len:
        raise InputValidationError(f"{key} must be a string with at least {min_len} characters")

def _string_list(value: Any, key: str) -> list[str]:
    if not isinstance(value, list) or any(not isinstance(v, str) or not v.strip() for v in value):
        raise InputValidationError(f"{key} must be an array of non-empty strings")
    seen: set[str] = set(); result: list[str] = []
    for raw in value:
        item = raw.strip()
        if item not in seen:
            seen.add(item); result.append(item)
    return result

def _bounded_int(value: Any, key: str, lower: int, upper: int) -> int:
    if isinstance(value, bool) or not isinstance(value, int): raise InputValidationError(f"{key} must be an integer")
    if value < lower or value > upper: raise InputValidationError(f"{key} must be between {lower} and {upper}")
    return value

def normalize_input(data: dict[str, Any]) -> dict[str, Any]:
    if not isinstance(data, dict): raise InputValidationError("input must be a JSON object")
    unknown = sorted(set(data) - ALLOWED_KEYS)
    if unknown: raise InputValidationError(f"unknown input fields: {', '.join(unknown)}")
    normalized = {**DEFAULTS, **data}
    _require_string(normalized, "business_context", 10); _require_string(normalized, "crm_goal", 3)
    normalized["business_context"] = normalized["business_context"].strip(); normalized["crm_goal"] = normalized["crm_goal"].strip()
    if not isinstance(normalized["language"], str): raise InputValidationError("language must be a string")
    normalized["language"] = normalized["language"].strip()
    if len(normalized["language"]) < 2: raise InputValidationError("language must have at least 2 characters")
    if not isinstance(normalized["reuse_policy"], str) or normalized["reuse_policy"] != "adopt_adapt_learn_build": raise InputValidationError("reuse_policy must be 'adopt_adapt_learn_build'")
    for key in ("target_markets", "priority_capabilities", "special_constraints"): normalized[key] = _string_list(normalized[key], key)
    normalized["candidate_target"] = _bounded_int(normalized["candidate_target"], "candidate_target", 15, 30)
    normalized["shortlist_target"] = _bounded_int(normalized["shortlist_target"], "shortlist_target", 5, 12)
    normalized["deep_dive_target"] = _bounded_int(normalized["deep_dive_target"], "deep_dive_target", 3, 8)
    if not normalized["deep_dive_target"] <= normalized["shortlist_target"] <= normalized["candidate_target"]: raise InputValidationError("must satisfy deep_dive_target <= shortlist_target <= candidate_target")
    return normalized

def main() -> int:
    parser = argparse.ArgumentParser(); parser.add_argument("--input", required=True, type=Path); parser.add_argument("--output", type=Path); args = parser.parse_args()
    try:
        data = json.loads(args.input.read_text(encoding="utf-8")); normalized = normalize_input(data)
    except (OSError, json.JSONDecodeError, InputValidationError) as exc:
        print(f"INVALID: {exc}", file=sys.stderr); return 2
    rendered = json.dumps(normalized, ensure_ascii=False, indent=2) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True); args.output.write_text(rendered, encoding="utf-8")
    else: sys.stdout.write(rendered)
    return 0

if __name__ == "__main__": raise SystemExit(main())
