# FreeMind CE - Development Guide

## Prerequisites

| Requirement | Version | Notes |
|---|---|---|
| Java (JDK) | 21+ | OpenJDK/Temurin recommended |
| Gradle | 8.6+ | Wrapper included (`./gradlew`) |
| Git | Any recent | For source control |
| IDE (recommended) | IntelliJ IDEA or Eclipse | Java 21 support required |

## Quick Start

```bash
# Clone repository
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce

# Build and run using Make (recommended)
make build    # Compile + test
make run      # Run FreeMind CE
make help     # Show all available targets
```

## Makefile Shortcuts (Recommended)

The project includes a cross-platform `Makefile` that auto-detects `JAVA_HOME` and wraps Gradle with the correct flags. It supports macOS (Homebrew/MacPorts), Linux (apt/dnf/SDKMAN), and Windows (WSL/Git Bash). Use `make help` to see all targets:

| Target | Description |
|---|---|
| **Development** | |
| `make build` | Build the project (compile + test) |
| `make run` | Run FreeMind CE |
| `make debug` | Run in debug mode (attach debugger on port 5005) |
| `make test` | Run tests only |
| `make coverage` | Run tests with JaCoCo coverage report |
| `make check` | Build + all quality checks |
| `make clean` | Clean all build artifacts |
| `make jaxb` | Regenerate JAXB classes from XSD schema |
| `make javadoc` | Generate API documentation |
| **Packaging** | |
| `make package` | Build native package for current OS (auto-detect) |
| `make package-mac` | Build macOS DMG package |
| `make package-win` | Build Windows EXE installer |
| `make package-linux` | Build Linux DEB package |
| `make dist-zip` | Create distribution ZIP archive |
| `make install-dist` | Create local distribution layout |
| **General** | |
| `make info` | Show detected Java and system info |
| `make help` | Show this help message |

To override Java detection: `JAVA_HOME=/path/to/jdk make build`

## Raw Gradle Commands

For cases where you need direct Gradle access (CI, IDE integration, advanced options):

| Command | Purpose |
|---|---|
| `./gradlew build` | Full compilation, testing, packaging |
| `./gradlew :freemind:run` | Run application with JVM args |
| `./gradlew test` | Run all unit tests |
| `./gradlew :freemind:generateJaxb` | Generate JAXB classes from XSD |
| `./gradlew :freemind:jar` | Build main JAR |
| `./gradlew :freemind:installDist` | Create distribution layout |
| `./gradlew :freemind:distZip` | Create distribution ZIP |
| `./gradlew :freemind:jpackageMac` | Create macOS .dmg |
| `./gradlew :freemind:jpackageWin` | Create Windows .exe |
| `./gradlew :freemind:jpackageLinux` | Create Linux .deb |
| `./gradlew :freemind:prepareMacDist` | Prepare macOS key mappings |
| `./gradlew javadoc` | Generate API documentation |

> **Important:** Always use `--no-configuration-cache` flag with raw Gradle commands. The Makefile handles this automatically.

## JVM Arguments (applied automatically via `./gradlew :freemind:run`)

```
-Xms64m -Xmx512m -Xss8M
-Dapple.laf.useScreenMenuBar=true
--add-opens=java.desktop/java.awt=ALL-UNNAMED
--add-opens=java.desktop/java.awt.event=ALL-UNNAMED
--add-opens=java.desktop/javax.swing=ALL-UNNAMED
--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
```

## Project Module Structure

```
:freemind                              → Main application
:freemind:plugins:svg                  → SVG/PDF export
:freemind:plugins:script               → Groovy scripting
:freemind:plugins:map                  → OpenStreetMap viewer
:freemind:plugins:search               → Full-text search
:freemind:plugins:help                 → Help system
:freemind:plugins:contextgraph         → Context graph export
:freemind:plugins:collaboration:socket → Real-time collaboration
```

## Source Layout

```
freemind/
├── freemind/          → Main Java sources (3 srcDirs)
│   ├── freemind/      → Core application packages
│   ├── accessories/   → Accessory/utility classes
│   └── de/            → German-specific classes
├── tests/             → Test sources (JUnit + jqwik)
├── images/            → Icons and images (resources)
└── doc/               → Template .mm files (resources)
```

## Configuration Files

| File | Purpose | Location |
|---|---|---|
| `freemind.properties` | Default application settings | `freemind/` |
| `auto.properties` | User overrides | `~/.freemind/` |
| `patterns.xml` | Node styling patterns | `freemind/` |
| `mindmap_menus.xml` | Menu structure definition | `freemind/` |
| `freemind.xsd` | Mind map file format schema | `freemind/` |
| `freemind_actions.xsd` | Action (undo/redo) schema | `freemind/` (root) |
| `Resources_*.properties` | Language resource bundles | `freemind/freemind/` |

## Testing

### Running Tests
```bash
# Using Make (recommended)
make test           # Unit tests only
make test-gui       # GUI tests only
make coverage       # Tests + JaCoCo coverage report

# Using Gradle directly
./gradlew test --no-configuration-cache                    # Unit tests
./gradlew testGui --no-configuration-cache                 # GUI tests
./gradlew test --no-configuration-cache --info              # Verbose output
```

### Test Framework
- **JUnit 4** test classes (`extends TestCase`) running on **JUnit 5 vintage engine**
- **GUI tests:** AssertJ Swing with `GuiTestBase` base class, `@Tag("gui")`, automatic screenshots
- **Property-based:** jqwik (sample size 1000, tries 100, seed 12345 — reproducible)
- **Test base:** `FreeMindTestBase` provides headless FreeMind context via `HeadlessFreeMind`

### Test Structure

| Directory/File | Purpose |
|---|---|
| `tests/freemind/gui/` | 22 GUI test classes (AssertJ Swing) |
| `tests/freemind/property/` | Property-based tests (jqwik) |
| `tests/freemind/fuzz/` | Fuzz tests |
| `tests/freemind/findreplace/` | Search/replace tests |
| `tests/freemind/unicode/` | Unicode handling tests |
| `FreeMindTestBase.java` | Unit test base class |
| `GuiTestBase.java` | GUI test base class (robot, screenshots) |
| `MarshallerTests.java` | JAXB XML serialization |
| `LayoutTests.java` | UI layout verification |
| `TransformTest.java` | XSLT transformation |
| `CollaborationTests.java` | Collaboration module |
| `ExportTests.java` | Export functionality |
| `ContextGraphExportTest.java` | Context graph plugin |
| `HtmlConversionTests.java` | HTML export |
| `StandaloneMapTests.java` | Standalone map operations |

## Security Audit

Dependency vulnerability scanning catches known CVEs in project dependencies before they reach production.

### Running Locally

```bash
# Quick scan — Grype (requires: mise install grype)
make audit            # ~30 seconds, fails on High+ with known fix

# Full report — OWASP Dependency-Check (Gradle plugin)
make audit-full       # ~5 minutes, HTML report
open freemind/build/reports/dependency-check-report.html
```

### What Gets Scanned

| Source | Examples |
|--------|---------|
| Gradle dependencies | jsoup, Batik, FOP, Lucene, Logback, JUnit |
| Tracked JARs (`lib/`) | jortho.jar, SimplyHTML.jar, bindings.jar |
| Plugin JARs (`plugins/*/`) | JMapViewer.jar, jhall.jar, groovy-all.jar |
| Build tools (non-runtime) | PMD, SpotBugs, JaCoCo |

### Core Principle

> No external API dependency for security tooling. If a tool runs as a local CLI, we use the CLI — not cloud dashboards or paid services. All scanning runs offline after initial database download.

### GitHub Safety Net

Weekly `security-scan.yml` runs Grype + Trivy + OWASP on GitHub Actions with SARIF upload to the Security tab. This catches anything missed locally and provides audit trail.

Full documentation: **[CONTRIBUTING.md — Security Audit](../CONTRIBUTING.md#security-audit-vulnerability-scanning)**

## Static Analysis

Static analysis tools (PMD + SpotBugs), configuration, reports, and the early detection chain are documented in **one place**:

> **[CONTRIBUTING.md — Static Analysis](../CONTRIBUTING.md#static-analysis-quality-gates)** — Tools, config files, report locations, viewing commands, early detection chain.

## Git Remotes

| Remote | URL | Purpose |
|--------|-----|---------|
| `origin` | `https://github.com/Denomas/freemind-ce.git` | Primary repository (push/pull) |
| `upstream` | `git://git.code.sf.net/p/freemind/code` | Original FreeMind on SourceForge (read-only) |

- **Branch:** `main` — trunk-based development, all work here
- **Tracking:** `main` tracks `origin/main`
- **Releases:** Tag `v*.*.*` on `main` triggers GitHub Actions release workflow

## CI/CD Pipeline

CI pipeline details, path filtering, test matrix, workflows, and packaging are documented in **one place**:

> **[docs/contributor-workflows.md — Section 6](contributor-workflows.md#section-6-ci-pipeline)** — CI flow diagrams, path filtering, doc-only detection, release gating.

> **[docs/merge-release-safety.md](merge-release-safety.md)** — Merge protocol, release checklist, dependency updates.

## Legacy Build (Ant)

The original Ant build system (`freemind/build.xml`) is preserved for reference but **Gradle is the primary build system**:
- Ant build targets Java 21 (`java_source_version=21`)
- Contains macOS `.app` bundle creation scripts
- Plugin builds: `build_svg.xml`, `build_map.xml`, etc.

**Note:** Ant build references JiBX (legacy XML binding). The Gradle build uses JAXB 2.3.9 instead. Use Gradle for all development.

## Dependency Management

Dependencies are managed in `freemind/build.gradle.kts` with version variables:
- `jaxbImplVersion = "2.3.9"` / `jaxbApiVersion = "2.3.1"`
- `batikVersion = "1.19"`
- `fopVersion = "2.11"`
- `flatlafVersion = "3.7"`
- `jgoodiesFormsVersion = "1.8.0"` / `jgoodiesCommonVersion = "1.8.1"`
- `junitVersion = "4.13.2"`

Repositories: Maven Central + JitPack

## Serena Code Intelligence

The project includes [Serena](https://github.com/oraios/serena) configuration for LSP-powered semantic code analysis. The config file `.serena/project.yml` is versioned in the repository.

### Installation

**Prerequisites:** [uv](https://docs.astral.sh/uv/getting-started/installation/) (Python package manager)

```bash
# Add Serena MCP server to Claude Code
claude mcp add serena -- uvx --from git+https://github.com/oraios/serena \
  serena start-mcp-server --context=claude-code --project-from-cwd

# Index the codebase (creates LSP cache for fast symbol lookup)
uvx --from git+https://github.com/oraios/serena serena project index .

# Verify everything works
uvx --from git+https://github.com/oraios/serena serena project health-check .
```

### Configuration

| File | Purpose | Versioned |
|------|---------|-----------|
| `.serena/project.yml` | Project config (language, encoding, ignored paths) | Yes |
| `.serena/project.local.yml` | Local overrides (developer-specific) | No (.gitignore) |
| `~/.serena/serena_config.yml` | Global Serena config (backend, dashboard, timeouts) | No |
| `.serena/memories/` | Project-specific AI memories | No (.gitignore) |

### Java LSP Configuration

For Java 21 support, add to `~/.serena/serena_config.yml`:

```yaml
ls_specific_settings:
  java:
    gradle_java_home: "/path/to/your/java-21-home"
```

On macOS with Homebrew: `/opt/homebrew/Cellar/openjdk@21/21.0.10/libexec/openjdk.jdk/Contents/Home`

### Re-indexing

Re-index after major codebase changes (new files, moved packages, renamed classes):

```bash
uvx --from git+https://github.com/oraios/serena serena project index .
```

## Common Development Tasks

### Adding a new plugin
1. Create directory under `freemind/plugins/<name>/`
2. Create `build.gradle.kts` with `compileOnly(project(":freemind"))` dependency
3. Add `include(":freemind:plugins:<name>")` to `settings.gradle.kts`
4. Create plugin registration XML file
5. Implement hook class extending appropriate adapter

### Modifying menus
Edit `freemind/mindmap_menus.xml` - declarative menu hierarchy.

### Adding a new action
1. Create Actor class in `freemind/modes/mindmapmode/actions/`
2. Define XML element in `freemind_actions.xsd`
3. Register in `MindMapController`

### Adding translations
Create/edit `Resources_<locale>.properties` in `freemind/freemind/`
