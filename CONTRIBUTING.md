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

### Who Can Contribute

> Workflow diagram: [docs/contributor-workflows.md — Section 2](docs/contributor-workflows.md#section-2-contributor-type-workflows)

| Contributor Type | Access | How to Submit | CI Behavior | Review Required |
|-----------------|--------|---------------|-------------|-----------------|
| **External (open source)** | None — fork the repo | PR from fork | CI runs, secrets NOT available | Maintainer approval |
| **Collaborator** | Write — invited by maintainer | PR from repo branch | CI runs, full access | Maintainer or peer approval |
| **Maintainer** | Admin | PR from repo branch | CI runs, full access | Self-review (CI is the gate) |
| **AI Agent** (Claude Code, etc.) | Via maintainer's credentials | PR from repo branch | CI runs, full access | Maintainer MUST review the diff |
| **Bots** (Dependabot, release-please) | App token | Auto-generated PR | CI runs automatically | Maintainer approval for merge |

### For External Contributors (Fork Workflow)

```bash
# 1. Fork on GitHub UI, then clone your fork
git clone https://github.com/YOUR-USERNAME/freemind-ce.git
cd freemind-ce

# 2. Add upstream remote
git remote add upstream https://github.com/Denomas/freemind-ce.git

# 3. Create feature branch
git checkout -b feat/your-feature

# 4. Make changes, build, and test
make build    # Must pass before committing

# 5. Push to your fork and open PR
git push -u origin feat/your-feature
# Then open PR on GitHub: YOUR-USERNAME/freemind-ce → Denomas/freemind-ce:main
```

> **Note for fork PRs:** CI secrets are NOT available in fork PR runs. This is a GitHub security measure. All tests still run normally since they don't require secrets.

### For Collaborators and Maintainer

```bash
# Clone directly (you have write access)
git clone https://github.com/Denomas/freemind-ce.git
cd freemind-ce

# Create feature branch (never commit directly to main)
git checkout -b feat/your-feature

# Make changes, build, and test
make build

# Push and create PR
git push -u origin feat/your-feature
gh pr create --title "feat: your feature" --body "Description"
```

### All Changes Go Through Pull Requests

**Direct push to main is BLOCKED for everyone** — maintainer included. This is enforced by GitHub Ruleset with an empty bypass list. There are no exceptions, no hotfix bypasses, no admin overrides.

> Blocked vs Allowed diagram: [docs/contributor-workflows.md — Section 3](docs/contributor-workflows.md#section-3-blocked-actions)

### Hotfix / Emergency Fix Workflow

There is NO shortcut for emergencies. The fastest path is still a PR:

> Hotfix flow diagram: [docs/contributor-workflows.md — Section 4](docs/contributor-workflows.md#section-4-hotfix-flow)

> Total time from bug discovery to merge: **~12 minutes.** This is fast enough. Never bypass CI.

### First-Time Contributor Flow

> First-time contributor diagram: [docs/contributor-workflows.md — Section 2](docs/contributor-workflows.md#first-time-contributor-flow)

### Conflict Resolution Flow

> Conflict resolution diagram: [docs/contributor-workflows.md — Section 5](docs/contributor-workflows.md#section-5-conflict-resolution)

### Bot PR Lifecycle

> Bot lifecycle diagram: [docs/contributor-workflows.md — Section 8](docs/contributor-workflows.md#section-8-bot-lifecycle)

### AI Agent Workflow

> AI agent workflow diagram: [docs/contributor-workflows.md — Section 2](docs/contributor-workflows.md#for-ai-agents)

### Security Incident: Secret Leaked in PR

> Security incident diagram: [docs/contributor-workflows.md — Section 11](docs/contributor-workflows.md#section-11-security-incident)

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
3. **Review your diff:** `git diff --staged` — make sure no debug code, secrets, or unrelated changes are included
4. **Stage intentionally:** use `git add <file>` for specific files, avoid `git add -A` or `git add .`
5. **No generated artifacts:** never commit `build/`, `*.class`, `auto.properties`, or IDE-specific files

### After Pushing: Automated Code Review

GitHub automated code review (github-code-quality) analyzes every commit and posts findings as PR comments. These are NOT suggestions — they identify real issues: unused variables, inefficient patterns, dead code, security findings.

**Every automated review comment MUST be fixed before merge:**

1. **Check PR comments** after each push: `gh api repos/OWNER/REPO/pulls/NUMBER/comments`
2. **Fix each finding** in a new atomic commit
3. **Push and verify** the comment is resolved
4. **Never ignore or dismiss** automated findings — they catch real bugs that humans miss

This applies to ALL contributors including AI agents. If an agent pushes code and gets automated findings, it must fix them in the same session.

## Serena Code Intelligence (MANDATORY)

> **This is NOT optional.** Serena usage is a hard requirement for every developer, every AI agent, and every subagent task. No code change may be committed without Serena verification.

> **Full guide:** [docs/serena-guide.md](docs/serena-guide.md) — 18 tools, workflow diagrams, decision trees, examples

### Why Serena?

- **Symbol-level precision:** Find all references to a method/class across the entire codebase
- **Impact analysis:** Understand what breaks when you change a symbol
- **Semantic editing:** Modify code at the symbol level, not just text search-and-replace
- **Token efficiency:** Read only the symbols you need, not entire files

### Mandatory Usage Rules

1. **Before any code change:** Use `get_symbols_overview` and `find_symbol` to understand the current structure
2. **Before committing:** Use `find_referencing_symbols` to verify all references remain valid
3. **AI agents and subagents:** Must use Serena tools as the first step in every analysis task — non-negotiable
4. **Never skip Serena:** Even for "simple" changes — every change has potential impact that Serena can detect

### Pre-Commit SOP

1. **Build passes:** `make build`
2. **Verify with Serena:** `find_referencing_symbols` — all references intact, no orphans
3. **Run the app:** `make run` — visually verify UI changes
4. **Clean diff:** `git diff --staged` — no debug code, no unrelated changes

> **Complete Serena reference:** [docs/serena-guide.md](docs/serena-guide.md) — 18 tools, setup, workflow diagrams, decision trees, examples, anti-patterns

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
      → Pre-commit hook (compile verification)
        → git push
          → GitHub code-quality (automated PR review)
            → CI 48-job matrix (6 OS × 4 Java)
```

Every layer catches different things. No layer is redundant. Skipping any layer means bugs slip through.

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

- Tests use **JUnit 3** (`extends TestCase`) running under JUnit 5 vintage engine
- Test base class: `FreeMindTestBase` (provides mock FreeMindMain context)
- GUI tests extend `GuiTestBase` (AssertJ Swing, `@Tag("gui")`, automatic screenshots)
- Property-based tests use jqwik with centralized generators in `MindmapGenerators`
- Run tests: `make test` (unit) / `make test-gui` (GUI)
- Coverage report: `make coverage` (opens `freemind/build/reports/jacoco/test/html/index.html`)

## CI/CD Standard Operating Procedure (SOP)

### 1. Zero-Tolerance Policy

ALL tests MUST pass on ALL platforms and ALL Java versions before merge or release.
No exceptions. No manual overrides. No `continue-on-error`.
A single failure on any combination blocks the entire pipeline.

### 2. Test Matrix

Every PR and every release runs on this complete matrix:

|  | ubuntu-24.04 | ubuntu-22.04 | win-2025 | win-2022 | macos-15 | macos-14 |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Java 21 (LTS)** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Java 22** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Java 23** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Java 24** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

**Total: 48 required checks** (24 Build + 24 GUI Tests).

> **Note:** `macos-13` was removed because GitHub Actions discontinued the runner image.

Runner images: [actions/runner-images](https://github.com/actions/runner-images)

### 3. CI Jobs & Path Filtering

| Job | Purpose | Runs on doc-only PR? |
|---|---|---|
| `Detect changes` | Determines if PR contains code changes (pure `git diff` with negated pathspecs) | **Always** |
| `Build (os, java)` | Compile + unit tests + SpotBugs (24 matrix combinations) | **Skipped** |
| `GUI Tests (os, java)` | GUI integration tests + screenshots (24 matrix combinations) | **Skipped** |
| `CI` | Aggregator — evaluates all job results, single required check in GitHub Ruleset | **Always** |

**Path filtering** skips the 48-job build/test matrix when a PR only changes documentation files:

- `**/*.md`, `docs/**`
- `LICENSE`, `COPYING`, `.gitattributes`
- `.github/ISSUE_TEMPLATE/**`, `.github/PULL_REQUEST_TEMPLATE/**`, `.github/release-notes-template.md`

Non-PR events (`push` to main, `workflow_call` from release-please) **always run the full matrix**.

**CI flow diagram:** [docs/contributor-workflows.md — Section 6](docs/contributor-workflows.md#section-6-ci-pipeline)

**Path filtering detection method:**

The `Detect changes` job uses `git merge-base` to compute the common ancestor between the PR branch and main, then checks only the PR's own commits (not commits merged into main since the branch was created). This prevents false positives when main has recent merges.

```bash
# Correct: merge-base ensures only PR's own changes are checked
BASE=$(git merge-base "$PR_BASE_SHA" "$PR_HEAD_SHA")
git diff --name-only "$BASE..$HEAD" -- ':!**/*.md' ':!docs/**' ...
```

**Doc-only file patterns** (changes to ONLY these files skip the build matrix):

| Pattern | Examples |
|---------|---------|
| `**/*.md` | `README.md`, `CONTRIBUTING.md`, `CLAUDE.md`, `docs/*.md` |
| `docs/**` | Any file under `docs/` directory |
| `LICENSE`, `COPYING`, `.gitattributes` | License and git config files |
| `.github/ISSUE_TEMPLATE/**` | Issue templates |
| `.github/PULL_REQUEST_TEMPLATE/**` | PR templates |
| `.github/release-notes-template.md` | Release notes template |

Any other file change (`.java`, `.kts`, `.yml`, `.xml`, `.properties`, etc.) triggers the full 48-job matrix.

### 4. Branch Workflow

> PR lifecycle diagram: [docs/contributor-workflows.md — Section 7](docs/contributor-workflows.md#section-7-branch-workflow)

- **All changes** via feature branch → Pull Request → main
- **No direct push to main** (enforced by GitHub Ruleset)
- PR requires: `CI` check pass + code review (single required check, not 48 individual)
- Squash merge only — clean linear history on main

### 5. GUI Test Requirements

- Every new UI feature **MUST** include GUI tests before merge
- Tests must capture screenshots for visual verification on every platform
- Tests must cover: happy path, error path, edge cases
- No UI change is too small to test
- GUI test base class: `GuiTestBase` (AssertJ Swing, automatic screenshots)

### 6. Release Gating

> Release pipeline diagram: [docs/contributor-workflows.md — Section 10](docs/contributor-workflows.md#section-10-release-gating)

**Any failure at any stage blocks the release completely.** There is no manual override.

> **Note:** `release-please.yml` and `scorecard.yml` use `paths-ignore` on push triggers.
> Doc-only pushes to main skip these workflows entirely. The next code push catches up
> because release-please accumulates all commits since the last release.

### 7. Scheduled Workflow Health (MANDATORY)

> **This check is MANDATORY before every release and after every major change.**
> A green PR CI does NOT mean all workflows are healthy — scheduled workflows run independently.

The following workflows run on a schedule, NOT on every PR. They can break silently:

| Workflow | Schedule | What it checks | How to verify |
|----------|----------|---------------|---------------|
| `security-scan.yml` | Weekly (Mon 06:00 UTC) | OWASP dependency vulns + CodeQL | `gh run list --workflow=security-scan.yml --limit 1` |
| `scorecard.yml` | Every push to main | OpenSSF security scorecard | `gh run list --workflow=scorecard.yml --limit 1` |
| `fuzz.yml` | On PR (if enabled) | Fuzzing tests | `gh run list --workflow=fuzz.yml --limit 1` |
| `stale.yml` | Daily | Stale issue/PR cleanup | `gh run list --workflow=stale.yml --limit 1` |

**Verification command (run before every release):**

```bash
# Check ALL workflow health — every line must show "success"
for wf in security-scan.yml scorecard.yml fuzz.yml stale.yml build.yml release-please.yml; do
  result=$(gh run list --workflow=$wf --limit 1 --json conclusion --jq '.[0].conclusion // "no runs"' 2>/dev/null)
  echo "$wf: $result"
done
```

**If any scheduled workflow is failing:**
1. Investigate the failure logs: `gh run view <run-id> --log-failed`
2. Fix the root cause (do NOT disable the workflow)
3. Trigger a manual re-run: `gh workflow run <workflow-name>`
4. Verify it passes before proceeding with the release

**Common scheduled workflow failures:**
- **CodeQL "no source code seen"** → add `clean` to the Gradle build command so CodeQL sees fresh compilation
- **OWASP timeout** → increase timeout or add `continue-on-error: true` (already set)
- **Scorecard API errors** → transient, re-run usually fixes

### 8. New Runner/Java Version Procedure

When a new GitHub Actions runner image or Java GA version becomes available:

1. Add to matrix in `build.yml` and `release.yml`
2. No ruleset update needed — single `CI` aggregator check covers all matrix combinations automatically
3. Update the SOP table above
4. Verify all existing tests pass on the new combination
5. Fix any compatibility issues before merging

New Java GA versions must be added within 30 days of release.
New runner images must be added within 30 days of availability.

### 9. Test Philosophy

> "Being lazy in writing tests means facing much bigger workloads later.
> Our goal is to reduce future workload by testing every user scenario,
> every edge case, every state, comprehensively, now."

### 10. Merge Safety Protocol

> **Full protocol:** [docs/merge-release-safety.md](docs/merge-release-safety.md)

Every PR merge requires ALL gates to pass — no exceptions, no bypass:

- **CI Matrix:** ALL 48 jobs SUCCESS (not 47/48 — every single one)
- **Review:** Maintainer approval required for ALL PRs (including Dependabot)
- **Up-to-date:** Branch must be synced with main
- **Conversations:** All review threads resolved

**STRICTLY FORBIDDEN:** `--admin` bypass, auto-merge without explicit maintainer instruction, merging with any failing check, force push to main. See [docs/merge-release-safety.md](docs/merge-release-safety.md) for the complete forbidden actions list.

### 11. Dependency Update Protocol

> **Full protocol:** [docs/merge-release-safety.md](docs/merge-release-safety.md#dependency-update-protocol)

> Dependency decision tree diagram: [docs/contributor-workflows.md — Section 9](docs/contributor-workflows.md#section-9-dependency-updates)

- **Patch** (x.x.1→x.x.2): CI passes + changelog review + maintainer approve
- **Minor** (x.1→x.2): Above + local `make build` verification
- **Major** (1.x→2.x): Above + migration guide + API fix + `make run` smoke test

CI passing alone is NEVER sufficient. Human review is always required.

### 12. Release Checklist

> **Full checklist:** [docs/merge-release-safety.md](docs/merge-release-safety.md#release-checklist)

Before merging any release-please PR: all CI green, `make build` + `make run` locally, CHANGELOG review, artifact download and verification. See the full checklist in the referenced document.

## Project Structure

See [CLAUDE.md](CLAUDE.md) for detailed repository layout, key source locations, and architecture overview.

## Reporting Issues

- Use the [Bug Report](https://github.com/Denomas/freemind-ce/issues/new?template=bug_report.yml) template
- Include your OS, Java version, and FreeMind CE version
- Attach the `.mm` file if the issue is map-specific

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v2.0](LICENSE).
