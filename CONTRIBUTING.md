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

# Set up Java (macOS with Homebrew)
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"

# Build the project
./gradlew :freemind:build --no-configuration-cache

# Run the application
./gradlew :freemind:run --no-configuration-cache

# Run tests
./gradlew :freemind:test --no-configuration-cache
```

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

1. **Build must pass:** `./gradlew :freemind:build --no-configuration-cache`
2. **Tests must pass:** `./gradlew :freemind:test --no-configuration-cache`
3. **Review your diff:** `git diff --staged` — make sure no debug code, secrets, or unrelated changes are included
4. **Stage intentionally:** use `git add <file>` for specific files, avoid `git add -A` or `git add .`
5. **No generated artifacts:** never commit `build/`, `*.class`, `auto.properties`, or IDE-specific files

## Code Style

- Follow existing patterns in the codebase
- No `@SuppressWarnings` — fix root causes
- Never modify files in `generated-src/` — regenerate with `./gradlew :freemind:generateJaxb`
- Never ignore `*.jar` in `.gitignore` — the project depends on ~90 tracked local JARs
- Preserve backward compatibility with existing `.mm` files

## Testing

- Tests use **JUnit 3** (`extends TestCase`) running under JUnit 5 vintage engine
- Test base class: `FreeMindTestBase` (provides mock FreeMindMain context)
- Run tests: `./gradlew :freemind:test --no-configuration-cache`
- Coverage report: `./gradlew :freemind:jacocoTestReport --no-configuration-cache`

## Project Structure

See [CLAUDE.md](CLAUDE.md) for detailed repository layout, key source locations, and architecture overview.

## Reporting Issues

- Use the [Bug Report](https://github.com/Denomas/freemind-ce/issues/new?template=bug_report.yml) template
- Include your OS, Java version, and FreeMind CE version
- Attach the `.mm` file if the issue is map-specific

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v2.0](LICENSE).
