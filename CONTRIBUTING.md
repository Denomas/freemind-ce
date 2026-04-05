# Contributing to FreeMind CE

Thank you for your interest in contributing to FreeMind CE! This guide will help you get started.

> **MANDATORY READING:** Before starting any work, you MUST read this entire document AND the referenced documents in `docs/`. These are the project's binding rules — not suggestions.

| Document | Purpose | When to Read |
|----------|---------|-------------|
| **This file (CONTRIBUTING.md)** | Project rules, workflows, conventions (entry point) | Always — before any work |
| **[docs/contributor-workflows.md](docs/contributor-workflows.md)** | All workflow diagrams (16 mermaid): CI, merge, release, security | Before any PR, merge, or release |
| **[docs/serena-guide.md](docs/serena-guide.md)** | Serena tool reference (18 tools), decision trees, examples | Before any code analysis or editing |
| **[docs/merge-release-safety.md](docs/merge-release-safety.md)** | Merge protocols, dependency updates, release checklist | Before any merge, review, or release |
| **[docs/development-guide.md](docs/development-guide.md)** | Build, test, debug, package | Before writing code |
| **[docs/architecture.md](docs/architecture.md)** | MVC, modes, action framework, plugin system | Before design decisions |
| **[docs/component-inventory.md](docs/component-inventory.md)** | UI components, plugin registry | Before finding/modifying specific code |
| **[docs/source-tree-analysis.md](docs/source-tree-analysis.md)** | Directory structure, file locations | Before navigating the codebase |

## Project Philosophy

> FreeMind CE exists because we love FreeMind as it is. We modernize the infrastructure, never the soul.

This project brings the original FreeMind back to life on modern platforms. Every contribution must respect these principles:

| Principle | What It Means |
|-----------|--------------|
| **Preserve the original** | Every feature, every icon, every behavior of the original FreeMind must be preserved. We change nothing — we only modernize the infrastructure. |
| **Full backward compatibility** | Every `.mm` file ever created by any version of FreeMind must open correctly. We NEVER break file format compatibility. There are no exceptions. |
| **No breaking changes** | The `feat!:` (breaking change) commit type is FORBIDDEN. We do not use `BREAKING CHANGE` footers. We do not trigger major version bumps. If a change would break backward compatibility, we find another way or we don't do it. |
| **User content is sacred** | FreeMind is a content creation tool. We NEVER sanitize, filter, block, or modify user content. Script tags, file paths, SQL strings, emoji — whatever the user types is preserved exactly as written through every save/load/export cycle. |
| **Simplicity over features** | FreeMind's power is its simplicity. We do not add unnecessary complexity, unnecessary dependencies, or unnecessary features. Every addition must earn its place. |

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

All contributor types, workflows, fork/collaborator setup, hotfix flow, conflict resolution, bot lifecycles, AI agent workflow, and security incident procedures are documented in **one place**:

> **[docs/contributor-workflows.md](docs/contributor-workflows.md)** — 16 workflow diagrams, contributor types, step-by-step guides for every scenario.

## Parallel Work Protection

When multiple developers, AI agents, or sessions work on the same repository simultaneously, each person's work is sovereign. These rules prevent one session from destroying, absorbing, or invalidating another's work.

### PR Sovereignty

| Rule | Description |
|------|-------------|
| **PW-1** | A PR may only be closed by: (a) its original author, (b) the repository maintainer, or (c) the stale bot after 60+14 days. No other party may close a PR. |
| **PW-2** | An AI agent MUST NEVER close, comment "superseded", or request closure of a PR it did not create. |
| **PW-3** | Closing a PR requires a comment explaining: why it's being closed, what happens to the work, and a link to any successor PR. |
| **PW-4** | If a PR is closed in favor of another, the successor MUST reference the original with `Incorporates #NN` or `Replaces #NN` and explain what was preserved vs. dropped. |

### No Unilateral Scope Absorption

| Rule | Description |
|------|-------------|
| **PW-5** | No contributor or AI agent may claim their PR "supersedes" another open PR. Only the maintainer can make that judgment. |
| **PW-6** | If two PRs overlap in scope, both remain open for independent review. The maintainer decides merge order and conflict resolution. |
| **PW-7** | Cherry-picking or copying commits from another open PR into your branch is forbidden without explicit maintainer approval. |
| **PW-8** | If you discover an overlapping open PR: (1) note "Related: #NN" in your PR description, (2) do NOT absorb it, (3) let the maintainer decide. |

### Branch and Commit Integrity

| Rule | Description |
|------|-------------|
| **PW-9** | Force-pushing to a branch with an open PR from another session/author is forbidden. Use `--force-with-lease` only on your own branches. |
| **PW-10** | Squashing multiple independent PRs into one before merge is forbidden. Each unit of work gets its own PR, CI run, and review thread. |
| **PW-11** | Every commit must be attributable. AI agent PRs must state which agent/session produced the work. |

### AI Agent Multi-Session Rules

| Rule | Description |
|------|-------------|
| **PW-12** | Each AI session operates in its own branch and PR. Sessions MUST NOT interact with each other's PRs (no comments, closures, reviews, or status changes). |
| **PW-13** | When an AI agent discovers another open PR during work, the ONLY permitted actions are: (a) mention "Related: #NN" in your own PR, (b) inform the human user. The agent MUST NOT act on the other PR. |
| **PW-14** | AI agents MUST NOT use `gh pr close`, `gh pr comment`, or `gh pr edit` on PRs they did not create. |

> **Overlap workflow diagram:** [docs/contributor-workflows.md — Section 15](docs/contributor-workflows.md#section-15-parallel-work)

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

**Allowed types:** `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`, `deps`

> **Note:** PR titles are automatically validated against this format. PRs with non-conforming titles will fail the CI check.

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
- **Breaking changes (`feat!:`):** NEVER USE. This project preserves full backward compatibility with every `.mm` file ever created. We do not break the past — see [Project Philosophy](#project-philosophy) below.

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
3. **Security audit (if dependencies changed):** `make audit` — fails on High+ CVEs with known fixes
4. **Review your diff:** `git diff --staged` — make sure no debug code, secrets, or unrelated changes are included
5. **Stage intentionally:** use `git add <file>` for specific files, avoid `git add -A` or `git add .`
6. **No generated artifacts:** never commit `build/`, `*.class`, `auto.properties`, or IDE-specific files

### After Pushing: Automated Code Review

GitHub automated code review (github-code-quality) analyzes every commit and posts findings as PR comments. These are NOT suggestions — they identify real issues: unused variables, inefficient patterns, dead code, security findings.

**Every automated review comment MUST be fixed before merge:**

1. **Check PR comments** after each push: `gh api repos/OWNER/REPO/pulls/NUMBER/comments`
2. **Fix each finding** in a new atomic commit
3. **Push and verify** the comment is resolved
4. **Never ignore or dismiss** automated findings — they catch real bugs that humans miss

This applies to ALL contributors including AI agents. If an agent pushes code and gets automated findings, it must fix them in the same session.

## Serena Code Intelligence (MANDATORY)

Serena is a hard requirement for every developer, every AI agent, and every subagent. No code change may be committed without Serena verification. All rules, workflows, tool reference, anti-patterns, and setup instructions are in **one place**:

> **[docs/serena-guide.md](docs/serena-guide.md)** — 18 tools, mandatory workflow, decision trees, examples, anti-patterns, pre-commit verification flow.

## Security Audit (Vulnerability Scanning)

Every dependency — whether declared in Gradle or tracked as a local JAR — must be free of known high-severity vulnerabilities before release.

### Core Principle

> **No external API dependency for security tooling.** If a tool can run as a local CLI, we use the CLI. We do not depend on cloud services, paid APIs, or third-party dashboards for security decisions. All scanning runs offline after the initial database download.

### Tools

| Tool | Where | Command | What It Catches | Speed |
|------|-------|---------|----------------|-------|
| **Grype** | Local + GitHub | `make audit` | Known CVEs in JARs and Gradle dependencies (with fix versions) | ~30 seconds |
| **OWASP Dependency-Check** | Local + GitHub | `make audit-full` | Same + detailed HTML report with NVD references | ~5 minutes |
| **Trivy** | GitHub only | `security-scan.yml` | Filesystem vulnerability scan, SARIF upload | ~2 minutes |

### Local Workflow

```bash
# Quick scan — run before pushing (fails on High+ CVEs with known fixes)
make audit

# Full report — run periodically or before releases
make audit-full
open freemind/build/reports/dependency-check-report.html
```

### Handling Findings

| Severity | Action | Timeline |
|----------|--------|----------|
| **Critical/High** (fix available) | Update dependency immediately | Before next merge |
| **Critical/High** (no fix) | Add OWASP suppression with justification | Document in `config/owasp-suppressions.xml` |
| **Medium** | Create GitHub issue, fix in next sprint | Before next release |
| **Low** | Track, fix opportunistically | No deadline |

### What Gets Scanned

- Gradle dependencies (Maven Central)
- Tracked JAR files in `freemind/lib/`, `freemind/plugins/*/`
- Build tool dependencies (PMD, SpotBugs, JaCoCo — build-time only, not runtime)
- Transitive dependencies

### Prerequisites

Install Grype via [mise](https://mise.jdx.dev/):

```bash
mise install grype
```

## Static Analysis (Quality Gates)

Every `make build` runs two static analysis tools automatically. These are **not optional** — they are the first line of defense against bugs.

### Tools

| Tool | Version | What It Catches | Config |
|------|---------|----------------|--------|
| **SpotBugs** | 6.4.8 | Null dereference, dead store, concurrency bugs, resource leaks | `config/spotbugs-exclude.xml` |
| **PMD** | 7.21.0 | Unused variables, empty catch blocks, inefficient patterns, design issues, security | `config/pmd-ruleset.xml` |

### PMD Configuration

- **All 7 categories active:** bestpractices, errorprone, performance, codestyle, design, multithreading, security
- **294 rules**, all priority levels (1-5)
- **Ruleset:** `freemind/config/pmd-ruleset.xml` — full categories, no exclusions
- **Reports:** `freemind/build/reports/pmd/main.html` (source) and `test.html` (tests)
- **Current status:** `ignoreFailures = true` due to legacy violations. Goal: zero violations, then switch to `false`

### SpotBugs Configuration

- **Confidence:** HIGH (minimum)
- **Exclude filter:** `freemind/config/spotbugs-exclude.xml`
- **Fails build:** Yes (`ignoreFailures = false`)
- **Reports:** `freemind/build/reports/spotbugs/` (HTML)

### Early Detection Chain

The goal is to catch issues as early as possible, at the closest point to where code is written:

```
Code written
  → Serena MCP (semantic analysis, reference checking)
    → PMD + SpotBugs (make build — static analysis)
      → Grype (make audit — dependency vulnerability scan)
        → Pre-commit hook (compile verification)
          → git push
            → GitHub code-quality (automated PR review)
              → CI 48-job matrix (6 OS × 4 Java)
                → Grype + Trivy + OWASP (weekly security-scan.yml)
```

Every layer catches different things. No layer is redundant. Skipping any layer means bugs slip through.

**Local layers** (developer machine): Serena → PMD/SpotBugs → Grype → pre-commit hooks
**Remote layers** (GitHub): code-quality → CI matrix → weekly security scans

### Viewing Reports

```bash
make build          # Runs PMD + SpotBugs automatically
# PMD reports:
open freemind/build/reports/pmd/main.html
open freemind/build/reports/pmd/test.html
# SpotBugs reports:
open freemind/build/reports/spotbugs/main.html
```

## Code Style

- Follow existing patterns in the codebase
- No `@SuppressWarnings` — fix root causes
- Never modify files in `generated-src/` — regenerate with `make jaxb`
- Never ignore `*.jar` in `.gitignore` — the project depends on ~90 tracked local JARs
- **Preserve backward compatibility** — every `.mm` file ever created must open correctly (see [Project Philosophy](#project-philosophy))

## Testing

Test framework, commands, base classes, and test structure are documented in **one place**:

> **[docs/development-guide.md — Testing](docs/development-guide.md#testing)** — JUnit, GUI tests, property-based tests, fuzz tests, coverage.

## CI/CD Standard Operating Procedure (SOP)

### Zero-Tolerance Policy

ALL tests MUST pass on ALL platforms (6 OS × 4 Java = 48 checks) before merge or release. No exceptions. No manual overrides. A single failure blocks the entire pipeline.

### GUI Test Requirements

- Every new UI feature **MUST** include GUI tests before merge
- Tests must capture screenshots for visual verification on every platform
- Tests must cover: happy path, error path, edge cases
- GUI test base class: `GuiTestBase` (AssertJ Swing, automatic screenshots)

### Test Philosophy

> "Being lazy in writing tests means facing much bigger workloads later.
> Our goal is to reduce future workload by testing every user scenario,
> every edge case, every state, comprehensively, now."

### Detailed CI/CD Documentation

CI pipeline details, path filtering, test matrix, branch workflow, release gating, scheduled workflow health, and runner/Java version procedures are documented in dedicated files:

> **[docs/contributor-workflows.md](docs/contributor-workflows.md)** — CI pipeline flow, path filtering, doc-only detection, branch lifecycle, release gating diagrams.

> **[docs/merge-release-safety.md](docs/merge-release-safety.md)** — Merge protocol, forbidden actions, dependency update protocol (patch/minor/major), release checklist, GitHub Ruleset configuration.

## Project Structure

> **[docs/source-tree-analysis.md](docs/source-tree-analysis.md)** — Full directory structure, critical folders, entry points, source statistics.

## Reporting Issues

- Use the [Bug Report](https://github.com/Denomas/freemind-ce/issues/new?template=bug_report.yml) template
- Include your OS, Java version, and FreeMind CE version
- Attach the `.mm` file if the issue is map-specific

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v2.0](LICENSE).
