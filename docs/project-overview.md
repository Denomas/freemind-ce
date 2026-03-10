# FreeMind CE - Project Overview

> Generated: 2026-03-10 | Scan Level: Deep | Source: Verified build files and source code

## Project Identity

- **Name:** FreeMind Classic Edition (CE)
- **Version:** 1.1.0-CE
- **Organization:** Denomas Engineering
- **License:** GNU General Public License v2.0
- **Repository Type:** Monolith (single desktop application with plugin submodules)
- **Project Type:** Desktop Application (Java Swing)

## Vision

FreeMind CE preserves the classic FreeMind mind-mapping experience (speed, simplicity, template structure) while running natively on modern systems. The project modernizes the original FreeMind codebase (20+ years old) to work with Java 21, macOS (including Apple Silicon), Windows 11, and modern Linux distributions.

**Primary Goal:** Achieve 100% cross-platform installation and operation on Windows, Linux, and macOS with automated CI/CD packaging and publishing via GitHub Actions.

**Target Audience:** Long-time FreeMind users and those seeking a simpler, classic alternative to Freeplane.

## Modernization Status

| Component | Original | Modern (CE) | Status |
|---|---|---|---|
| Build Tool | Apache Ant | Gradle 8.6+ (Kotlin DSL) | Done |
| Java Version | Java 1.6 | Java 21 (Temurin) | Done |
| XML Binding | JiBX (bytecode) | JAXB 2.3.9 | Done |
| Look & Feel | Metal/System | FlatLaf 3.4.1 | Done |
| SVG/PDF | Batik 1.6 | Batik 1.17 / FOP 2.9 | Done |
| Logging | java.util.logging | SLF4J 2.0.12 + Logback 1.4.14 | Done |
| CI/CD | None | GitHub Actions (multi-platform) | Done |
| Packaging | Manual | jpackage (DMG/EXE/DEB) | Done |

## Technology Stack

| Category | Technology | Version |
|---|---|---|
| Language | Java (OpenJDK/Temurin) | 21 |
| Build (Modern) | Gradle (Kotlin DSL) | 8.6+ |
| Build (Legacy) | Apache Ant | (still functional) |
| UI Framework | Java Swing | JDK built-in |
| Look & Feel | FlatLaf | 3.4.1 |
| Forms | JGoodies Forms | 1.9.0 |
| XML Binding | JAXB | 2.3.9 |
| SVG Rendering | Apache Batik | 1.17 |
| PDF Export | Apache FOP | 2.9 |
| JavaScript Engine | Mozilla Rhino | 1.7.14 |
| HTML Parsing | jsoup | 1.17.2 |
| XSLT Processing | Xalan + Xerces | 2.7.3 / 2.12.2 |
| Spell Checking | JOrtho | 1.0 |
| Logging | SLF4J + Logback | 2.0.12 / 1.4.14 |
| Testing | JUnit + Mockito + jqwik + AssertJ | 4.13.2 / 5.10.0 / 1.8.2 / 3.25.3 |
| Packaging | jpackage | JDK 21 built-in |
| CI/CD | GitHub Actions | v4 |

## Architecture Overview

FreeMind CE uses an **MVC (Model-View-Controller)** architecture with a **Mode-based** operation system and a **Hook/Plugin** framework for extensibility.

### Core Architecture Patterns

1. **MVC per Mode** - Each operation mode (Browse, MindMap, File) has its own Model, View, and Controller
2. **Action/Actor Pattern** - All edits recorded as XML actions for undo/redo (50+ action types in XSD)
3. **Hook/Plugin System** - XML-based plugin registration with dynamic class loading
4. **Observer Pattern** - Event-driven map module lifecycle, tree model changes, property changes
5. **Visitor Pattern** - Tree traversal via NodeViewVisitor

### Plugin Modules (6 active in Gradle)

| Plugin | Purpose | Key Dependency |
|---|---|---|
| SVG | SVG/PDF export | Apache Batik, FOP |
| Script | Groovy scripting engine | Groovy, Rhino |
| Map | OpenStreetMap integration | JMapViewer |
| Search | Full-text search | Apache Lucene |
| Help | Integrated help system | JavaHelp |
| Collaboration (Socket) | Real-time collaboration | TCP Sockets |

**Note:** LaTeX plugin exists in source but is not yet included in `settings.gradle.kts`.

### Legacy Modules (not in active build)

- **PDA** (`pda/`) - Palm OS version (historical)
- **Flash** (`flash/`) - Flash/web viewer (deprecated technology)
- **MediaWiki** (`mediawiki/`) - Wiki extension (reference only)

## Key Entry Points

- **Application Entry:** `freemind.main.FreeMindStarter` → version check → `FreeMind.main()`
- **Main Orchestrator:** `freemind.main.FreeMind` (49.7KB)
- **Controller Hub:** `freemind.controller.Controller` (2000+ lines)
- **Primary Mode Controller:** `freemind.modes.mindmapmode.MindMapController` (2467 lines)

## File Format

FreeMind uses XML-based `.mm` files with schema defined in `freemind.xsd`. Actions are serialized to XML per `freemind_actions.xsd` (127+ element types).

## Localization

30+ language packs available (en, de, fr, es, ja, zh_CN, zh_TW, ru, it, nl, pl, pt, sv, no, and more).

## Links to Detailed Documentation

- [Architecture](./architecture.md)
- [Source Tree Analysis](./source-tree-analysis.md)
- [Component Inventory](./component-inventory.md)
- [Development Guide](./development-guide.md)
