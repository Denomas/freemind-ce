# FreeMind CE - Project Documentation Index

> Generated: 2026-03-10 | Deep Scan | Source: Verified code analysis

## Project Overview

- **Name:** FreeMind Classic Edition (CE)
- **Version:** 1.1.0-CE
- **Type:** Monolith Desktop Application (Java Swing)
- **Primary Language:** Java 21
- **Architecture:** MVC with Mode-based decomposition + Hook/Plugin framework
- **Build System:** Gradle 8.6+ (Kotlin DSL)
- **Organization:** Denomas Engineering

## Quick Reference

- **Entry Point:** `freemind.main.FreeMindStarter`
- **Tech Stack:** Java 21, Swing, FlatLaf 3.4.1, JAXB 2.3.9, Batik 1.17, FOP 2.9
- **Architecture Pattern:** MVC + Action/Actor (undo/redo) + Hook/Plugin
- **Plugins:** SVG, Script, Map, Search, Help, Collaboration (Socket)
- **Platforms:** macOS, Windows, Linux
- **CI/CD:** GitHub Actions (multi-platform matrix build + jpackage)

## Documentation

- [Project Overview](./project-overview.md) — Vision, tech stack, modernization status
- [Architecture](./architecture.md) — System architecture, patterns, components, data model
- [Source Tree Analysis](./source-tree-analysis.md) — Directory structure, critical folders, entry points
- [Component Inventory](./component-inventory.md) — UI components, event handlers, plugins, extensions
- [Development Guide](./development-guide.md) — Prerequisites, build commands, testing, CI/CD

## Additional References

- [README.md](../README.md) — Project introduction and quick start
- [CLAUDE.md](../CLAUDE.md) — Agent & developer quick-reference guide

## Legacy Documentation

- `admin/docs/` — Original FreeMind documentation (HTML User Guide, feature docs, compilation guides, JavaHelp)
- `admin/installer/` — Legacy installer configurations (Mac DMG, RPM, Windows)

## Getting Started

```bash
# Build and run
./gradlew build
./gradlew :freemind:run

# Run tests
./gradlew test

# Create platform package
./gradlew :freemind:jpackageMac    # macOS .dmg
./gradlew :freemind:jpackageWin    # Windows .exe
./gradlew :freemind:jpackageLinux  # Linux .deb
```

## AI-Assisted Development

When using this documentation with AI tools:
1. Start with this `index.md` as the entry point
2. Reference `architecture.md` for understanding system design
3. Reference `component-inventory.md` for finding specific components
4. Reference `development-guide.md` for build/test/deploy workflows
5. Reference `source-tree-analysis.md` for navigating the codebase

## Current Focus

See the [Active Tech Spec](../_bmad-output/implementation-artifacts/tech-spec-freemind-ce-next-gen-modernization.md) for the full implementation plan and roadmap.

Next milestone: **v1.1.x — Infrastructure, DX & Automation**
