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
