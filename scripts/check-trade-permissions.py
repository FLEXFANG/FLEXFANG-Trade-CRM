from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CONTROLLER_DIR = ROOT / "yudao-module-crm/src/main/java/cn/iocoder/yudao/module/crm/controller/admin/trade"
SQL_DIR = ROOT / "sql/mysql"
pattern = re.compile(r"hasPermission\('([^']+)'\)")
permissions = set()
for java_file in CONTROLLER_DIR.glob("*.java"):
    permissions.update(pattern.findall(java_file.read_text(encoding="utf-8")))
trade_permissions = sorted(p for p in permissions if p.startswith("crm:trade-"))
permission_sql = "\n".join(p.read_text(encoding="utf-8") for p in SQL_DIR.glob("flexfang-trade*permissions*.sql"))
missing = [p for p in trade_permissions if p not in permission_sql]
if missing:
    raise SystemExit("Missing permission migration entries: " + ", ".join(missing))
expected = {
    "crm:trade-profile:query", "crm:trade-profile:update",
    "crm:trade-rfq:query", "crm:trade-rfq:create", "crm:trade-rfq:update", "crm:trade-rfq:delete",
    "crm:trade-sample:query", "crm:trade-sample:create", "crm:trade-sample:update", "crm:trade-sample:delete",
    "crm:trade-quotation:query", "crm:trade-quotation:create", "crm:trade-quotation:update",
    "crm:trade-quotation:delete", "crm:trade-quotation:status", "crm:trade-quotation:revise",
}
if set(trade_permissions) != expected:
    raise SystemExit(f"Permission contract drift. controllers={trade_permissions}, expected={sorted(expected)}")
print(f"PASS: {len(trade_permissions)} Trade CRM permissions covered by migrations")
