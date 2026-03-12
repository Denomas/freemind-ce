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

## Serena Code Intelligence

This project uses [Serena](https://github.com/oraios/serena) for LSP-powered semantic code analysis via MCP.

- **Config:** `.serena/project.yml` (versioned)
- **Setup:** `claude mcp add serena -- uvx --from git+https://github.com/oraios/serena serena start-mcp-server --context=claude-code --project-from-cwd`
- **Index:** `uvx --from git+https://github.com/oraios/serena serena project index .`
- **Pre-commit:** Always use `find_referencing_symbols` to verify impact of changes before committing

For full setup → [`CONTRIBUTING.md`](CONTRIBUTING.md#serena-code-intelligence-required) | [`docs/development-guide.md`](docs/development-guide.md#serena-code-intelligence)

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
- **Branch:** `main` — trunk-based, all work here
- **Release:** Tag `v*.*.*` on main → GitHub Actions auto-builds DMG/EXE/DEB
- **Pre-commit:** `.pre-commit-config.yaml` — XML validation, Java compilation, whitespace

## Architecture (Summary)

- **Pattern:** MVC + Mode-based (Browse/MindMap/File) + Hook/Plugin
- **XML Binding:** JAXB 2.3.9 (schema: `freemind_actions.xsd`)
- **Plugins:** Registered via XML descriptors in `plugins/`, loaded by `ImportWizard`
- **L&F:** FlatLaf 3.4.1 (light/dark), configured in `freemind.properties`

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

1. **Never ignore `*.jar` in .gitignore** — project depends on ~90 tracked local JARs in `lib/`
2. **Never modify `generated-src/`** — regenerate with `make jaxb`
3. **Never commit `auto.properties`** — runtime-generated user config
4. **Test before commit** — `make build` must pass, `make run` to verify
5. **No @SuppressWarnings** — fix root causes, don't suppress
6. **Preserve backward compatibility** — existing .mm files must keep working
7. **Verify with Serena before commit** — use `find_referencing_symbols` to check impact of all changes

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
