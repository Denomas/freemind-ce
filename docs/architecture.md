# FreeMind CE - Architecture Document

> Generated: 2026-03-10 | Scan Level: Deep | Source: Verified code analysis

## Executive Summary

FreeMind CE is a Java 21 Swing desktop application for creating and editing mind maps. It uses an MVC architecture with a mode-based operation system, an XML action framework for undo/redo, and a hook-based plugin system for extensibility. The application is being modernized from a legacy Java 1.6/Ant codebase to Java 21/Gradle with JAXB replacing JiBX for XML binding.

## Architecture Pattern

**Primary:** Model-View-Controller (MVC) with Mode-based decomposition
**Secondary:** Hook/Plugin architecture, Observer pattern, Action/Actor pattern

```
┌─────────────────────────────────────────────────────────────┐
│                    FreeMindStarter                           │
│                    (Bootstrap + JVM checks)                  │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                    FreeMind                                   │
│                    (Main Application Frame)                   │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │  MenuBar     │  │  MainToolBar  │  │  MapModuleManager  │ │
│  └─────────────┘  └──────────────┘  └────────┬───────────┘ │
└──────────────────────────────────────────────┬──────────────┘
                                               │
              ┌────────────────────────────────┼────────────────┐
              │                                │                │
   ┌──────────▼──────────┐  ┌─────────────────▼──────┐ ┌──────▼──────┐
   │    BrowseMode        │  │    MindMapMode          │ │  FileMode   │
   │  (Read-only view)    │  │  (Full editing)         │ │ (File mgr)  │
   └─────────────────────┘  └─────────┬───────────────┘ └────────────┘
                                      │
                    ┌─────────────────┼──────────────────┐
                    │                 │                   │
         ┌──────────▼───┐  ┌────────▼────────┐  ┌──────▼──────────┐
         │  MindMapModel │  │  MindMapController│  │  MapView         │
         │  (Tree data)  │  │  (Edit actions)   │  │  (Canvas render) │
         └───────────────┘  └─────────┬────────┘  └──────────────────┘
                                      │
                            ┌─────────▼──────────┐
                            │  Action Framework   │
                            │  (XML Undo/Redo)    │
                            │  UndoActionHandler  │
                            │  50+ Actor classes   │
                            └─────────────────────┘
```

## Core Components

### 1. Application Bootstrap

| Class | File | Responsibility |
|---|---|---|
| `FreeMindStarter` | `freemind/main/FreeMindStarter.java` | JVM version check, property loading, launch |
| `FreeMind` | `freemind/main/FreeMind.java` | Main JFrame, module management, lifecycle |
| `FreeMindMain` | `freemind/main/FreeMindMain.java` | Core application interface contract |

**Bootstrap Sequence:**
1. `FreeMindStarter.main()` → Java version validation
2. Load `freemind.properties` (default config)
3. Create user directory `~/.freemind/`
4. Load `~/.freemind/auto.properties` (user overrides)
5. Set locale, initialize `FreeMind.main()`
6. Initialize Controller, MenuBar, ToolBar
7. Load initial mode (MindMapMode by default)

### 2. Controller Layer

| Class | Lines | Responsibility |
|---|---|---|
| `Controller` | ~2000 | Central orchestrator: menus, toolbar, zoom, filters, fonts |
| `MapModuleManager` | ~500 | Multiple open maps management, observer notifications |
| `MindMapController` | ~2467 | Primary editing controller: all node operations |

### 3. Mode System

Three operation modes, each with its own MVC triad:

| Mode | Purpose | Controller |
|---|---|---|
| MindMapMode | Full editing (primary) | `MindMapController` |
| BrowseMode | Read-only navigation | `BrowseController` |
| FileMode | File browser | `FileController` |

Modes are declared in `freemind.properties` and loaded dynamically.

### 4. Action Framework (Undo/Redo)

All edits are captured as XML action pairs (do/undo) via `freemind_actions.xsd`:

- **ActionPair** - Wraps forward + backward XML action
- **UndoActionHandler** - Manages undo/redo stacks with transaction support
- **Actor classes** - 50+ actors: `NewChildActor`, `DeleteChildActor`, `PasteActor`, `BoldActor`, `CloudActor`, `AddArrowLinkActor`, etc.
- **Compound Actions** - Nested action groups for complex operations

### 5. View Layer

| Class | Purpose |
|---|---|
| `MapView` | Main map canvas (JPanel + Printable + Autoscroll), 115KB+ |
| `NodeView` | Individual node renderer with layout variants |
| `RootMainView` / `ForkMainView` / `BubbleMainView` | Node style renderers |
| `EdgeView` variants | Linear, Bezier, Sharp variants for connections |
| `EditNodeTextField` / `EditNodeDialog` | Inline and dialog editing |
| `CloudView` | Cloud grouping visual |
| `NodeFoldingComponent` | Collapse/expand toggle |

### 6. Plugin/Hook Framework

```
HookAdapter (base)
├── ModeControllerHookAdapter    → Mode-level plugins (export, scripting)
├── NodeHookAdapter              → Node context actions
└── PermanentNodeHookAdapter     → Persistent node extensions
```

**Registration:** XML files per plugin define class, menu location, instantiation mode.
**Loading:** `HookFactory` dynamically loads and instantiates hooks.

### 7. Data Architecture

**Mind Map Format:** XML (`.mm` files) defined by `freemind.xsd`
- Tree structure: root node → child nodes (recursive)
- Node attributes: text, color, font, style, icons, links, notes
- Edges: style, color, width
- Clouds: color, grouping
- Arrow links: cross-node connections

**Action Format:** XML defined by `freemind_actions.xsd` (127+ element types)
- Used for undo/redo serialization
- Compound action support

**Configuration:**
- `freemind.properties` → default settings
- `~/.freemind/auto.properties` → user overrides
- `patterns.xml` → 24+ node styling patterns
- `mindmap_menus.xml` → declarative menu structure

## Plugin Architecture Detail

| Plugin | Entry Class | Registration XML | Dependencies |
|---|---|---|---|
| SVG/PDF Export | `ExportSvg`, `ExportPdf` | `ExportSvg.xml` | Batik 1.17, FOP 2.9 |
| Scripting | `ScriptingEngine` | `ScriptingEngine.xml` | Groovy, Rhino 1.7.14 |
| Map Viewer | `MapDialog` | `MapViewer.xml` | JMapViewer |
| Search | `SearchControllerHook` | `Search.xml` | Apache Lucene |
| Help | `FreemindHelpStarter` | `FreemindHelp.xml` | JavaHelp |
| Collaboration | Socket-based | `CollaborationSocket.xml` | TCP |
| LaTeX | `LatexNodeHook` | `Latex.xml` | HotEqn |

## Build Architecture

**Dual Build System (transition period):**

| System | Config | Status |
|---|---|---|
| Gradle 8.6+ | `build.gradle.kts` (root + modules) | Primary (modern) |
| Apache Ant | `freemind/build.xml` | Legacy (still functional) |

**Gradle Module Structure:**
```
:freemind                              (main application)
:freemind:plugins:svg                  (SVG/PDF export)
:freemind:plugins:script               (scripting)
:freemind:plugins:map                  (map viewer)
:freemind:plugins:search               (search)
:freemind:plugins:help                 (help)
:freemind:plugins:collaboration:socket (collaboration)
```

## Deployment Architecture

**Packaging:** `jpackage` (JDK 21 built-in)
- macOS: `.dmg` (with fallback manual DMG creation)
- Windows: `.exe` installer
- Linux: `.deb` package

**CI/CD:** GitHub Actions (`build.yml`)
- Multi-platform matrix: ubuntu-latest, windows-latest, macos-latest
- Build → Test → Package (per platform) → Release (on tag)
- Artifact retention: 30 days

## Testing Strategy

| Type | Framework | Location |
|---|---|---|
| Unit Tests | JUnit 4.13.2 | `freemind/tests/` |
| Mocking | Mockito 5.10.0 | Test classes |
| Property-Based | jqwik 1.8.2 | `tests/freemind/property/` |
| Assertions | AssertJ 3.25.3 | Test classes |

## Modernization Gaps (Known)

1. **LaTeX plugin** — Not in `settings.gradle.kts`, no `build.gradle.kts` exists yet
2. **Collaboration plugins** — Only Socket in Gradle; Database and Jabber are legacy-only
3. **Java module system** — Requires 6 `--add-opens` flags for Swing access
4. **macOS code signing** — DMG is not signed/notarized; Gatekeeper blocks first launch
5. **SecurityManager deprecation** — 25 compiler warnings from deprecated-for-removal APIs (Java 21)
6. **FlatLaf integration** — Dependency present but not yet activated as default look-and-feel
