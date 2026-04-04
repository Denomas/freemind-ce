# FreeMind CE — Kaldığımız Yerden Devam Ederken Yapılacaklar

**Son güncelleme:** 2026-03-25
**Branch:** `main` (temiz, güncel)
**Son release:** v1.3.2 (17 Mart 2026)
**Toplam test:** 824 headless + 489 GUI = 1,313 test, 0 fail

---

## HEMEN YAPILACAKLAR (Open PR'lar)

### 1. PR #30 — `chore(main): release 1.3.3`
- **Ne:** release-please otomatik PR — CodeQL fix'ini release'e çeviriyor
- **CI:** Geçmiş
- **Risk:** Sıfır — sadece version bump + CHANGELOG
- **Aksiyon:** Merge et (squash)
- **URL:** https://github.com/Denomas/freemind-ce/pull/30

### 2. PR #32 — `ci: bump the github-actions group with 2 updates`
- **Ne:** dorny/test-reporter 2.6→3.0 + github/codeql-action güncelleme
- **CI:** Kontrol et — test-reporter v3 Node.js 24 gerektiriyor
- **Risk:** Düşük — CI-only değişiklik
- **Aksiyon:** CI geçiyorsa merge et
- **URL:** https://github.com/Denomas/freemind-ce/pull/32

### 3. PR #33 — `deps: groovy 2.1.8 → 3.0.25`
- **Ne:** Groovy MAJOR versiyon atlaması (12 yıllık 2.x → modern 3.x)
- **CI:** ALL PASS (tüm 48 job geçmiş)
- **Risk:** Orta — major bump ama CI geçiyor. Groovy sadece scripting plugin'inde
- **Aksiyon:** Merge et, sonra `make run` ile script execution test et
- **Dikkat:** merge-release-safety.md'deki "major dependency" protokolünü izle
- **URL:** https://github.com/Denomas/freemind-ce/pull/33

### 4. PR #34 — `deps: jgoodies-forms 1.8.0 → 1.9.0`
- **Ne:** JGoodies Forms minor version ama API breaking change
- **CI:** ❌ FAIL — 3 compile error:
  - `freemind/preferences/layout/OptionPanel.java:207`
  - `freemind/modes/mindmapmode/dialogs/StylePatternFrame.java:273`
  - `plugins/collaboration/socket/FormDialog.java:93`
- **Risk:** Yüksek — kod değişikliği gerekli, GUI form layout'larını etkiler
- **Aksiyon seçenekleri:**
  - A: Kapat (ignore this minor version) — JGoodies 1.8.0 çalışıyor
  - B: 3 dosyayı düzelt, test et, merge et — Serena ile hangi symbol'ler değişmiş analiz et
- **URL:** https://github.com/Denomas/freemind-ce/pull/34

### 5. Stale Branch Temizliği
Merge edilmiş PR'ların branch'leri hala duruyor — silinmeli:
```bash
git branch -d ci/path-filtering
git branch -d dependabot/gradle/org.apache.lucene-lucene-analysis-common-10.4.0
git branch -d docs/merge-release-safety-protocol
git branch -d feat/test-coverage-expansion-and-quality-gates
git branch -d fix/codeql-scan-and-workflow-health
git branch -d fix/v1.3.1-packaging-fixes-and-polish
# Remote'ları da sil:
git push origin --delete ci/path-filtering docs/merge-release-safety-protocol feat/test-coverage-expansion-and-quality-gates fix/codeql-scan-and-workflow-health fix/v1.3.1-packaging-fixes-and-polish
# NOT: dependabot branch'ini silme — dependabot kendi yönetir
```

---

## MEVCUT PROJE DURUMU

### Başarıyla Tamamlananlar
- ✅ Quality Sweep: 17 commit (whitespace, javadoc, suppresswarnings, correctness, i18n, boxing, deprecated, threading)
- ✅ Test Coverage Expansion: Phase 0-5 tamamlandı (263 → 824 headless test)
- ✅ TEA Test Review: 86/100 (Grade B — Good)
- ✅ BUG-1 fix: Flag emoji Unicode (XMLElement.writeEncoded + resolveEntity)
- ✅ BUG-2 fix: getXml() Writer close kaldırıldı
- ✅ BUG-4 fix: NodeAdapter.children → Collections.synchronizedList()
- ✅ BUG-5 fix: XML entity expansion → FEATURE_SECURE_PROCESSING (10 dosya)
- ✅ CodeQL security scan fix (clean + --no-build-cache)
- ✅ Gradle testPerformance, testChaos tasks
- ✅ JaCoCo minimum 20% threshold
- ✅ Makefile test-performance, test-chaos targets
- ✅ CONTRIBUTING.md §7: Scheduled Workflow Health (MANDATORY)
- ✅ Release Checklist: scheduled workflow doğrulaması eklendi
- ✅ GitHub Ruleset: required_approving_review_count → 0 (solo maintainer merge)
- ✅ PR #26 merged (test expansion + CI gates)
- ✅ PR #28 merged (docs: merge safety protocol)
- ✅ PR #29 merged (CodeQL fix + workflow health)

### Coverage Durumu
- Instruction: %21 (başlangıç %19)
- Branch: %20 (başlangıç %17)
- Methods covered: 1,935 / 7,180
- Key gains: filter.condition %0→%65, controller.filter %2→%10, actions.xml %52→%61

### Test Dosyaları (19 yeni dosya, +8,599 satır)
```
freemind/tests/freemind/testutil/MindMapGenerator.java — Test data factory (builder pattern)
freemind/tests/freemind/testutil/MindMapGeneratorTest.java — 12 test
freemind/tests/freemind/filter/FilterConditionTest.java — 92 test
freemind/tests/freemind/filter/FilterIntegrationTest.java — 46 test
freemind/tests/freemind/modes/NodeAdapterTest.java — 89 test
freemind/tests/freemind/io/MapRoundTripTest.java — 24 test
freemind/tests/freemind/io/LargeFilePerformanceTest.java — 9 test
freemind/tests/freemind/io/ChaosXmlDefenseTest.java — 20 test
freemind/tests/freemind/io/MapLifecycleTest.java — 16 test
freemind/tests/freemind/actions/ActionFrameworkTest.java — 70 test
freemind/tests/freemind/plugins/HookFrameworkTest.java — 27 test
freemind/tests/freemind/plugins/PluginLogicTest.java — 40 test
freemind/tests/freemind/plugins/CollaborationStructuralTest.java — 28 test
freemind/tests/freemind/export/XsltExportTest.java — 27 test
freemind/tests/freemind/export/ContentPreservationExportTest.java — 16 test
freemind/tests/freemind/ConcurrencyTest.java — 15 test
freemind/tests/freemind/StructuralQualityTest.java — 12 test
freemind/tests/freemind/BackwardCompatTest.java — 10 test
+ Mevcut 5 quality sweep test dosyası (CodeQualityTest, ThreadSafetyTest, vb.)
```

---

## ERTELENMİŞ İŞLER

### BUG-3: SimplyHTML Note Reformat (ayrı PR gerekli)
- **Sorun:** `setNoteText()` çağrıldığında SimplyHTML'in `toXhtml()` metodu HTML'i reformat eder (head/body ekler, script'i comment'e alır)
- **Etki:** Düşük — içerik korunuyor ama HTML yapısı değişiyor
- **Konum:** `freemind/freemind/modes/NodeAdapter.java` → `setNoteText()` → `HtmlTools.toXhtml()`
- **Fix yaklaşımı:** Raw HTML'i normalizasyon olmadan sakla veya davranışı belgele

### Phase 6: Yeni GUI Testleri
- Mevcut 489 GUI test çalışıyor (24 dosya)
- Ek GUI testleri yazılabilir ama mevcut kapsam yeterli
- macOS'ta çalıştırılabilir (WindowServer aktif), CI'da Xvfb ile

---

## TEMEL PRENSİPLER (unutulmamalı)

### User Content is Sacred
- Kullanıcı verisi ASLA filtrelenmez, engellenmez veya sanitize edilmez
- `<script>`, `file:///etc/passwd`, SQL injection string'leri meşru kullanıcı içeriğidir
- Güvenlik testleri ARACI korur (XML DoS, OOM), kullanıcı içeriğini değil
- Testler içerik KORUNUMUNU doğrular, içerik ENGELLENMEYI değil

### Scheduled Workflow Health (CONTRIBUTING.md §7)
PR CI geçse bile scheduled workflow'lar kırık olabilir. Release öncesi kontrol:
```bash
for wf in security-scan.yml scorecard.yml fuzz.yml stale.yml build.yml release-please.yml; do
  result=$(gh run list --workflow=$wf --limit 1 --json conclusion --jq '.[0].conclusion // "no runs"' 2>/dev/null)
  echo "$wf: $result"
done
```

### Merge Protokolü
- `required_approving_review_count: 0` — solo maintainer merge yapabilir
- CI `CI` aggregator check zorunlu
- Squash merge only
- Major dependency update → merge-release-safety.md protokolünü izle

---

## WORKFLOW URL'LERİ

| Sayfa | URL |
|-------|-----|
| Actions Ana | https://github.com/Denomas/freemind-ce/actions |
| Build | https://github.com/Denomas/freemind-ce/actions/workflows/build.yml |
| Security Scan | https://github.com/Denomas/freemind-ce/actions/workflows/security-scan.yml |
| Scorecard | https://github.com/Denomas/freemind-ce/actions/workflows/scorecard.yml |
| Release | https://github.com/Denomas/freemind-ce/actions/workflows/release.yml |
| Fuzz | https://github.com/Denomas/freemind-ce/actions/workflows/fuzz.yml |
| Code Scanning | https://github.com/Denomas/freemind-ce/security/code-scanning |
| Dependabot | https://github.com/Denomas/freemind-ce/security/dependabot |
| OpenSSF Scorecard | https://securityscorecards.dev/viewer/?uri=github.com/Denomas/freemind-ce |
| Releases | https://github.com/Denomas/freemind-ce/releases |
| Latest Release | https://github.com/Denomas/freemind-ce/releases/tag/v1.3.2 |

---

## KEŞFEDİLEN BUGLAR (durum)

| Bug | Severity | Durum | Konum |
|-----|----------|-------|-------|
| BUG-1: Flag emoji Unicode | Medium | ✅ FIXED | XMLElement.writeEncoded + resolveEntity |
| BUG-2: getXml() Writer close | Low | ✅ FIXED | MindMapMapModel.getXml() |
| BUG-3: SimplyHTML note reformat | Low | ⏩ DEFERRED | NodeAdapter.setNoteText → HtmlTools.toXhtml |
| BUG-4: LinkedList thread safety | High | ✅ FIXED | NodeAdapter + 3 subclass (synchronizedList) |
| BUG-5: XML entity expansion DoS | High | ✅ FIXED | 10 dosyada FEATURE_SECURE_PROCESSING |

---

## v1.2.0+ ROADMAP

| Versiyon | Özellik | Durum |
|----------|---------|-------|
| v1.2.0 | Multi-platform dist (MSI, RPM, AppImage, portable ZIP) | v1.3.2'de tamamlandı |
| v1.3.0 | LaTeX modernization (HotEqn→JLaTeXMath), JSON/YAML export | Planlanmadı |
| v1.4.0 | SVG icons, FlatLaf theme refinement | Planlanmadı |
| v1.5.0 | i18n gaps, accessibility, performance | Planlanmadı |

---

## HIZLI KOMUTLAR

```bash
# Build + test
make build                    # Compile + headless tests
make test-gui                 # GUI tests (macOS'ta çalışır)
make coverage                 # JaCoCo coverage report
make test-performance         # Performance tests (@Tag("performance"))
make test-chaos               # Chaos tests (@Tag("chaos"))

# CI kontrol
gh pr list --state open       # Açık PR'lar
gh run list --limit 5         # Son CI run'ları
gh pr checks <PR_NUMBER>      # PR CI durumu
gh workflow run security-scan.yml  # Manuel security scan tetikle

# Branch temizliği
git branch -d <branch>        # Local branch sil
git push origin --delete <branch>  # Remote branch sil
```

---

## AUTOMATION SUMMARY
Detaylı test planı, elicitation bulguları ve chaos monkey senaryoları:
`_bmad-output/test-artifacts/automation-summary.md` (gitignored — sadece local)

## TEA TEST REVIEW
Test kalite raporu (86/100):
`_bmad-output/test-artifacts/test-review.md` (gitignored — sadece local)
