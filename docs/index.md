# FreeMind CE - Project Documentation Index

## Project Overview

- **Name:** FreeMind Classic Edition (CE)
- **Type:** Monolith Desktop Application (Java Swing)
- **Primary Language:** Java 21
- **Architecture:** MVC with Mode-based decomposition + Hook/Plugin framework
- **Build System:** Gradle 8.6+ (Kotlin DSL)

## Quick Reference

- **Entry Point:** `freemind.main.FreeMindStarter`
- **Tech Stack:** Java 21, Swing, FlatLaf 3.7, JAXB 2.3.9, Batik 1.19, FOP 2.11
- **Architecture Pattern:** MVC + Action/Actor (undo/redo) + Hook/Plugin
- **Plugins:** SVG, Script, Map, Search, Help, Context Graph, Collaboration (Socket)
- **Platforms:** macOS, Windows, Linux
- **CI/CD:** GitHub Actions (6 OS × 4 Java versions = 48 checks, path filtering, single `CI` required check)

## Documentation

- [Project Overview](./project-overview.md) — Vision, tech stack, modernization status
- [Architecture](./architecture.md) — System architecture, patterns, components, data model
- [Source Tree Analysis](./source-tree-analysis.md) — Directory structure, critical folders, entry points
- [Component Inventory](./component-inventory.md) — UI components, event handlers, plugins, extensions
- [Development Guide](./development-guide.md) — Prerequisites, build commands, testing, CI/CD

## Additional References

- [README.md](../README.md) — Project introduction and quick start
- [CLAUDE.md](../CLAUDE.md) — Agent & developer quick-reference guide
- [CONTRIBUTING.md](../CONTRIBUTING.md) — Contribution guidelines, CI/CD SOP, Serena reference

## Getting Started

```bash
# Clone and build
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce

# Build and run using Make (recommended)
make build    # Compile + test
make run      # Run FreeMind CE
make help     # Show all available targets
```

## AI-Assisted Development

When using this documentation with AI tools:
1. Start with [CLAUDE.md](../CLAUDE.md) as the primary entry point
2. Reference `architecture.md` for understanding system design
3. Reference `component-inventory.md` for finding specific components
4. Reference `development-guide.md` for build/test/deploy workflows
5. Reference `source-tree-analysis.md` for navigating the codebase

## Current Focus

See the [Active Tech Spec](../_bmad-output/implementation-artifacts/tech-spec-freemind-ce-next-gen-modernization.md) for the full implementation plan and roadmap.
