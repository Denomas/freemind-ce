# FreeMind CE - Source Tree Analysis

> Generated: 2026-03-10 | Scan Level: Deep

## Project Root Structure

```
freemind-ce/
├── freemind/                          # MAIN APPLICATION MODULE
│   ├── freemind/                      # Core Java source packages
│   │   ├── main/                      # ★ Entry points (FreeMindStarter, FreeMind)
│   │   ├── controller/                # MVC Controller layer
│   │   │   ├── color/                 # Color picker components
│   │   │   ├── filter/                # Node filtering engine + conditions
│   │   │   └── printpreview/          # Print preview dialog
│   │   ├── view/                      # MVC View layer
│   │   │   └── mindmapview/           # Map canvas, node rendering, edge drawing
│   │   ├── modes/                     # Operation modes system
│   │   │   ├── browsemode/            # Read-only navigation mode
│   │   │   ├── mindmapmode/           # ★ Primary editing mode
│   │   │   │   ├── actions/           # All editing actions (bold, copy, paste, etc.)
│   │   │   │   ├── dialogs/           # Mode-specific dialogs
│   │   │   │   ├── hooks/             # Mode hook implementations
│   │   │   │   └── listeners/         # Event listeners
│   │   │   ├── filemode/              # File browser mode
│   │   │   ├── common/                # Shared mode utilities
│   │   │   ├── attributes/            # Node attribute system
│   │   │   └── viewmodes/             # View mode configurations
│   │   ├── extensions/                # Hook/Plugin extension framework
│   │   ├── preferences/               # User preferences system
│   │   │   └── layout/                # Preferences UI (OptionPanel)
│   │   ├── swing/                     # Swing utility classes
│   │   └── common/                    # Shared utilities
│   │
│   ├── accessories/                   # Accessory classes and utilities
│   │   └── plugins/                   # Plugin helper classes
│   │       ├── dialogs/               # Shared plugin dialogs
│   │       ├── flash/                 # Flash export helpers
│   │       ├── icons/                 # Icon management
│   │       ├── time/                  # Time/calendar plugin
│   │       └── util/                  # Plugin utilities
│   │
│   ├── de/foltin/                     # CompileXsdStart (XSD compilation utility)
│   │
│   ├── plugins/                       # ★ PLUGIN MODULES (Gradle subprojects)
│   │   ├── svg/                       # SVG/PDF export (Batik + FOP)
│   │   ├── script/                    # Groovy scripting engine
│   │   ├── map/                       # OpenStreetMap integration (JMapViewer)
│   │   ├── search/                    # Full-text search (Lucene)
│   │   ├── help/                      # Integrated help system (JavaHelp)
│   │   ├── contextgraph/             # ★ Context Graph export (Markdown + XML)
│   │   ├── latex/                     # LaTeX formula rendering (legacy, not in Gradle)
│   │   └── collaboration/             # Real-time collaboration
│   │       ├── socket/                # ★ TCP socket-based (in Gradle)
│   │       ├── database/              # Database-based (legacy)
│   │       └── jabber/                # XMPP-based (legacy)
│   │
│   ├── tests/                         # Test suites
│   │   └── freemind/
│   │       ├── findreplace/           # Search/replace tests
│   │       └── property/              # Property-based tests (jqwik)
│   │           └── generators/        # Custom test generators
│   │
│   ├── images/                        # Application icons and images
│   │   └── icons/                     # 100+ node icons
│   ├── doc/                           # Mind map templates (.mm files)
│   │   └── development/               # Developer icon resources
│   ├── html/                          # HTML templates
│   ├── lib/                           # Legacy JAR dependencies (JiBX, etc.)
│   ├── binding/                       # JiBX binding classes (legacy)
│   ├── windows-launcher/              # Windows C++ launcher (legacy)
│   │
│   ├── build.gradle.kts               # ★ Main module Gradle build
│   ├── build.xml                      # Legacy Ant build (still functional)
│   ├── freemind.properties            # ★ Main application config
│   ├── freemind.xsd                   # Mind map file format schema
│   ├── freemind_actions.xsd           # Undo/redo action schema (127+ types)
│   ├── patterns.xml                   # Node styling patterns (24+)
│   ├── mindmap_menus.xml              # Declarative menu structure
│   └── *.properties                   # 30+ language resource bundles
│
├── admin/                             # Administrative assets (legacy)
│   ├── docs/                          # Historical documentation (HTML)
│   ├── installer/                     # Legacy installer configs (mac/rpm/windows)
│   └── software/                      # Related software (openstreetmap, sortmm)
│
├── pda/                               # Palm OS version (historical, inactive)
├── flash/                             # Flash viewer (deprecated technology)
├── mediawiki/                         # MediaWiki extension (reference)
├── plugins/wsl/                       # WSL plugin (standalone)
│
├── .github/workflows/                 # ★ CI/CD Pipelines
│   ├── build.yml                      # Multi-platform build + packaging
│   └── property-tests.yml             # Property-based test workflow
│
├── gradle/                            # Gradle wrapper files
├── docs/                              # ★ Generated project documentation
│
├── build.gradle.kts                   # Root Gradle config
├── settings.gradle.kts                # Module includes (6 plugins)
├── gradle.properties                  # Gradle JVM/cache settings
├── gradlew                            # Gradle wrapper script
└── README.md                          # Project README (draft)
```

## Critical Folders

| Folder | Purpose | Criticality |
|---|---|---|
| `freemind/freemind/main/` | Application entry point and bootstrap | Core |
| `freemind/freemind/controller/` | MVC Controller, menus, toolbars, events | Core |
| `freemind/freemind/view/mindmapview/` | Map canvas, node rendering, editing | Core |
| `freemind/freemind/modes/mindmapmode/` | Primary editing mode (2467-line controller) | Core |
| `freemind/freemind/extensions/` | Plugin/Hook framework | Core |
| `freemind/plugins/` | All plugin modules | Extension |
| `.github/workflows/` | CI/CD build & packaging pipelines | DevOps |
| `freemind/freemind/modes/mindmapmode/actions/` | All editing actions + undo/redo | Core |

## Entry Points

| Entry Point | File | Purpose |
|---|---|---|
| `FreeMindStarter.main()` | `freemind/freemind/main/FreeMindStarter.java` | JVM bootstrap, version check |
| `FreeMind.main()` | `freemind/freemind/main/FreeMind.java` | Application initialization |
| Plugin XML configs | `freemind/plugins/*//*.xml` | Plugin registration |
| Gradle build | `freemind/build.gradle.kts` | Build entry |
| CI/CD | `.github/workflows/build.yml` | Automated build |

## Source Code Statistics

| Area | Java Files (approx) | Key Classes |
|---|---|---|
| Core (`freemind/freemind/`) | ~379 | FreeMind, Controller, MapView, NodeView |
| Accessories | ~59 | Plugin helpers, time manager, icon manager |
| Plugins | ~57 | SVG export, scripting, map viewer, search |
| Tests | ~12 | JUnit + jqwik property tests |
| **Total** | **~507** | |
