# FreeMind CE — Developer Guide

> **RULE 0 — READ BEFORE ANY WORK:** You MUST read ALL documents listed in the Mandatory Reading Table below before starting any task. Skipping any document is a disqualifying violation. Every rule in those documents must be applied before any commit, push, or merge.

## Mandatory Reading Table

| Document | What It Contains | When to Read |
|----------|-----------------|-------------|
| **[CONTRIBUTING.md](CONTRIBUTING.md)** | **Single source of truth for ALL rules:** project philosophy, commit conventions, Serena workflow, CI/CD SOP (48-job matrix, path filtering, GUI test requirements, scheduled workflow health checks, CI output verification), merge safety protocol, dependency update protocol, release checklist, code style, testing | **Always — before any work, without exception** |
| **[docs/serena-guide.md](docs/serena-guide.md)** | Serena MCP tool reference (18 tools), workflow diagrams, decision trees, examples, anti-patterns | Before any code analysis or editing |
| **[docs/development-guide.md](docs/development-guide.md)** | Build commands, test execution, debug mode, packaging, JVM arguments, module structure | Before writing or running code |
| **[docs/architecture.md](docs/architecture.md)** | MVC pattern, mode-based architecture (Browse/MindMap/File), action framework, plugin system, hook architecture | Before any design decision |
| **[docs/component-inventory.md](docs/component-inventory.md)** | UI components, plugin registry, source file locations | Before finding or modifying specific code |
| **[docs/source-tree-analysis.md](docs/source-tree-analysis.md)** | Directory structure, file locations, module organization | Before navigating the codebase |
| **[docs/merge-release-safety.md](docs/merge-release-safety.md)** | Merge protocols, branch protection, dependency update rules (patch/minor/major), release checklist | Before any merge, review, or release |
| **[docs/contributor-workflows.md](docs/contributor-workflows.md)** | Workflow diagrams: CI pipeline, PR lifecycle, release gating, dependency updates, blocked actions, hotfix flow, conflict resolution, bot lifecycle, security incidents | Before any PR, merge, or release operation |

> **SINGLE SOURCE OF TRUTH RULE:** Every project rule, workflow, and convention exists in exactly ONE place — CONTRIBUTING.md and the docs/ directory. This file (CLAUDE.md) only contains links and quick references. Never duplicate content. If a rule is referenced here, follow the link to read the authoritative version.

---

## Quick Start

```bash
make build    # Build the project (compile + test)
make run      # Run FreeMind CE
make help     # Show all available make targets
```

> **Note:** `make` wraps Gradle with correct `JAVA_HOME` and `--no-configuration-cache` flags automatically.
> See raw Gradle commands in `Makefile` if needed.

- Entry point: `freemind.main.FreeMindStarter`
- Version: Managed by release-please. **Never edit manually.** → [CONTRIBUTING.md — Commit Best Practices](CONTRIBUTING.md#commit-best-practices)

## Repository Layout

```
freemind-ce/
├── CLAUDE.md                 # This file — index and quick reference
├── CONTRIBUTING.md           # ← SINGLE SOURCE OF TRUTH for ALL rules
├── Makefile                  # Development shortcuts
├── .serena/project.yml       # Serena LSP config
├── docs/
│   ├── architecture.md       # MVC, modes, action framework
│   ├── component-inventory.md # UI components, plugins
│   ├── source-tree-analysis.md # Directory structure
│   ├── development-guide.md  # Build, test, deploy
│   ├── serena-guide.md       # Serena tool reference (18 tools)
│   ├── merge-release-safety.md # Merge protocols, release checklist
│   └── contributor-workflows.md # Workflow diagrams
├── build.gradle.kts          # Root build config
├── freemind/                  # Main module
│   ├── freemind/              # Core source
│   ├── accessories/           # XSLT exports, accessory plugins
│   ├── plugins/               # Plugin modules
│   ├── generated-src/         # JAXB generated (DO NOT EDIT)
│   └── tests/                 # JUnit tests
└── _bmad-output/              # Technical specs
```

## Key Source Locations

| What | Where |
|------|-------|
| Main app class | `freemind/freemind/main/FreeMind.java` |
| Controller hub | `freemind/freemind/controller/Controller.java` |
| Primary mode | `freemind/freemind/modes/mindmapmode/MindMapController.java` |
| Map view | `freemind/freemind/view/mindmapview/MapView.java` |
| Node model | `freemind/freemind/modes/MindMapNode.java` |

## Plugin Development

→ [CONTRIBUTING.md — Plugin Development](CONTRIBUTING.md#plugin-development)

Active plugins: svg, script, map, search, help, contextgraph, collaboration/socket

## Critical Rules (Summary)

> **Full rules → [CONTRIBUTING.md — Critical Rules](CONTRIBUTING.md#critical-rules)**

1. **Never ignore `*.jar` in .gitignore** — ~90 tracked local JARs
2. **Never modify `generated-src/`** — regenerate with `make jaxb`
3. **Never commit `auto.properties`** — runtime-generated
4. **Test before commit** — `make build` must pass
5. **No @SuppressWarnings** — fix root causes
6. **Preserve backward compatibility — ABSOLUTE RULE** — every `.mm` file must open
7. **Verify with Serena before commit** — `find_referencing_symbols`
8. **Serena is MANDATORY** — every agent, every subagent
9. **Check `.gitignore` before CI paths** — never reference gitignored paths
10. **Never bypass merge controls** — no `--admin`, no force merge
11. **Dependency updates require manual review** → [docs/merge-release-safety.md](docs/merge-release-safety.md#dependency-update-protocol)
12. **Never auto-merge without maintainer instruction**

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
make package        # Native package for current OS
make package-mac    # macOS (.dmg)
make package-win    # Windows (.exe)
make package-linux  # Linux (.deb)
make dist-zip       # Distribution ZIP
make install-dist   # Local distribution layout
make info           # Show detected Java and system info
make help           # Show all targets
```

## Git & Release

→ [CONTRIBUTING.md — Git & Release](CONTRIBUTING.md#git--release)

- **Remotes:** `origin` → Denomas/freemind-ce (GitHub), `upstream` → SourceForge
- **Branch:** `main` — trunk-based, all changes via PR (no direct push)
- **Release:** Tag `v*.*.*` → GitHub Actions auto-builds DMG/EXE/DEB
- **CI:** 6 OS × 4 Java = 48 checks, gated by single `CI` aggregator

## Subagent Rules

→ [CONTRIBUTING.md — Subagent Rules](CONTRIBUTING.md#subagent-rules)

**Summary:**
- Every subagent MUST read CONTRIBUTING.md, serena-guide.md, development-guide.md
- Every subagent MUST use Serena MCP tools (not bash grep/find)
- Every subagent MUST verify backward compatibility
- Every subagent MUST verify with `find_referencing_symbols` + `make build`
- After subagent completes: verify `git status`, `make build`, file existence

## Architecture (Summary)

→ [docs/architecture.md](docs/architecture.md)

- **Pattern:** MVC + Mode-based (Browse/MindMap/File) + Hook/Plugin
- **XML Binding:** JAXB 2.3.1/2.3.9
- **L&F:** FlatLaf 3.7

## Serena Code Intelligence

→ [docs/serena-guide.md](docs/serena-guide.md) — 18 tools, workflow diagrams, decision trees

**Mandatory workflow:**
```
1. get_symbols_overview(file)     → understand structure
2. find_symbol(name, body=True)   → read specific code
3. find_referencing_symbols(name) → verify all references intact
4. make build                     → compile + test
```
