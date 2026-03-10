# FreeMind CE - Development Guide

> Generated: 2026-03-10 | Scan Level: Deep | Source: Verified build files

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
git clone <repo-url> freemind-ce
cd freemind-ce

# Build the project
./gradlew build

# Run the application
./gradlew :freemind:run

# Run tests
./gradlew test
```

## Build Commands

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
# All tests
./gradlew test

# With detailed output
./gradlew test --info
```

### Test Configuration (automatic)
- JUnit Platform enabled
- jqwik: sample size 1000, tries 100, seed 12345 (reproducible)
- Full exception format logging

### Test Files
| Test | Purpose |
|---|---|
| `AllTests.java` | Test suite aggregator |
| `MarshallerTests.java` | JAXB XML serialization |
| `LayoutTests.java` | UI layout verification |
| `FindTextTests.java` | Search functionality |
| `TransformTest.java` | XSLT transformation |
| `CollaborationTests.java` | Collaboration module |
| `ScriptEditorPanelTest.java` | Scripting UI |
| `tests/freemind/property/` | Property-based tests (jqwik) |

## CI/CD Pipeline

**GitHub Actions** (`.github/workflows/build.yml`):

1. **Build** - Matrix: Ubuntu, Windows, macOS (parallel)
   - Checkout → Java 21 setup → Gradle build → Tests → Upload artifacts
2. **Package macOS** - jpackage DMG (with manual fallback)
3. **Package Windows** - jpackage EXE
4. **Package Linux** - jpackage DEB
5. **Release** - Upload to GitHub Releases (on release event)
6. **Homebrew Update** - Optional tap update

**Triggers:** Push to main/develop, PRs on main, release creation

## Legacy Build (Ant)

The original Ant build system (`freemind/build.xml`) remains functional:
- Uses JiBX for XML binding (legacy)
- Targets Java 21 (`java_source_version=21`)
- Contains macOS `.app` bundle creation
- Plugin builds: `build_svg.xml`, `build_map.xml`, etc.

**Note:** Ant build still references JiBX bytecode enhancement in `lib/jibx/`. The Gradle build uses JAXB instead.

## Dependency Management

Dependencies are managed in `freemind/build.gradle.kts` with version variables:
- `jaxbVersion = "2.3.9"`
- `batikVersion = "1.17"`
- `fopVersion = "2.9"`
- `flatlafVersion = "3.4.1"`
- `jgoodiesVersion = "1.9.0"`
- `junitVersion = "4.13.2"`

Repositories: Maven Central + JitPack

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
