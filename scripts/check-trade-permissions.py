from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
CONTROLLER_DIR = ROOT / "yudao-module-crm/src/main/java/cn/iocoder/yudao/module/crm/controller/admin/trade"
MIGRATION = ROOT / "sql/mysql/flexfang-trade-permissions-v0.1.sql"

pattern = re.compile(r"hasPermission\('([^']+)'\)")
permissions = set()
for java_file in CONTROLLER_DIR.glob("*.java"):
    permissions.update(pattern.findall(java_file.read_text(encoding="utf-8")))

trade_permissions = sorted(p for p in permissions if p.startswith("crm:trade-"))
sql = MIGRATION.read_text(encoding="utf-8")
missing = [p for p in trade_permissions if p not in sql]
if missing:
    raise SystemExit("Missing permission migration entries: " + ", ".join(missing))

if len(trade_permissions) != len(set(trade_permissions)):
    raise SystemExit("Duplicate permission contract detected")

expected = {
    "crm:trade-profile:query",
    "crm:trade-profile:update",
    "crm:trade-rfq:query",
    "crm:trade-rfq:create",
    "crm:trade-rfq:update",
    "crm:trade-rfq:delete",
    "crm:trade-sample:query",
    "crm:trade-sample:create",
    "crm:trade-sample:update",
    "crm:trade-sample:delete",
}
if set(trade_permissions) != expected:
    raise SystemExit(f"Permission contract drift. controllers={trade_permissions}, expected={sorted(expected)}")

print(f"PASS: {len(trade_permissions)} Trade CRM permissions covered by migration")
