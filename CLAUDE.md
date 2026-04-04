# FreeMind CE — Developer Guide

## YOU KNOW THIS, BUT IT'S DIFFERENT HERE

You already know Java, security, refactoring — good. But this project has specific rules that override standard practices. If you skip this section and rely on your general knowledge, you WILL break things.

| You normally do this | HERE, do this instead | Why |
|---|---|---|
| `grep/rg` on Java files | `search_for_pattern()` (Serena MCP) | Semantic analysis, not text matching |
| `cat File.java` or `Read` whole file | `get_symbols_overview()` then `find_symbol()` | Token-efficient, symbol-level precision |
| `find . -name "*.java"` | `find_file("*.java", ".")` (Serena MCP) | Respects .gitignore, structured results |
| Commit without checking callers | `find_referencing_symbols()` before every commit | Broken callers are invisible without it |
| Sanitize/filter user input | **NEVER** — user content is sacred | Every path, script, SQL the user types is preserved exactly |
| Use `feat!:` for breaking changes | **NEVER** — backward compatibility is absolute | Every `.mm` file ever created must still open |

> `grep/rg` on non-Java files (`.properties`, `.md`, `.yml`, `.xml`) is fine and encouraged.

## First Action — Grounding Checklist

Before ANY work, call `mcp__serena__check_onboarding_performed` then print:

```
GROUNDING:
- Branch: ___  Directory: ___
- Task (one sentence): ___
- Serena onboarding: yes/no
- First Serena call: get_symbols_overview on ___
```

## Read What Applies to Your Task

Don't read everything — read what's relevant:

| Your task involves... | Read this |
|---|---|
| Any code analysis or editing | [docs/serena-guide.md](docs/serena-guide.md) |
| Design or architecture decisions | [docs/architecture.md](docs/architecture.md) |
| PR, merge, or release | [docs/merge-release-safety.md](docs/merge-release-safety.md) + [docs/contributor-workflows.md](docs/contributor-workflows.md) |
| Build, test, or packaging | [docs/development-guide.md](docs/development-guide.md) |
| Finding specific code/components | [docs/component-inventory.md](docs/component-inventory.md) + [docs/source-tree-analysis.md](docs/source-tree-analysis.md) |
| Commit conventions, project rules | [CONTRIBUTING.md](CONTRIBUTING.md) (single source of truth) |

## Pre-Commit Checklist

→ [CONTRIBUTING.md — Before Committing](CONTRIBUTING.md#before-committing) (single source of truth)
→ [docs/serena-guide.md — Pre-Commit Verification](docs/serena-guide.md#pre-commit-verification-flow) (Serena steps)

## Subagent Rule

Every subagent prompt starts with:
```
Proje: freemind-ce. CLAUDE.md'yi oku ve talimatları uygula. Java kod analizi için Serena MCP kullan (grep/find değil). Görev: [X]
```
One line. The subagent reads THIS file and gets all the context it needs.

---

## Reference (look up as needed — do not read upfront)

### Quick Start

```bash
make build    # Build the project (compile + test)
make run      # Run FreeMind CE
make help     # Show all available make targets
```

> `make` wraps Gradle with correct `JAVA_HOME` and `--no-configuration-cache` flags.

- Entry point: `freemind.main.FreeMindStarter`
- Version: Managed by release-please. Never edit manually.

### Key Constraints

1. Never ignore `*.jar` in .gitignore — ~90 tracked local JARs
2. Never modify `generated-src/` — regenerate with `make jaxb`
3. Never commit `auto.properties` — runtime-generated
4. No `@SuppressWarnings` — fix root causes
5. Backward compatibility is ABSOLUTE — every `.mm` file must open
6. Never bypass merge controls — no `--admin`, no force merge
7. Dependency updates require manual review → [docs/merge-release-safety.md](docs/merge-release-safety.md#dependency-update-protocol)
8. Static analysis (PMD + SpotBugs) runs on every `make build` → [CONTRIBUTING.md — Static Analysis](CONTRIBUTING.md#static-analysis-quality-gates)
9. Parallel work is sovereign — never close, absorb, or supersede another session's PR → [CONTRIBUTING.md — Parallel Work Protection](CONTRIBUTING.md#parallel-work-protection)

### Repository Layout

```
freemind-ce/
├── CLAUDE.md                 # This file
├── CONTRIBUTING.md           # Single source of truth for ALL rules
├── Makefile                  # Development shortcuts
├── .serena/project.yml       # Serena LSP config
├── docs/                     # Architecture, guides, workflows
├── build.gradle.kts          # Root build config
├── freemind/                 # Main module
│   ├── freemind/             # Core source
│   ├── accessories/          # XSLT exports
│   ├── plugins/              # Plugin modules
│   ├── generated-src/        # JAXB generated (DO NOT EDIT)
│   └── tests/                # JUnit tests
└── _bmad-output/             # Technical specs
```

### Key Source Locations

| What | Where |
|------|-------|
| Main app class | `freemind/freemind/main/FreeMind.java` |
| Controller hub | `freemind/freemind/controller/Controller.java` |
| Primary mode | `freemind/freemind/modes/mindmapmode/MindMapController.java` |
| Map view | `freemind/freemind/view/mindmapview/MapView.java` |
| Node model | `freemind/freemind/modes/MindMapNode.java` |

### Architecture

→ [docs/architecture.md](docs/architecture.md) — MVC, modes, action framework, plugin system, data architecture.

### Common Tasks

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
make dist-zip       # Distribution ZIP
make help           # Show all targets
```

### Git & Release

→ [docs/contributor-workflows.md](docs/contributor-workflows.md) — Branch workflow, CI pipeline, release gating.
→ [docs/merge-release-safety.md](docs/merge-release-safety.md) — Merge protocol, release checklist.
→ [CONTRIBUTING.md — Commit Best Practices](CONTRIBUTING.md#commit-best-practices) — Conventional Commits format.

### Serena Quick Reference

```
EXPLORE:  get_symbols_overview(file, depth=1) → find_symbol(name, include_body=True)
VERIFY:   find_referencing_symbols(name, file) → make build
EDIT:     replace_symbol_body | insert_after_symbol | insert_before_symbol | rename_symbol
SEARCH:   search_for_pattern(regex) | find_file(mask, path) | list_dir(path, recursive)
```

Full guide: [docs/serena-guide.md](docs/serena-guide.md)
