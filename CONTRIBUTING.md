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

## Serena Code Intelligence (MANDATORY)

> **This is NOT optional.** Serena usage is a hard requirement for every developer, every AI agent, and every subagent task. No code change may be committed without Serena verification. All analysis work must start with Serena's symbolic tools before any file reads or edits.

This project uses [Serena](https://github.com/oraios/serena) as a semantic code analysis tool via MCP (Model Context Protocol). Serena provides LSP-powered symbol navigation, reference tracking, and impact analysis — ensuring every change is fully understood before it is committed.

### Why Serena?

- **Symbol-level precision:** Find all references to a method/class across the entire codebase
- **Impact analysis:** Understand what breaks when you change a symbol
- **Semantic editing:** Modify code at the symbol level, not just text search-and-replace
- **Token efficiency:** Read only the symbols you need, not entire files

### Mandatory Usage Rules

1. **Before any code change:** Use `get_symbols_overview` and `find_symbol` to understand the current structure
2. **Before committing:** Use `find_referencing_symbols` to verify all references remain valid
3. **AI agents and subagents:** Must use Serena tools as the first step in every analysis task — this is non-negotiable
4. **Never skip Serena:** Even for "simple" changes — every change has potential impact that Serena can detect

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

### Complete Serena Tool Reference (18 Tools)

#### Category 1: Code Exploration & Analysis

| Tool | Purpose | Key Parameters |
|------|---------|----------------|
| `get_symbols_overview` | Get a file's class/method/field structure without reading the entire file. **Always call this first** when exploring a new file. | `relative_path` (required), `depth` (0=top-level only, 1=include children like methods of a class) |
| `find_symbol` | Locate a class, method, or field by name path pattern. Supports simple names (`"setNoteText"`), relative paths (`"NodeAdapter/setNoteText"`), and absolute paths (`"/NodeAdapter/setNoteText"`). Can include source body and hover info. | `name_path_pattern` (required), `include_body` (read source), `include_info` (docstring/signature), `depth` (descendants), `substring_matching` (partial match), `relative_path` (restrict scope), `include_kinds`/`exclude_kinds` (filter by LSP kind) |
| `find_referencing_symbols` | Find **all code that references a symbol** — callers, usages, imports. Returns code snippets around each reference with symbolic metadata. **Critical for impact analysis.** | `name_path` (required), `relative_path` (file path, required — must be a file, not directory) |
| `search_for_pattern` | Flexible regex search across the entire codebase including non-code files (XML, YAML, properties, HTML). Supports DOTALL mode (`.` matches newlines). Use for finding patterns that aren't code symbols. | `substring_pattern` (regex, required), `relative_path` (restrict to dir/file), `restrict_search_to_code_files`, `paths_include_glob`/`paths_exclude_glob`, `context_lines_before`/`context_lines_after` |
| `list_dir` | List files and directories. Use to understand project structure. | `relative_path` (required), `recursive` (required), `skip_ignored_files` |
| `find_file` | Find files by name or wildcard mask (e.g., `*.java`, `*Controller*`). Only searches non-gitignored files. | `file_mask` (required), `relative_path` (required — use `.` for root) |

#### Category 2: Code Editing (Symbol-Level Precision)

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `replace_symbol_body` | Replace the **entire body** of a symbol (method, class, function). The body includes the signature line but NOT preceding comments/docstrings/imports. | When you need to rewrite an entire method or class. First retrieve with `find_symbol(include_body=True)` to see current body. |
| `insert_after_symbol` | Insert new code **after** a symbol's definition. | Adding a new method after an existing one, appending code at end of file (use last top-level symbol). |
| `insert_before_symbol` | Insert new code **before** a symbol's definition. | Adding imports before the first symbol, inserting a new method before an existing one. |
| `rename_symbol` | Rename a symbol **across the entire codebase** using LSP rename. Handles all references automatically. For Java overloaded methods, include signature in name_path. | When renaming a class, method, field, or variable. All references updated atomically. |

#### Category 3: Memory System (Project Knowledge Persistence)

| Tool | Purpose | Example |
|------|---------|---------|
| `write_memory` | Save project knowledge to named memory files (persists across sessions). Use `/` in names for topic organization. Prefix with `global/` for cross-project memories. | `write_memory("auth/login_flow", "The login flow uses...")` |
| `read_memory` | Read a previously saved memory by name. Only read if relevant to current task. | `read_memory("suggested_commands")` |
| `list_memories` | List all available memories, optionally filtered by topic. | `list_memories(topic="auth")` |
| `edit_memory` | Edit memory content using literal string or regex replacement. | `edit_memory("overview", "old text", "new text", mode="literal")` |
| `delete_memory` | Delete a memory. Only when explicitly instructed. | `delete_memory("obsolete/old_info")` |
| `rename_memory` | Rename or move a memory to a different topic. | `rename_memory("old_name", "new/organized_name")` |

#### Category 4: Project Management

| Tool | Purpose |
|------|---------|
| `check_onboarding_performed` | Check if project onboarding has been done. Call at start of every new conversation. |
| `onboarding` | Perform initial project setup — creates memory files with project overview, commands, conventions. Call once per project. |

### Name Path Pattern Syntax

Serena uses **name paths** to identify symbols within files:

```
Simple name:     "setNoteText"              → matches ANY symbol with that name
Relative path:   "NodeAdapter/setNoteText"  → matches name path suffix
Absolute path:   "/NodeAdapter/setNoteText" → exact full path match
With index:      "MyClass/method[1]"        → specific overload (0-based)
Substring:       "Node/set" + substring_matching=true → matches "Node/setText", "Node/setLink", etc.
```

### LSP Symbol Kinds (for `include_kinds`/`exclude_kinds`)

| Kind | Integer | Kind | Integer |
|------|---------|------|---------|
| File | 1 | Module | 2 |
| Namespace | 3 | Package | 4 |
| Class | 5 | Method | 6 |
| Property | 7 | Field | 8 |
| Constructor | 9 | Enum | 10 |
| Interface | 11 | Function | 12 |
| Variable | 13 | Constant | 14 |
| String | 15 | Number | 16 |
| Boolean | 17 | Array | 18 |

### Example Workflows

#### Workflow 1: Understanding a Class Before Modifying It

```
# Step 1: Get class structure overview
get_symbols_overview("freemind/freemind/modes/NodeAdapter.java", depth=1)
→ See all methods, fields, inner classes

# Step 2: Read specific method body
find_symbol("NodeAdapter/setNoteText", include_body=True)
→ Read full implementation

# Step 3: Find all callers
find_referencing_symbols("NodeAdapter/setNoteText",
    relative_path="freemind/freemind/modes/NodeAdapter.java")
→ See every place that calls this method with code context

# Step 4: Make the change
replace_symbol_body("NodeAdapter/setNoteText", ..., body="new implementation")

# Step 5: Verify no broken references
find_referencing_symbols again → confirm all callers still compatible
```

#### Workflow 2: Safe Renaming

```
# Step 1: Find the symbol
find_symbol("MindMapController/addNewNode", include_info=True)
→ See signature and javadoc

# Step 2: Check impact
find_referencing_symbols("MindMapController/addNewNode", ...)
→ See all 47 callers across 12 files

# Step 3: Rename across entire codebase
rename_symbol("MindMapController/addNewNode", ..., new_name="createChildNode")
→ All 47 references updated automatically

# Step 4: Build to verify
make build
```

#### Workflow 3: Adding a New Method to an Existing Class

```
# Step 1: Find the last method in the class
get_symbols_overview("freemind/freemind/main/HtmlTools.java", depth=1)
→ Identify last method name

# Step 2: Insert new method after it
insert_after_symbol("HtmlTools/lastMethodName", ...,
    body="\n    public static String newMethod() {\n        ...\n    }")

# Step 3: Verify placement
get_symbols_overview again → confirm new method appears
```

#### Workflow 4: Cross-Codebase Pattern Search

```
# Find all XML plugin descriptors
search_for_pattern("hook_name=",
    paths_include_glob="**/*.xml",
    relative_path="freemind/plugins")

# Find all TODO/FIXME in code
search_for_pattern("TODO|FIXME",
    restrict_search_to_code_files=True,
    context_lines_after=2)

# Find usages of deprecated API in non-test code
search_for_pattern("FreeMindMainMock",
    restrict_search_to_code_files=True,
    paths_exclude_glob="**/tests/**")
```

### Anti-Patterns (What NOT to Do)

| Don't | Do Instead |
|-------|-----------|
| Read entire file with `cat` or `Read` tool first | Use `get_symbols_overview` → `find_symbol(include_body=True)` for specific symbols |
| Use `grep` to find method callers | Use `find_referencing_symbols` for precise, LSP-powered reference tracking |
| Manually search-and-replace a rename | Use `rename_symbol` for atomic, codebase-wide rename |
| Edit code with line-based text tools | Use `replace_symbol_body` for precise symbol-level edits |
| Skip Serena for "simple" changes | Always verify — even one-line changes can break interface contracts |
| Commit without `find_referencing_symbols` | Every modified symbol must be verified for reference integrity |

## Code Style

- Follow existing patterns in the codebase
- No `@SuppressWarnings` — fix root causes
- Never modify files in `generated-src/` — regenerate with `make jaxb`
- Never ignore `*.jar` in `.gitignore` — the project depends on ~90 tracked local JARs
- Preserve backward compatibility with existing `.mm` files

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

- `**/*.md`, `docs/**`, `_bmad-output/**`
- `LICENSE`, `COPYING`, `.gitattributes`
- `.github/ISSUE_TEMPLATE/**`, `.github/PULL_REQUEST_TEMPLATE/**`, `.github/release-notes-template.md`

Non-PR events (`push` to main, `workflow_call` from release-please) **always run the full matrix**.

**CI flow diagram:**

```
PR (code change):   changes(code=true)  → build(24) → gui-tests(24) → CI ✅
PR (docs only):     changes(code=false) → build(SKIP) → gui-tests(SKIP) → CI ✅
Push to main:       changes(code=true)  → build(24) → gui-tests(24) → CI ✅
workflow_call:      changes(code=true)  → build(24) → gui-tests(24) → CI ✅
```

### 4. Branch Workflow

- **All changes** via feature branch → Pull Request → main
- **No direct push to main** (enforced by GitHub Ruleset)
- PR requires: `CI` check pass + code review (single required check, not 48 individual)
- Squash merge preferred for clean history

### 5. GUI Test Requirements

- Every new UI feature **MUST** include GUI tests before merge
- Tests must capture screenshots for visual verification on every platform
- Tests must cover: happy path, error path, edge cases
- No UI change is too small to test
- GUI test base class: `GuiTestBase` (AssertJ Swing, automatic screenshots)

### 6. Release Gating

```
Push to main → release-please.yml (paths-ignore skips doc-only pushes)
  → build.yml (changes → build(24) → gui-tests(24) → CI) → release-please PR
Tag → release.yml validate (24) + gui-tests (24) → packaging
Any failure at any stage blocks release completely.
```

> **Note:** `release-please.yml` and `scorecard.yml` use `paths-ignore` on push triggers.
> Doc-only pushes to main skip these workflows entirely. The next code push catches up
> because release-please accumulates all commits since the last release.

### 7. New Runner/Java Version Procedure

When a new GitHub Actions runner image or Java GA version becomes available:

1. Add to matrix in `build.yml` and `release.yml`
2. No ruleset update needed — single `CI` aggregator check covers all matrix combinations automatically
3. Update the SOP table above
4. Verify all existing tests pass on the new combination
5. Fix any compatibility issues before merging

New Java GA versions must be added within 30 days of release.
New runner images must be added within 30 days of availability.

### 8. Test Philosophy

> "Being lazy in writing tests means facing much bigger workloads later.
> Our goal is to reduce future workload by testing every user scenario,
> every edge case, every state, comprehensively, now."

## Project Structure

See [CLAUDE.md](CLAUDE.md) for detailed repository layout, key source locations, and architecture overview.

## Reporting Issues

- Use the [Bug Report](https://github.com/Denomas/freemind-ce/issues/new?template=bug_report.yml) template
- Include your OS, Java version, and FreeMind CE version
- Attach the `.mm` file if the issue is map-specific

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v2.0](LICENSE).
