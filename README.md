# FreeMind Classic Edition (CE)

**Denomas Engineering - 2026**

[![Build Status](https://github.com/denomas/freemind-ce/actions/workflows/build.yml/badge.svg)](https://github.com/denomas/freemind-ce/actions)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

## 🎯 Vision

FreeMind Classic Edition preserves the classic FreeMind experience (speed, simplicity, template structure) while running natively on modern systems:

- ✅ **Java 21** - Modern JVM compatibility
- ✅ **macOS** - Native support on Apple Silicon and Intel Macs
- ✅ **Windows 11** - Full compatibility
- ✅ **Linux** - Debian, Ubuntu, Fedora, Arch
- ✅ **Gradle Build** - Modern dependency management
- ✅ **JAXB** - Replaced legacy JiBX XML binding

## 🚀 Quick Start

### Prerequisites

- **Java 21 JDK** (Temurin, OpenJDK, or Oracle)
- **Gradle 8.6+** (wrapper included)

### Installation

```bash
# Clone the repository
git clone https://github.com/denomas/freemind-ce.git
cd freemind-ce

# Build the project
./gradlew build

# Run the application
./gradlew :freemind:run
```

### Native Installation

#### macOS (Homebrew)
```bash
brew tap denomas/homebrew-tap
brew install --cask freemind-ce
```

#### Windows (Chocolatey)
```bash
choco install freemind-ce
```

#### Linux (Debian/Ubuntu)
```bash
sudo apt install ./freemind-ce_1.0.0-CE_amd64.deb
```

## 📦 Build Artifacts

| Platform | Command | Output |
|----------|---------|--------|
| **All** | `./gradlew build` | JAR in `build/libs/` |
| **macOS** | `./gradlew jpackageMac` | `.dmg` installer |
| **Windows** | `./gradlew jpackageWin` | `.exe` installer |
| **Linux** | `./gradlew jpackageLinux` | `.deb` package |

## 🏗️ Architecture

### Modernization Phases

```
Phase 1: Gradle Build System ✅
Phase 2: JAXB Migration 🔄
Phase 3: Java 21 Compatibility ⏳
Phase 4: Plugin Modernization ⏳
Phase 5: CI/CD & Distribution ⏳
```

### Key Changes from Original FreeMind

| Component | Original | Modern (CE) |
|-----------|----------|-------------|
| **Build Tool** | Ant | Gradle 8.6 |
| **Java Version** | Java 1.6 | Java 21 |
| **XML Binding** | JiBX | JAXB 2.3.9 |
| **Look & Feel** | Metal | FlatLaf 3.4.1 |
| **SVG/PDF** | Batik 1.6 | Batik 1.17 |
| **Logging** | java.util.logging | SLF4J + Logback |

## 📁 Project Structure

```
freemind-ce/
├── freemind/              # Main application module
│   ├── freemind/          # Core Java sources
│   ├── accessories/       # Accessories and utilities
│   ├── plugins/           # Plugin modules
│   ├── images/            # Resources and icons
│   └── build.gradle.kts   # Module build config
├── plugins/               # Standalone plugins
├── admin/                 # Documentation and assets
├── build.gradle.kts       # Root build config
└── settings.gradle.kts    # Gradle settings
```

## 🔧 Development

### Generate JAXB Classes

```bash
./gradlew :freemind:generateJaxb
```

### Run Tests

```bash
./gradlew test
```

### Build Documentation

```bash
./gradlew javadoc
```

### Debug Mode

```bash
./gradlew :freemind:run --debug-jvm
# Connect debugger to port 5005
```

## ⌨️ Keyboard Shortcuts (macOS)

| Action | Shortcut |
|--------|----------|
| New Map | `⌘ N` |
| Open | `⌘ O` |
| Save | `⌘ S` |
| Add Child | `TAB` |
| Add Sibling | `↵ Enter` |
| Edit Node | `F2` |
| Toggle Fold | `SPACE` |
| Find | `⌘ F` |
| Zoom In | `⌘ +` |
| Zoom Out | `⌘ -` |

## 🎨 Features

### Preserved from Classic FreeMind
- ⚡ Fast startup and rendering
- 🎯 Simple, focused UI
- 📐 Template system
- 🔗 Relative/absolute links
- 📄 HTML export with folding

### New in CE
- 🌙 Dark Mode (FlatLaf)
- 🖥️ HiDPI/Retina support
- 🔍 Enhanced search
- 📱 Better multi-monitor support
- 🛡️ Modern security practices

## 🐛 Known Issues

1. **Tab Key Navigation**: On macOS, `TAB` is mapped to "Add Child". Use `Ctrl+Tab` for focus traversal.
2. **Legacy Plugins**: Some old plugins may need manual updates.
3. **Apple Silicon**: Rosetta 2 may be required for some native features.

## 📝 License

FreeMind CE is licensed under **GNU General Public License v2.0** (GPL-2.0), same as the original FreeMind.

```
Copyright (C) 2000-2026 Joerg Mueller, Daniel Polansky, Christian Foltin,
Dimitry Polivaev, Tolga Karatas (Denomas), and others.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
```

## 🙏 Acknowledgments

- Original [FreeMind](https://sourceforge.net/p/freemind/) team
- SourceForge community
- Apache Software Foundation (Batik, FOP)
- FlatLaf team

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/denomas/freemind-ce/issues)
- **Discussions**: [GitHub Discussions](https://github.com/denomas/freemind-ce/discussions)
- **Documentation**: `admin/docs/` folder

---

**Built with ❤️ by Denomas Engineering**
