# Contributing to FreeMind CE

Thank you for your interest in contributing to FreeMind CE! This guide will help you get started.

## Prerequisites

- **Java 21** (Temurin/Adoptium recommended)
- **Gradle 8.6+** (included via wrapper — use `./gradlew`)
- **Git** with pre-commit hooks

## Getting Started

```bash
# Clone the repository
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce

# Build, run, and test using Make (recommended)
make build    # Compile + test
make run      # Run FreeMind CE
make test     # Run tests only
make help     # Show all available targets
```

> **Note:** The Makefile sets `JAVA_HOME` and `--no-configuration-cache` automatically.
> If you prefer raw Gradle commands, see [docs/development-guide.md](docs/development-guide.md).

## Development Workflow

1. **Create a branch** from `main`
2. **Make your changes** following the code style below
3. **Run the build** — `./gradlew build --no-configuration-cache` must pass
4. **Run the app** — verify your changes visually if they affect the UI
5. **Commit** using Conventional Commits format
6. **Open a Pull Request** against `main`

## Commit Best Practices

### Conventional Commits

We use [Conventional Commits](https://www.conventionalcommits.org/) enforced by gitlint:

```
feat: add dark mode icon variants
fix: resolve crash when opening large maps
docs: update build instructions for Windows
refactor: extract HeadlessFreeMind from test mock
test: add coverage for XML round-trip serialization
build: upgrade FlatLaf to 3.5.0
ci: add OWASP dependency scanning
chore: update .gitignore patterns
```

**Allowed types:** `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`

### Atomic Commits

Each commit must represent **one logical change**. Do not bundle unrelated changes in a single commit. This makes code review, bisecting, and reverting significantly easier.

**Good — separate commits for each concern:**
```
refactor: extract HeadlessFreeMind from FreeMindMainMock
test: move tests from main to test sourceSet
fix: resolve locale-dependent date parsing in CalendarMarkingTests
ci: add Xvfb headless test step to build workflow
```

**Bad — one giant commit:**
```
feat: infrastructure modernization (tests, CI, release, docs)
```

### Commit Message Guidelines

- **Subject line:** max 72 characters, imperative mood ("add", "fix", "remove" — not "added", "fixed", "removed")
- **Body (optional):** explain **why** the change was made, not what changed (the diff shows that). Wrap at 72 characters.
- **Footer (optional):** reference issues with `Closes #123` or `Refs #456`
- **Breaking changes:** add `!` after the type, e.g., `feat!: remove legacy XML import`

### What Belongs in a Single Commit

| Scenario | Commits |
|---|---|
| New feature + tests for that feature | 1 commit (tests are part of the feature) |
| Bug fix + test that reproduces the bug | 1 commit |
| Refactor production code + update tests to match | 1 commit |
| CI config change + unrelated test fix | 2 separate commits |
| Multiple independent bug fixes | 1 commit per fix |
| New files (config, templates) for different systems | 1 commit per system (CI, release, repo templates) |

### Before Committing

1. **Build must pass:** `make build`
2. **Tests must pass:** `make test`
3. **Review your diff:** `git diff --staged` — make sure no debug code, secrets, or unrelated changes are included
4. **Stage intentionally:** use `git add <file>` for specific files, avoid `git add -A` or `git add .`
5. **No generated artifacts:** never commit `build/`, `*.class`, `auto.properties`, or IDE-specific files

## Serena Code Intelligence (Required)

This project uses [Serena](https://github.com/oraios/serena) as a semantic code analysis tool via MCP (Model Context Protocol). Serena provides LSP-powered symbol navigation, reference tracking, and impact analysis — ensuring every change is fully understood before it is committed.

### Why Serena?

- **Symbol-level precision:** Find all references to a method/class across the entire codebase
- **Impact analysis:** Understand what breaks when you change a symbol
- **Semantic editing:** Modify code at the symbol level, not just text search-and-replace
- **Token efficiency:** Read only the symbols you need, not entire files

### Setup (One-Time)

```bash
# 1. Install uv (Python package manager) if not already installed
curl -LsSf https://astral.sh/uv/install.sh | sh

# 2. Add Serena as MCP server for Claude Code (per-project)
claude mcp add serena -- uvx --from git+https://github.com/oraios/serena \
  serena start-mcp-server --context=claude-code --project-from-cwd

# 3. Create project config (already done — .serena/project.yml is versioned)
# Only needed if starting a new project:
# uvx --from git+https://github.com/oraios/serena serena project create --language java .

# 4. Index the codebase (recommended after major changes)
uvx --from git+https://github.com/oraios/serena serena project index .

# 5. Verify setup
uvx --from git+https://github.com/oraios/serena serena project health-check .
```

### Pre-Commit SOP (Standard Operating Procedure)

**Every developer must follow this checklist before committing:**

1. **Build passes:** `make build`
2. **Verify with Serena:** Use `find_symbol` and `find_referencing_symbols` to confirm:
   - All references to modified symbols still compile and work correctly
   - No orphaned references exist (dead code from renames/deletions)
   - Interface contracts are preserved (method signatures, return types)
3. **Run the app:** `make run` — visually verify UI changes
4. **Review log files:** Check `~/.freemind/log.0` for new SEVERE/WARNING entries
5. **Clean diff:** `git diff --staged` — no debug code, no unrelated changes

### Key Serena Tools for Code Review

| Tool | Use Case |
|------|----------|
| `find_symbol` | Locate a class, method, or field by name |
| `find_referencing_symbols` | Find all code that calls/uses a symbol |
| `get_symbols_overview` | Get a file's class/method structure without reading the entire file |
| `replace_symbol_body` | Replace an entire method/class definition precisely |
| `insert_after_symbol` / `insert_before_symbol` | Add code at exact positions |
| `search_for_pattern` | Regex search across the codebase (for non-code files too) |

### Example Workflow

```
# Before changing NodeAdapter.setNoteText():

1. find_symbol("NodeAdapter/setNoteText") → read current implementation
2. find_referencing_symbols("NodeAdapter/setNoteText") → see all callers
3. Make the change
4. find_referencing_symbols again → verify all callers still compatible
5. make build → compile + test
6. make run → visual verification
7. Commit
```

## Code Style

- Follow existing patterns in the codebase
- No `@SuppressWarnings` — fix root causes
- Never modify files in `generated-src/` — regenerate with `make jaxb`
- Never ignore `*.jar` in `.gitignore` — the project depends on ~90 tracked local JARs
- Preserve backward compatibility with existing `.mm` files

## Testing

- Tests use **JUnit 3** (`extends TestCase`) running under JUnit 5 vintage engine
- Test base class: `FreeMindTestBase` (provides mock FreeMindMain context)
- Run tests: `make test`
- Coverage report: `make coverage` (opens `freemind/build/reports/jacoco/test/html/index.html`)

## Project Structure

See [CLAUDE.md](CLAUDE.md) for detailed repository layout, key source locations, and architecture overview.

## Reporting Issues

- Use the [Bug Report](https://github.com/Denomas/freemind-ce/issues/new?template=bug_report.yml) template
- Include your OS, Java version, and FreeMind CE version
- Attach the `.mm` file if the issue is map-specific

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v2.0](LICENSE).
