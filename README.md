# FreeMind Classic Edition (CE)

**Denomas Engineering - 2026**

[![Build Status](https://github.com/Denomas/freemind-ce/actions/workflows/build.yml/badge.svg)](https://github.com/Denomas/freemind-ce/actions)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

---

## Why FreeMind CE?

FreeMind was many of our first mind-mapping experience. For years we organized our ideas, projects, and dreams with this small but powerful program. It launched fast, stayed out of the way, had nothing unnecessary. Just you and your thoughts.

Time passed. Java versions changed, operating systems evolved, and original FreeMind development stopped. Freeplane became a fine alternative -- we love it too. But FreeMind's original feel, that simplicity, that speed... Some things you never forget.

**FreeMind CE was born to bring that old friend back to life.**

We preserved every line, every feature, every icon of the original FreeMind. We changed nothing -- we only modernized. It runs on Java 21, works natively on macOS Apple Silicon, installs cleanly on Windows 11 and Linux. It builds with Gradle and packages automatically via GitHub Actions.

To all the old friends out there -- FreeMind is still here, still the same, still the fastest.

We are proud to share this project with the community.

---

## Quick Start

### Prerequisites

- **Java 21 JDK** (Temurin, OpenJDK, or Oracle)
- **Gradle 8.6+** (wrapper included)

### Build & Run

```bash
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce
./gradlew build
./gradlew :freemind:run
```

### Download Pre-built Packages

Grab ready-to-use installers from the [GitHub Releases](https://github.com/Denomas/freemind-ce/releases) page.

| Platform | Package | Install |
|----------|---------|---------|
| **macOS** | `.dmg` | Open and drag to Applications ([see note below](#macos-gatekeeper)) |
| **Windows** | `.exe` | Double-click and install |
| **Linux** | `.deb` | `sudo apt install ./freemind-ce_1.1.0_amd64.deb` |

#### macOS Gatekeeper

Since FreeMind CE is not yet signed with an Apple Developer certificate, macOS will block the first launch. To allow it:

**Option 1 — Right-click method (recommended):**
1. Open Finder and navigate to `/Applications`
2. Right-click (or Control-click) on **FreeMind-CE**
3. Select **Open** from the context menu
4. Click **Open** in the dialog that appears
5. You only need to do this once — subsequent launches work normally

**Option 2 — Terminal:**
```bash
xattr -cr /Applications/FreeMind-CE.app
```

**Option 3 — System Settings:**
1. Try to open FreeMind-CE (it will be blocked)
2. Go to **System Settings > Privacy & Security**
3. Scroll down — you will see "FreeMind-CE was blocked"
4. Click **Open Anyway**

### Build Packages from Source

| Platform | Command | Output |
|----------|---------|--------|
| **macOS** | `./gradlew :freemind:jpackageMac` | `.dmg` installer |
| **Windows** | `gradlew.bat :freemind:jpackageWin` | `.exe` installer |
| **Linux** | `./gradlew :freemind:jpackageLinux` | `.deb` package |

## Modernization

```
Phase 1: Gradle Build System ✅ (replaced Ant)
Phase 2: JAXB Migration ✅ (replaced JiBX)
Phase 3: Java 21 Compatibility ✅
Phase 4: Plugin Modernization ✅
Phase 5: CI/CD & Distribution ✅
Phase 6: Standalone HTML Export ✅
Phase 7: Context Graph Plugin ✅
```

### What Changed from the Original?

| Component | Original | CE |
|-----------|----------|----|
| **Build** | Ant | Gradle 8.6 (Kotlin DSL) |
| **Java** | 1.6 | 21 |
| **XML Binding** | JiBX | JAXB 2.3.9 |
| **SVG/PDF** | Batik 1.6 | Batik 1.17 / FOP 2.9 |
| **Logging** | java.util.logging | SLF4J + Logback |
| **CI/CD** | None | GitHub Actions (multi-platform) |
| **Packaging** | Manual | jpackage (DMG/EXE/DEB) |
| **Encoding** | Mixed | UTF-8 everywhere |

### What Stayed the Same?

Everything. The interface, icons, keyboard shortcuts, file format (.mm), plugin system, template structure. Everything you loved about FreeMind is exactly where you left it.

## Project Structure

```
freemind-ce/
├── freemind/              # Main application module
│   ├── freemind/          # Core Java source code
│   ├── accessories/       # XSLT exports, accessory plugins
│   ├── plugins/           # Plugin modules (SVG, Script, Map, Search, Help)
│   ├── images/            # Icons and images
│   ├── generated-src/     # JAXB generated classes (do not touch)
│   └── build.gradle.kts   # Module build config
├── docs/                  # Project documentation
├── .github/workflows/     # CI/CD (build + release)
├── build.gradle.kts       # Root build config
└── settings.gradle.kts    # Gradle settings
```

## Development

```bash
# Regenerate JAXB classes
./gradlew :freemind:generateJaxb

# Run tests
./gradlew test

# Debug mode (port 5005)
./gradlew :freemind:run --debug-jvm

# Generate Javadoc
./gradlew javadoc
```

## Keyboard Shortcuts (macOS)

| Action | Shortcut |
|--------|----------|
| New Map | `⌘ N` |
| Open | `⌘ O` |
| Save | `⌘ S` |
| Add Child Node | `TAB` |
| Add Sibling Node | `↵ Enter` |
| Edit Node | `F2` |
| Toggle Fold | `SPACE` |
| Find | `⌘ F` |
| Zoom In | `⌘ +` |
| Zoom Out | `⌘ -` |

## License

FreeMind CE is licensed under **GNU General Public License v2.0** (GPL-2.0), same as the original FreeMind.

```
Copyright (C) 2000-2026 Joerg Mueller, Daniel Polansky, Christian Foltin,
Dimitry Polivaev, Tolga Karatas (Denomas), and others.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
```

## Acknowledgments

- The original [FreeMind](https://sourceforge.net/p/freemind/) team -- for creating this wonderful program
- The SourceForge community -- the platform that was home for so many years
- All FreeMind users -- for staying loyal
- Apache Software Foundation (Batik, FOP)

## Support

- **Issues**: [GitHub Issues](https://github.com/Denomas/freemind-ce/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Denomas/freemind-ce/discussions)
- **Documentation**: [`docs/`](docs/index.md)
- **Turkish README**: [README-TR.md](README-TR.md)

---

**Made with love by Denomas Engineering, for the FreeMind community.**
