from __future__ import annotations
import csv, importlib.util, io, json, tempfile, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def load_module(name,path):
    spec=importlib.util.spec_from_file_location(name,path); module=importlib.util.module_from_spec(spec); assert spec and spec.loader; spec.loader.exec_module(module); return module
validate_input=load_module('validate_input',ROOT/'scripts'/'validate_input.py'); build_outputs=load_module('build_outputs',ROOT/'scripts'/'build_outputs.py')
class InputValidationTests(unittest.TestCase):
    def test_valid_input_applies_defaults(self):
        raw=json.loads((ROOT/'tests/fixtures/valid_input.json').read_text(encoding='utf-8')); normalized=validate_input.normalize_input(raw); self.assertEqual(normalized['candidate_target'],20); self.assertEqual(normalized['shortlist_target'],10); self.assertEqual(normalized['deep_dive_target'],5); self.assertEqual(normalized['language'],'zh-CN')
    def test_missing_required_field_fails(self):
        with self.assertRaises(validate_input.InputValidationError): validate_input.normalize_input({'crm_goal':'Build CRM'})
    def test_unknown_field_fails_closed(self):
        with self.assertRaises(validate_input.InputValidationError): validate_input.normalize_input({'business_context':'Long enough business context','crm_goal':'CRM goal','magic_override':True})
    def test_count_order_is_enforced(self):
        with self.assertRaises(validate_input.InputValidationError): validate_input.normalize_input({'business_context':'Long enough business context','crm_goal':'CRM goal','candidate_target':15,'shortlist_target':5,'deep_dive_target':8})
class EvidenceAndOutputTests(unittest.TestCase):
    def load_packet(self): return json.loads((ROOT/'tests/fixtures/valid_packet.json').read_text(encoding='utf-8'))
    def test_valid_packet_passes(self): build_outputs.validate_packet(self.load_packet())
    def test_candidate_without_evidence_fails(self):
        packet=self.load_packet(); packet['candidates'][0]['evidence']=[]
        with self.assertRaises(build_outputs.PacketValidationError): build_outputs.validate_packet(packet)
    def test_unknown_license_blocks_adopt(self):
        packet=self.load_packet(); packet['candidates'][1]['reuse_decision']='ADOPT'
        with self.assertRaises(build_outputs.PacketValidationError): build_outputs.validate_packet(packet)
    def test_duplicate_repo_is_rejected(self):
        packet=self.load_packet(); packet['candidates'][1]['repo_url']=packet['candidates'][0]['repo_url']
        with self.assertRaises(build_outputs.PacketValidationError): build_outputs.validate_packet(packet)
    def test_missing_architecture_section_fails(self):
        packet=self.load_packet(); del packet['architecture_decision']['API']
        with self.assertRaises(build_outputs.PacketValidationError): build_outputs.validate_packet(packet)
    def test_outputs_are_real_and_readable(self):
        packet=self.load_packet()
        with tempfile.TemporaryDirectory() as tmp:
            paths=build_outputs.build_outputs(packet,Path(tmp)); self.assertEqual(len(paths),4)
            for path in paths: self.assertTrue(path.is_file()); self.assertGreater(path.stat().st_size,20)
            report=(Path(tmp)/'FLEXFANG_CRM_OPEN_SOURCE_RESEARCH.md').read_text(encoding='utf-8'); self.assertIn('## Candidate Projects',report); self.assertIn('## License & Reuse',report)
            architecture=(Path(tmp)/'FLEXFANG_CRM_ARCHITECTURE_DECISION.md').read_text(encoding='utf-8')
            for section in build_outputs.ARCH_SECTIONS: self.assertIn(f'## {section}',architecture)
            parsed=json.loads((Path(tmp)/'FLEXFANG_CRM_RESEARCH_PACKET.json').read_text(encoding='utf-8')); self.assertEqual(parsed['crm_goal'],packet['crm_goal'])
            rows=list(csv.DictReader(io.StringIO((Path(tmp)/'FLEXFANG_CRM_CAPABILITY_MATRIX.csv').read_text(encoding='utf-8')))); self.assertEqual(len(rows),len(packet['capability_matrix'])); self.assertEqual(set(rows[0]),{'capability','best_source','decision','rationale'})
    def test_invalid_packet_writes_nothing(self):
        packet=self.load_packet(); packet['candidates'][0]['license']['status']='unknown'; packet['candidates'][0]['license']['source_url']=None
        with tempfile.TemporaryDirectory() as tmp:
            with self.assertRaises(build_outputs.PacketValidationError): build_outputs.build_outputs(packet,Path(tmp))
            self.assertEqual(list(Path(tmp).iterdir()),[])
class EvalDefinitionTests(unittest.TestCase):
    def test_evals_cover_positive_negative_and_gates(self):
        cases=json.loads((ROOT/'evals/evals.json').read_text(encoding='utf-8'))['cases']; self.assertGreaterEqual(len(cases),8); self.assertTrue(any(c['expected'].get('should_trigger') is True for c in cases)); self.assertTrue(any(c['expected'].get('should_trigger') is False for c in cases)); ids={c['id'] for c in cases}; self.assertIn('github_unavailable',ids); self.assertIn('unknown_license_blocks_reuse',ids); self.assertIn('decision_gate',ids); self.assertIn('formal_output_gate',ids)
if __name__=='__main__': unittest.main()
