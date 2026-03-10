# FreeMind CE — Developer Guide

## Quick Start

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
./gradlew :freemind:build --no-configuration-cache
./gradlew :freemind:run --no-configuration-cache
```

- Always use `--no-configuration-cache` (config cache serialization issue)
- Entry point: `freemind.main.FreeMindStarter`
- Version: `build.gradle.kts` (root) → `version = "X.Y.Z-CE"`

## Repository Layout

```
freemind-ce/
├── CLAUDE.md                 # This file — read first
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

- **Remotes:** `github` → Denomas/freemind-ce (push target), `origin` → SourceForge (read-only)
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
2. **Never modify `generated-src/`** — regenerate with `./gradlew :freemind:generateJaxb`
3. **Never commit `auto.properties`** — runtime-generated user config
4. **Test before commit** — `./gradlew build` must pass, run the app to verify
5. **No @SuppressWarnings** — fix root causes, don't suppress
6. **Preserve backward compatibility** — existing .mm files must keep working

## Common Tasks

```bash
# Full build (all platforms)
./gradlew build --no-configuration-cache

# Run the app
./gradlew :freemind:run --no-configuration-cache

# Regenerate JAXB classes
./gradlew :freemind:generateJaxb

# Package for macOS/Windows/Linux
./gradlew :freemind:jpackageMac
./gradlew :freemind:jpackageWin
./gradlew :freemind:jpackageLinux

# Debug mode (port 5005)
./gradlew :freemind:run --debug-jvm
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
