# FreeMind CE — Developer Guide

## Quick Start

```bash
make build    # Build the project (compile + test)
make run      # Run FreeMind CE
make help     # Show all available make targets
```

> **Note:** `make` wraps Gradle with correct `JAVA_HOME` and `--no-configuration-cache` flags automatically.
> See raw Gradle commands in `Makefile` if needed.

- Entry point: `freemind.main.FreeMindStarter`
- Version: Managed automatically by release-please. **Never edit version numbers manually.**
  - Source of truth: `.release-please-manifest.json`
  - Auto-synced: `build.gradle.kts` (via `x-release-please-version` annotation)

## Serena Code Intelligence (MANDATORY)

> **Serena usage is MANDATORY — not optional.** Every code analysis must start with Serena tools. Every subagent must use Serena. No exceptions.

This project uses [Serena](https://github.com/oraios/serena) for LSP-powered semantic code analysis via MCP.

- **Config:** `.serena/project.yml` (versioned)
- **Setup:** `claude mcp add serena -- uvx --from git+https://github.com/oraios/serena serena start-mcp-server --context=claude-code --project-from-cwd`
- **Index:** `uvx --from git+https://github.com/oraios/serena serena project index .`
- **Pre-commit:** Always use `find_referencing_symbols` to verify impact of changes before committing
- **Subagents:** When spawning subagents, ALWAYS include "Use Serena tools for code analysis" in the task description

### Quick Reference (18 Tools)

| Category | Tools | Purpose |
|----------|-------|---------|
| **Exploration** | `get_symbols_overview`, `find_symbol`, `find_referencing_symbols` | Understand code structure, find symbols, trace references |
| **Search** | `search_for_pattern`, `find_file`, `list_dir` | Regex search, file discovery, directory listing |
| **Editing** | `replace_symbol_body`, `insert_after_symbol`, `insert_before_symbol`, `rename_symbol` | Symbol-level code modification, codebase-wide rename |
| **Memory** | `write_memory`, `read_memory`, `list_memories`, `edit_memory`, `delete_memory`, `rename_memory` | Persistent project knowledge across sessions |
| **Setup** | `check_onboarding_performed`, `onboarding` | First-time project setup |

### Mandatory Workflow

```
1. get_symbols_overview(file)     → understand structure
2. find_symbol(name, body=True)   → read specific code
3. Make changes
4. find_referencing_symbols(name) → verify all references intact
5. make build                     → compile + test
```

For complete tool reference with parameters, examples, and anti-patterns → [`CONTRIBUTING.md`](CONTRIBUTING.md#complete-serena-tool-reference-18-tools)

## Repository Layout

```
freemind-ce/
├── CLAUDE.md                 # This file — read first
├── Makefile                  # Development shortcuts (make help)
├── .serena/project.yml       # Serena LSP config (versioned)
├── docs/
│   ├── architecture.md       # MVC, modes, action framework → READ FOR DESIGN
│   ├── component-inventory.md # UI components, plugins → READ FOR FINDING CODE
│   ├── source-tree-analysis.md # Directory guide → READ FOR NAVIGATION
│   └── development-guide.md  # Build, test, deploy → READ FOR WORKFLOWS
├── build.gradle.kts          # Root build config (version here)
├── settings.gradle.kts       # Module registration
├── freemind/                  # Main module
│   ├── freemind/              # Core source (package: freemind.*)
│   ├── accessories/           # XSLT exports, accessory plugins
│   ├── plugins/               # Plugin modules
│   ├── generated-src/         # JAXB generated (DO NOT EDIT)
│   ├── tests/                 # JUnit tests
│   ├── build.gradle.kts       # Module build config
│   └── freemind.properties    # Default application properties
└── _bmad-output/              # Technical specs and migration analysis
```

## Git & Release

- **Remotes:** `origin` → Denomas/freemind-ce (GitHub, primary), `upstream` → SourceForge (read-only, original project)
- **Branch:** `main` — trunk-based, all changes via PR (no direct push)
- **Release:** Tag `v*.*.*` on main → GitHub Actions auto-builds DMG/EXE/DEB
- **Pre-commit:** `.pre-commit-config.yaml` — XML validation, Java compilation, whitespace
- **CI Zero-Tolerance:** 6 runners × 4 Java versions (21, 22, 23, 24) = 48 checks, gated by single `CI` aggregator
- **Path filtering:** Doc-only PRs skip the 48-job matrix — `CI` aggregator passes directly (~30s)
  - Detection: pure `git diff` with negated pathspecs (no 3rd-party actions)
  - Doc paths: `**/*.md`, `docs/**`, `LICENSE`, `COPYING`, `.gitattributes`, `.github/ISSUE_TEMPLATE/**`, `.github/PULL_REQUEST_TEMPLATE/**`, `.github/release-notes-template.md`
  - Non-PR events (push to main, `workflow_call`) always run full build
- **Required check:** Single `CI` job in GitHub Ruleset (not 48 individual checks)
- **GUI tests are fully blocking** — no `continue-on-error`, any failure blocks merge/release
- **Every UI change requires GUI tests** with screenshots — see [`CONTRIBUTING.md` SOP](CONTRIBUTING.md#cicd-standard-operating-procedure-sop)

## Architecture (Summary)

- **Pattern:** MVC + Mode-based (Browse/MindMap/File) + Hook/Plugin
- **XML Binding:** JAXB 2.3.1/2.3.9 (schema: `freemind_actions.xsd`)
- **Plugins:** Registered via XML descriptors in `plugins/`, loaded by `ImportWizard`
- **L&F:** FlatLaf 3.7 (light/dark), configured in `freemind.properties`

For full architecture details → [`docs/architecture.md`](docs/architecture.md)

## Key Source Locations

| What | Where |
|------|-------|
| Main app class | `freemind/freemind/main/FreeMind.java` |
| Controller hub | `freemind/freemind/controller/Controller.java` |
| Primary mode | `freemind/freemind/modes/mindmapmode/MindMapController.java` |
| Map view | `freemind/freemind/view/mindmapview/MapView.java` |
| Node model | `freemind/freemind/modes/MindMapNode.java` |
| JAXB tools | `freemind/freemind/common/XmlBindingTools.java` |
| Plugin registry | `freemind/accessories/plugins/` (XML descriptors) |
| Export XSLT | `freemind/accessories/*.xsl` (32 formats) |
| Properties | `freemind/freemind.properties` |
| XSD schema | `freemind/freemind_actions.xsd` |
| JAXB bindings | `freemind/jaxb-bindings.xjb` |

## Plugin Development

- Extend `ExportHook` or `ModeControllerHookAdapter`
- Register via XML in `plugins/` directory
- Add resources to `Resources_en.properties`
- Each plugin has its own `build.gradle.kts` in `plugins/<name>/`

Active plugins: svg, script, map, search, help, contextgraph, collaboration/socket
Not in Gradle: latex, collaboration/database, collaboration/jabber

## Critical Rules

> **Rule 0 — Read [`CONTRIBUTING.md`](CONTRIBUTING.md) first.** It is the single source of truth for ALL project rules, merge protocols, dependency update procedures, and release checklists. Every agent, every subagent, every contributor MUST read it before starting any work. Follow the mandatory reading table at the top of that file.

1. **Never ignore `*.jar` in .gitignore** — project depends on ~90 tracked local JARs in `lib/`
2. **Never modify `generated-src/`** — regenerate with `make jaxb`
3. **Never commit `auto.properties`** — runtime-generated user config
4. **Test before commit** — `make build` must pass, `make run` to verify
5. **No @SuppressWarnings** — fix root causes, don't suppress
6. **Preserve backward compatibility — ABSOLUTE RULE** — every `.mm` file ever created must open correctly. No breaking changes. No `feat!:` commits. See CONTRIBUTING.md Project Philosophy.
7. **Verify with Serena before commit** — use `find_referencing_symbols` to check impact of all changes
8. **Serena is MANDATORY for all agents** — every subagent task must include Serena usage as a requirement; start all analysis with `get_symbols_overview` and `find_symbol`
9. **Always check `.gitignore` before CI paths** — never reference gitignored paths in GitHub Actions workflows (`paths-ignore`, pathspecs, etc.). They don't exist in the CI runner. Always run `grep <path> .gitignore` before adding any path to workflow files.
10. **Never bypass merge controls** — no `--admin`, no force merge, no skipping review. See [`docs/merge-release-safety.md`](docs/merge-release-safety.md)
11. **Dependency updates require manual review** — follow patch/minor/major protocol in [`docs/merge-release-safety.md`](docs/merge-release-safety.md#dependency-update-protocol)
12. **Never auto-merge without explicit maintainer instruction** — every merge decision requires human approval for that specific PR

## Common Tasks

```bash
make build          # Full build (compile + test)
make run            # Run the app
make test           # Run tests only
make coverage       # Tests + JaCoCo coverage report
make debug          # Debug mode (port 5005)
make clean          # Clean build artifacts
make check          # Build + all quality checks
make jaxb           # Regenerate JAXB classes
make javadoc        # Generate API documentation
make package        # Native package for current OS (auto-detect)
make package-mac    # Package for macOS (.dmg)
make package-win    # Package for Windows (.exe)
make package-linux  # Package for Linux (.deb)
make dist-zip       # Distribution ZIP archive
make install-dist   # Local distribution layout
make info           # Show detected Java and system info
make help           # Show all targets
```

## Subagent Rules (CRITICAL — Lessons Learned)

> **WHY SUBAGENTS FAILED BEFORE:** In early security fix attempts, subagents were spawned without proper context. They did NOT read CONTRIBUTING.md, did NOT use Serena MCP, did NOT check if files exist, and did NOT understand backward compatibility rules. This resulted in broken code, missing changes, and wasted time.

### Root Cause Analysis: Why Agents Start Wrong

| # | Failure | Root Cause | Prevention |
|---|---------|-----------|------------|
| 1 | **Did not read CONTRIBUTING.md** | Subagent prompt didn't enforce mandatory reading | MUST include "Read CONTRIBUTING.md first" as rule #1 |
| 2 | **Did not read README.md** | No requirement to understand project philosophy | MUST include project philosophy summary in prompt |
| 3 | **Did not use Serena MCP** | Subagent used bash grep/find instead of Serena | MUST enforce Serena workflow: get_symbols_overview → find_symbol → find_referencing_symbols |
| 4 | **Hallucinated file paths** | Tried to edit `plugins/wsl/src/...` which doesn't exist | MUST verify file exists with Serena `find_file` or `list_dir` before editing |
| 5 | **Did not check backward compatibility** | Would have broken .mm file format | MUST include backward compatibility rules in every subagent prompt |
| 6 | **Did not verify references** | Changed code without checking callers | MUST run `find_referencing_symbols` before committing |
| 7 | **Changes not saved** | Subagent reported fixes but didn't persist | MUST verify with `git status` and `make build` after subagent completes |
| 8 | **Did not read developer docs** | Skipped serena-guide.md, development-guide.md | MUST include doc reading checklist in subagent prompt |

### Mandatory Subagent Rules

1. **DO NOT spawn subagents without first reading this file (CLAUDE.md) and CONTRIBUTING.md yourself**
2. **Every subagent prompt MUST include these exact instructions:**
   ```
   MANDATORY RULES — READ BEFORE ANY WORK:
   
   1. DOCUMENTATION (read these files in order):
      - CONTRIBUTING.md (project rules, philosophy, workflows)
      - docs/serena-guide.md (Serena tool reference — MANDATORY)
      - docs/development-guide.md (build, test, deploy)
   
   2. SERENA WORKFLOW (NO EXCEPTIONS):
      - Step 1: get_symbols_overview(file) → understand structure
      - Step 2: find_symbol(name, include_body=True) → read code
      - Step 3: find_referencing_symbols(name) → understand impact
      - Step 4: search_for_pattern(pattern) → find all occurrences
      - Step 5: find_file(mask) → verify file exists before editing
      - NEVER use bash grep/find/cat for code analysis
   
   3. BACKWARD COMPATIBILITY (ABSOLUTE RULE):
      - Every .mm file ever created must open correctly
      - Never change file format compatibility
      - Never change runtime behavior or UX
      - Never sanitize, filter, or modify user content
   
   4. VERIFICATION (before reporting done):
      - Run find_referencing_symbols to verify references intact
      - Run make build to verify compilation
      - Run git status to verify changes are saved
      - Report exact files changed with line numbers
   
   5. COMMIT MESSAGES:
      - Must be in English (international project)
      - Use conventional commits: fix:, refactor:, ci:, test:, etc.
      - Never use feat!: (breaking changes forbidden)
   ```
3. **Subagents must use Serena MCP tools** — not bash grep/find/cat for code analysis
4. **Subagents must verify file existence** before attempting to edit (many files may not exist in this project)
5. **Subagents must understand backward compatibility** — no breaking changes to .mm file format
6. **Subagents must NOT change runtime behavior or UX** — only fix security/code quality issues
7. **After subagent completes, ALWAYS verify with `git status` and `make build`** — subagents may report fixes that weren't actually saved

### When to Use Subagents

| Task | Use Subagent? | Notes |
|------|--------------|-------|
| Security vulnerability fixes | ✅ Yes | But MUST include all rules above |
| SpotBugs fixes | ✅ Yes | But MUST use Serena tools |
| CI/CD workflow changes | ✅ Yes | But MUST check .gitignore first |
| Test writing | ✅ Yes | But MUST understand existing test patterns |
| Simple file edits | ❌ No | Do it yourself, faster |

### Subagent Verification Checklist

After subagents complete, ALWAYS verify:
- [ ] All changed files actually exist in the project (not hallucinated paths)
- [ ] `make build` passes
- [ ] No runtime behavior changes
- [ ] No backward compatibility breaks
- [ ] Commit messages are in English
- [ ] Serena `find_referencing_symbols` was used before each change
- [ ] `git status` shows the expected changes are actually saved
- [ ] CONTRIBUTING.md and docs were read by the subagent

## Where to Look Next

| If you need... | Read... |
|----------------|---------|
| How the system works | [`docs/architecture.md`](docs/architecture.md) |
| Finding specific code | [`docs/component-inventory.md`](docs/component-inventory.md) |
| Build/test/deploy how-to | [`docs/development-guide.md`](docs/development-guide.md) |
| Directory navigation | [`docs/source-tree-analysis.md`](docs/source-tree-analysis.md) |
| Completed tech spec (v1.1.0) | [`_bmad-output/implementation-artifacts/tech-spec-freemind-ce-full-modernization.md`](_bmad-output/implementation-artifacts/tech-spec-freemind-ce-full-modernization.md) |
| Active tech spec (next) | [`_bmad-output/implementation-artifacts/tech-spec-freemind-ce-next-gen-modernization.md`](_bmad-output/implementation-artifacts/tech-spec-freemind-ce-next-gen-modernization.md) |
| Freeplane reference | `~/Development/Resources-Taken-From-Others/freeplane/` |
