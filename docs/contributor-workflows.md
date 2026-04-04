# Contributor Workflows

This document consolidates all workflow diagrams for FreeMind CE contributors. Every diagram here
describes a real, enforced process — not aspirational guidelines. Read [CONTRIBUTING.md](../CONTRIBUTING.md)
for the full rules and policy text that accompanies these diagrams.

---

## Section 1: Master Overview

The full end-to-end path from "I want to contribute" to "code is shipped in a release":

```mermaid
flowchart TD
    A["Agent / Contributor starts work"] --> B["Read CONTRIBUTING.md<br>(MANDATORY first step)"]
    B --> C["Read required docs/<br>architecture, component-inventory,<br>source-tree-analysis, development-guide"]
    C --> D["Use Serena for code analysis<br>(MANDATORY — not optional)"]
    D --> E["Create feature branch from main"]
    E --> F["Make changes<br>(make build must pass locally)"]
    F --> G["git commit<br>Pre-commit hooks run"]
    G --> H{Hooks pass?}
    H -->|No| I["Fix hook failures<br>Do NOT use --no-verify"]
    I --> F
    H -->|Yes| J["Push branch"]
    J --> K["Open PR against<br>Denomas/freemind-ce:main"]
    K --> L{Doc-only PR?}
    L -->|Yes — only .md, docs/, etc.| M["CI: Detect Changes → code=false<br>48 build/test jobs SKIPPED<br>CI aggregator passes ~30s"]
    L -->|No — any .java/.kts/.xml etc.| N["CI: 48 jobs<br>6 OS × 4 Java versions<br>Build + GUI Tests"]
    M --> O{Review approved?}
    N --> P{All 48 jobs pass?}
    P -->|No| Q["Fix failures and re-push"]
    Q --> N
    P -->|Yes| O
    O -->|No| R["Address review feedback"]
    R --> F
    O -->|Yes| S["Squash merge to main"]
    S --> T["Branch auto-deleted"]
    T --> U["release-please detects<br>new commits on main"]
    U --> V["release-please creates<br>or updates Release PR<br>with CHANGELOG"]
    V --> W["Maintainer runs Release Checklist<br>(docs/merge-release-safety.md)"]
    W --> X["Merge Release PR<br>→ Git tag v*.*.* created"]
    X --> Y["release.yml: Build 24 + GUI 24<br>all platforms"]
    Y --> Z{All pass?}
    Z -->|No| AA["Release BLOCKED<br>Fix on main first"]
    Z -->|Yes| AB["Publish DMG + EXE + DEB<br>to GitHub Releases"]

    style B fill:#ffffcc,stroke:#cccc00
    style D fill:#ffffcc,stroke:#cccc00
    style O fill:#ffffcc,stroke:#cccc00
```

---

## Section 2: Contributor Type Workflows

### Who Can Contribute

Access level determines how you interact with the repository:

```mermaid
flowchart TD
    A[You want to contribute] --> B{Do you have<br/>write access?}
    B -->|No — External contributor| C["Fork the repo on GitHub<br/>Clone YOUR fork"]
    B -->|Yes — Collaborator/Maintainer| D["Clone the main repo directly"]

    C --> E["Create feature branch<br/>on your fork"]
    D --> F["Create feature branch<br/>on the main repo"]

    E --> G[Make changes + make build]
    F --> G

    G --> H["Push to your branch"]
    H --> I["Open PR against<br/>Denomas/freemind-ce:main"]
    I --> J["CI runs (48 jobs)"]
    J --> K{All pass?}
    K -->|No| L["Fix and re-push"]
    L --> J
    K -->|Yes| M["Maintainer reviews"]
    M --> N["Squash merge to main"]
```

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

> CI secrets are NOT available in fork PR runs. This is a GitHub security measure. All tests still
> run normally since they don't require secrets.

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

### For AI Agents

AI agents must follow the same process as human contributors with two additional mandatory gates
— Serena usage and explicit maintainer review of the generated diff:

```mermaid
flowchart TD
    A["AI agent starts work<br>(Claude Code, Cursor, etc.)"] --> B["Read CONTRIBUTING.md<br>(MANDATORY first step)"]
    B --> C["Read referenced docs/<br>files as needed"]
    C --> D["Use Serena for<br>code analysis (MANDATORY)"]
    D --> E["Create branch<br>make changes"]
    E --> F["make build<br>(must pass locally)"]
    F --> G["find_referencing_symbols<br>(verify impact with Serena)"]
    G --> H["Push branch + open PR"]
    H --> I["CI runs (48 jobs)"]
    I --> J{CI passes?}
    J -->|No| K["AI fixes issues"]
    K --> F
    J -->|Yes| L["MAINTAINER reviews diff<br>(human is accountable<br>for AI-generated code)"]
    L --> M{Approved?}
    M -->|Yes| N["Squash merge"]
    M -->|No| O["AI addresses feedback"]
    O --> F

    style B fill:#ffffcc,stroke:#cccc00
    style D fill:#ffffcc,stroke:#cccc00
    style L fill:#ffffcc,stroke:#cccc00
```

### First-Time Contributor Flow

GitHub pauses CI for first-time contributors until a maintainer verifies the code is safe to run:

```mermaid
flowchart TD
    A["New contributor forks repo"] --> B["Creates branch + makes changes"]
    B --> C["Opens PR from fork"]
    C --> D{First-time contributor?}
    D -->|Yes| E["CI PAUSED<br>Waiting for maintainer approval"]
    D -->|No, returning contributor| F["CI runs automatically"]

    E --> G["Maintainer reviews code<br>for safety (no malicious code)"]
    G --> H{Code looks safe?}
    H -->|Yes| I["Maintainer approves CI run"]
    H -->|No| J["Close PR with explanation"]

    I --> F
    F --> K{CI passes?}
    K -->|No| L["Contributor fixes + re-pushes"]
    L --> F
    K -->|Yes| M["Maintainer reviews changes"]
    M --> N["Maintainer approves + merges"]

    style E fill:#ffffcc,stroke:#cccc00
    style J fill:#ffcccc,stroke:#cc0000
```

---

## Section 3: Blocked Actions

### BLOCKED vs ALLOWED

Direct push to main is blocked for everyone — maintainer included. This is enforced by GitHub
Ruleset with an empty bypass list. There are no exceptions, no hotfix bypasses, no admin overrides.

```mermaid
flowchart TD
    subgraph BLOCKED["BLOCKED — These will be rejected by GitHub"]
        direction TB
        X1["git push origin main"] -->|REJECTED| R1["Ruleset: Require PR"]
        X2["git push --force origin main"] -->|REJECTED| R2["Ruleset: Block force push"]
        X3["gh pr merge --admin"] -->|FORBIDDEN| R3["Policy: No admin bypass"]
        X4["gh pr merge --auto<br>(without maintainer instruction)"] -->|FORBIDDEN| R4["Policy: Human decides"]
    end

    subgraph ALLOWED["ALLOWED — The only valid path"]
        direction TB
        A1["Create branch"] --> A2["Make changes"]
        A2 --> A3["make build (local)"]
        A3 --> A4["Push branch"]
        A4 --> A5["Open PR"]
        A5 --> A6["CI runs (48 jobs)"]
        A6 --> A7["Maintainer reviews + approves"]
        A7 --> A8["Squash merge"]
    end

    style BLOCKED fill:#ffcccc,stroke:#cc0000
    style ALLOWED fill:#ccffcc,stroke:#00cc00
```

### Forbidden Actions

| Action | Why Forbidden |
|--------|--------------|
| `git push origin main` | GitHub Ruleset blocks all direct pushes |
| `git push --force origin main` | Ruleset blocks force push to protected branch |
| `gh pr merge --admin` | Policy: no admin bypass, ever |
| `gh pr merge --auto` without explicit maintainer instruction | Policy: human decides when to merge |
| Merging with any failing CI job | Zero-tolerance policy — all 48 must be green |
| `@SuppressWarnings` | Fix root causes, never suppress |
| Editing files in `generated-src/` | Regenerate with `make jaxb` instead |
| Committing `auto.properties` | Runtime-generated user config, never committed |
| Committing `*.class` files | Build artifacts, blocked by pre-commit hook |
| Ignoring `*.jar` in `.gitignore` | ~90 tracked JARs in `lib/` are required dependencies |

---

## Section 4: Hotfix Flow

There is no shortcut for emergencies. The fastest path is still a PR. Estimated total time from
bug discovery to merge: approximately 12 minutes.

```mermaid
flowchart TD
    A["Bug found<br>in production"] --> B["Create hotfix branch<br>git checkout -b fix/critical-bug"]
    B --> C["Minimal fix<br>(only the bug, nothing else)"]
    C --> D["make build<br>(~30 seconds cached)"]
    D --> E["Push + open PR"]
    E --> F["CI runs<br>(~10 min)"]
    F --> G["Self-approve<br>(maintainer)"]
    G --> H["Squash merge"]
    H --> I["release-please picks up<br>on next release"]

    style A fill:#ff9999
    style H fill:#99ff99
```

Never bypass CI even for critical bugs. The 10-minute CI run is not negotiable.

---

## Section 5: Conflict Resolution

When two PRs modify the same file, the second one needs to be updated after the first merges:

```mermaid
flowchart TD
    A["PR #1 and PR #2<br>both modify same file"] --> B["PR #1 merges first"]
    B --> C["PR #2 shows:<br>'Branch is out of date'"]
    C --> D{Merge conflict?}

    D -->|No conflict| E["Click 'Update branch'<br>on GitHub UI"]
    D -->|Has conflict| F["Resolve locally:<br>git fetch origin<br>git rebase origin/main<br>Fix conflicts<br>git push --force-with-lease"]

    E --> G["CI re-runs on updated branch"]
    F --> G
    G --> H{CI passes?}
    H -->|Yes| I["Maintainer re-reviews if needed"]
    H -->|No| J["Fix new issues from merge"]
    J --> G
    I --> K["Merge"]
```

Use `--force-with-lease` instead of `--force` when rebasing — it prevents accidentally overwriting
commits pushed by someone else after your last fetch.

---

## Section 6: CI Pipeline

### CI Path Filtering Flow

The `Detect changes` job determines whether a PR contains only documentation changes. If so, the
48-job build/test matrix is skipped and the `CI` aggregator passes immediately (~30 seconds).
Non-PR events (push to main, `workflow_call` from release-please) always run the full matrix.

```mermaid
flowchart TD
    A[Event Triggered] --> B{Event Type?}
    B -->|pull_request| C[Detect Changes]
    B -->|push to main| F[code=true]
    B -->|workflow_call| F

    C --> D{Compute merge-base diff<br/>PR's own changes only}
    D -->|Only .md, docs/, LICENSE,<br/>COPYING, .gitattributes,<br/>templates changed| E[code=false]
    D -->|Any .java, .kts, .yml,<br/>.xml, .properties, etc.| F[code=true]

    E --> G[Build Matrix: SKIP]
    E --> H[GUI Tests: SKIP]

    F --> I["Build Matrix (24 jobs)<br>6 OS x 4 Java versions"]
    F --> J["GUI Tests (24 jobs)<br>6 OS x 4 Java versions"]

    G --> K{CI Aggregator}
    H --> K
    I --> K
    J --> K

    K -->|All SUCCESS or SKIPPED| L["CI: PASS"]
    K -->|Any FAILURE or CANCELLED| M["CI: FAIL<br>Merge BLOCKED"]

    L --> N{Review Approved?}
    N -->|Yes| O["Merge Allowed"]
    N -->|No| P["Merge BLOCKED<br>Waiting for review"]
```

### Doc-Only File Patterns

Changes to ONLY these files cause the 48-job matrix to be skipped:

| Pattern | Examples |
|---------|---------|
| `**/*.md` | `README.md`, `CONTRIBUTING.md`, `CLAUDE.md`, `docs/*.md` |
| `docs/**` | Any file under `docs/` directory |
| `LICENSE`, `COPYING`, `.gitattributes` | License and git config files |
| `.github/ISSUE_TEMPLATE/**` | Issue templates |
| `.github/PULL_REQUEST_TEMPLATE/**` | PR templates |
| `.github/release-notes-template.md` | Release notes template |

Any other file change (`.java`, `.kts`, `.yml`, `.xml`, `.properties`, etc.) triggers the full
48-job matrix.

### Path Filtering Detection Method

The `Detect changes` job uses `git merge-base` to compute the common ancestor between the PR branch
and main, then checks only the PR's own commits — not commits merged into main since the branch
was created. This prevents false positives when main has recent merges.

```bash
# Correct: merge-base ensures only PR's own changes are checked
BASE=$(git merge-base "$PR_BASE_SHA" "$PR_HEAD_SHA")
git diff --name-only "$BASE..$HEAD" -- ':!**/*.md' ':!docs/**' ...
```

---

## Section 7: Branch Workflow

### PR Lifecycle

All changes go through a feature branch and Pull Request. Squash merge produces a clean linear
history on main. The branch is automatically deleted after merge.

```mermaid
flowchart TD
    A[Create Branch<br/>from main] --> B[Make Changes]
    B --> C[Pre-commit Hooks<br/>XML, Java, Whitespace]
    C --> D[Push & Open PR]
    D --> E[CI: 48 Jobs<br/>or Doc-only Skip]
    E --> F[Maintainer Review]
    F --> G{All Gates Pass?}
    G -->|Yes| H[Squash Merge<br/>to main]
    G -->|No| I[Fix & Re-push]
    I --> E
    H --> J[Branch Auto-deleted]
    J --> K[release-please<br/>Creates Release PR]
```

- All changes via feature branch → Pull Request → main
- No direct push to main (enforced by GitHub Ruleset)
- PR requires: `CI` check pass + code review (single required check, not 48 individual)
- Squash merge only — clean linear history on main

---

## Section 8: Bot Lifecycle

Dependabot and release-please are the two bots that open PRs automatically. Both require
maintainer review before merge.

```mermaid
flowchart TD
    subgraph Dependabot
        DA["Dependabot detects<br>new dependency version"] --> DB["Opens PR automatically"]
        DB --> DC["CI runs (48 jobs)"]
        DC --> DD{CI passes?}
        DD -->|Yes| DE{Version type?}
        DD -->|No| DF["Maintainer investigates<br>See Dependency Update Protocol"]

        DE -->|Patch/Minor| DG["Maintainer reviews changelog<br>Approves PR"]
        DE -->|Major| DH["Maintainer follows<br>Major Update Protocol<br>(migration guide, local test)"]

        DG --> DI["Merge"]
        DH --> DI
    end

    subgraph ReleasePlease["release-please"]
        RA["Code merged to main"] --> RB["release-please creates/updates<br>Release PR with CHANGELOG"]
        RB --> RC["CI runs on Release PR"]
        RC --> RD{CI passes?}
        RD -->|No| RE["Fix failures on main first<br>release-please will update PR"]
        RD -->|Yes| RF["Maintainer applies<br>Release Checklist"]
        RF --> RG{Checklist complete?}
        RG -->|No| RH["Complete missing items"]
        RH --> RF
        RG -->|Yes| RI["Maintainer merges Release PR"]
        RI --> RJ["Tag created → release.yml<br>→ DMG/EXE/DEB published"]
    end
```

---

## Section 9: Dependency Updates

Dependabot opens PRs when new versions of dependencies are available. The decision tree below
determines the required review depth. CI passing alone is never sufficient — human review is
always required. For the full protocol, see [docs/merge-release-safety.md](merge-release-safety.md).

```mermaid
flowchart TD
    A[Dependabot opens PR] --> B["CI Matrix runs (48 jobs)"]
    B --> C{All 48 pass?}
    C -->|No, expected for major| D{Major version bump?}
    C -->|Yes| E{Version bump type?}

    D -->|Yes| F["Read migration guide<br>Fix breaking API changes<br>Push fixes to PR branch"]
    F --> B
    D -->|No| G["Investigate failure<br>Do NOT merge"]

    E -->|Patch x.x.1→x.x.2| H["Review changelog<br>Maintainer approves"]
    E -->|Minor x.1→x.2| I["Review changelog<br>Run make build locally<br>Maintainer approves"]
    E -->|Major 1.x→2.x| J["Review migration guide<br>Run make build locally<br>Run make run smoke test<br>Check ./gradlew dependencies<br>Maintainer approves"]

    H --> K[Merge]
    I --> K
    J --> K
```

- **Patch** (x.x.1→x.x.2): CI passes + changelog review + maintainer approve
- **Minor** (x.1→x.2): Above + local `make build` verification
- **Major** (1.x→2.x): Above + migration guide + API fix + `make run` smoke test

---

## Section 10: Release Gating

The release pipeline has multiple gates. Any failure at any stage blocks the release completely.
There is no manual override. For the full checklist, see
[docs/merge-release-safety.md](merge-release-safety.md).

```mermaid
flowchart TD
    A[Code merged to main] --> B[release-please.yml triggered]
    B --> C{Doc-only push?}
    C -->|Yes, paths-ignore match| D[release-please SKIPPED<br/>Next code push catches up]
    C -->|No, code changes| E[release-please creates<br/>Release PR with CHANGELOG]

    E --> F["build.yml: CI Matrix (48 jobs)"]
    F --> G{All 48 jobs pass?}
    G -->|No| H["Release BLOCKED<br>Fix failures first"]
    G -->|Yes| I[Maintainer reviews<br/>Release PR]

    I --> J["Release Checklist<br>(see docs/merge-release-safety.md)"]
    J --> K{Checklist complete?}
    K -->|No| L["Do NOT merge<br>Complete checklist first"]
    K -->|Yes| M[Merge Release PR]

    M --> N[Git tag v*.*.* created]
    N --> O["release.yml triggered"]
    O --> P["Validate: build(24) + gui-tests(24)"]
    P --> Q{All pass?}
    Q -->|No| R["Release BLOCKED<br>Tag exists but no artifacts"]
    Q -->|Yes| S["Package: DMG + EXE + DEB"]
    S --> T["GitHub Release published<br>with artifacts"]
```

> `release-please.yml` and `scorecard.yml` use `paths-ignore` on push triggers. Doc-only pushes
> to main skip these workflows entirely. The next code push catches up because release-please
> accumulates all commits since the last release.

---

## Section 11: Security Incident

When a secret is detected, the response depends on where it was caught. Rotate the credential
immediately regardless of where the detection occurred.

```mermaid
flowchart TD
    A["Secret detected in commit"] --> B{Where detected?}

    B -->|Push Protection<br/>at git push time| C["Push BLOCKED<br>Secret never reaches GitHub"]
    C --> D["Remove secret from code<br>Rotate credential immediately<br>Re-push clean commit"]

    B -->|Secret Scanning<br/>after push| E["GitHub creates alert"]
    E --> F["Rotate credential IMMEDIATELY"]
    F --> G["Remove from git history:<br>git filter-repo or BFG"]
    G --> H["Force push cleaned history<br>(ONLY valid use of force push)"]
    H --> I["Verify alert resolved"]

    B -->|Code review<br/>during PR| J["Reviewer flags the secret"]
    J --> K["Contributor removes from PR"]
    K --> L["Rotate credential if<br>it was pushed to any branch"]

    style C fill:#99ff99
    style F fill:#ff9999
```

---

## Section 12: Pre-Commit Hooks

At every `git commit`, pre-commit runs a sequence of checks. Any single failure rejects the
commit. Never use `--no-verify` — fix the root cause instead.

```mermaid
flowchart TD
    A["git commit triggered"] --> B["check-merge-conflict<br>Scan staged files for conflict markers"]
    B --> C{Pass?}
    C -->|No| FAIL["Commit REJECTED<br>Fix the failure and re-stage"]
    C -->|Yes| D["check-added-large-files<br>>500KB non-JAR files blocked"]
    D --> E{Pass?}
    E -->|No| FAIL
    E -->|Yes| F["end-of-file-fixer<br>Ensure files end with newline"]
    F --> G{Pass?}
    G -->|No — auto-fixed| H["Re-stage the auto-fixed files<br>and re-run commit"]
    G -->|Yes| I["trailing-whitespace<br>Remove trailing spaces"]
    I --> J{Pass?}
    J -->|No — auto-fixed| H
    J -->|Yes| K{XML/XSL/XSD files staged?}
    K -->|Yes| L["check-xml<br>Validate XML syntax"]
    K -->|No| M{YAML files staged?}
    L --> LA{Pass?}
    LA -->|No| FAIL
    LA -->|Yes| M
    M -->|Yes| N["check-yaml<br>Validate YAML syntax"]
    M -->|No| O["detect-private-key<br>Scan for PEM headers and SSH keys"]
    N --> NA{Pass?}
    NA -->|No| FAIL
    NA -->|Yes| O
    O --> P{Pass?}
    P -->|No| FAIL
    P -->|Yes| Q["check-symlinks<br>Detect broken symbolic links"]
    Q --> R{Pass?}
    R -->|No| FAIL
    R -->|Yes| S{JSON files staged?}
    S -->|Yes| T["check-json<br>Validate JSON syntax"]
    S -->|No| U["Block .class files<br>Fail if any *.class staged"]
    T --> TA{Pass?}
    TA -->|No| FAIL
    TA -->|Yes| U
    U --> V{Pass?}
    V -->|No| FAIL
    V -->|Yes| W["Block auto.properties<br>Fail if auto.properties staged"]
    W --> X{Pass?}
    X -->|No| FAIL
    X -->|Yes| Y["Block build directory files<br>Fail if build/ paths staged"]
    Y --> Z{Pass?}
    Z -->|No| FAIL
    Z -->|Yes| AA{Java files staged?}
    AA -->|Yes| AB["Verify Java compilation<br>./gradlew :freemind:compileJava"]
    AA -->|No| AC["gitlint<br>Validate commit message format"]
    AB --> AC2{Compiles?}
    AC2 -->|No| FAIL
    AC2 -->|Yes| AC
    AC --> AD{Conventional Commits format?}
    AD -->|No| FAIL
    AD -->|Yes| AE["Commit ACCEPTED"]

    style FAIL fill:#ffcccc,stroke:#cc0000
    style AE fill:#ccffcc,stroke:#00cc00
    style H fill:#ffffcc,stroke:#cccc00
```

---

## Section 13: PR Review Checklist

When a maintainer reviews a PR, the following checklist must be completed before approval.
Every item is a hard requirement — not a suggestion.

```mermaid
flowchart TD
    A["PR opened / updated"] --> B["Code correctness<br>Does the logic match the intent?<br>No off-by-one errors, null dereferences, race conditions?"]
    B --> C{Pass?}
    C -->|No| CHANGES["Request changes"]
    C -->|Yes| D["Test coverage<br>Does new/changed code have tests?<br>Do tests cover happy path, error path, edge cases?"]
    D --> E{Pass?}
    E -->|No| CHANGES
    E -->|Yes| F["Backward compatibility<br>Do existing .mm files still load correctly?<br>Are public API signatures preserved?"]
    F --> G{Pass?}
    G -->|No| CHANGES
    G -->|Yes| H["No @SuppressWarnings<br>All warnings must be fixed at the root cause"]
    H --> I{Pass?}
    I -->|No| CHANGES
    I -->|Yes| J["No generated-src modifications<br>Files in generated-src/ must not be hand-edited"]
    J --> K{Pass?}
    K -->|No| CHANGES
    K -->|Yes| L["No secrets or credentials<br>No API keys, passwords, tokens, private keys"]
    L --> M{Pass?}
    M -->|No| CHANGES
    M -->|Yes| N["Conventional commit message format<br>feat/fix/docs/refactor/test/build/ci/chore/deps<br>Max 72 chars subject, imperative mood"]
    N --> O{Pass?}
    O -->|No| CHANGES
    O -->|Yes| P["Serena verification done<br>find_referencing_symbols run for all changed symbols?<br>No orphaned references?"]
    P --> Q{Pass?}
    Q -->|No| CHANGES
    Q -->|Yes| R["make build passes<br>CI is green, but local build confirms no env-specific issues"]
    R --> S{Pass?}
    S -->|No| CHANGES
    S -->|Yes| T["APPROVE PR"]

    style CHANGES fill:#ffffcc,stroke:#cccc00
    style T fill:#ccffcc,stroke:#00cc00
```

---

## Section 14: Commit Message Decision Tree

Use the type prefix that most accurately describes the change. When in doubt, choose the prefix
for the primary effect — not the mechanism.

```mermaid
flowchart TD
    A["What does this commit do?"] --> B{Adds a new capability<br>that didn't exist before?}
    B -->|Yes| FEAT["feat:<br>Example: feat: add dark mode icon variants"]

    B -->|No| C{Fixes incorrect behavior<br>or a bug?}
    C -->|Yes| FIX["fix:<br>Example: fix: resolve crash when opening large maps"]

    C -->|No| D{Only changes documentation,<br>comments, or markdown?}
    D -->|Yes| DOCS["docs:<br>Example: docs: update build instructions for Windows"]

    D -->|No| E{Formatting, whitespace,<br>or style only — no logic change?}
    E -->|Yes| STYLE["style:<br>Example: style: normalize indentation in MapView"]

    E -->|No| F{Restructures existing code<br>without changing behavior?}
    F -->|Yes| REFACTOR["refactor:<br>Example: refactor: extract HeadlessFreeMind from test mock"]

    F -->|No| G{Improves speed or<br>resource usage?}
    G -->|Yes| PERF["perf:<br>Example: perf: cache parsed node styles"]

    G -->|No| H{Adds or updates<br>tests only?}
    H -->|Yes| TEST["test:<br>Example: test: add coverage for XML round-trip serialization"]

    H -->|No| I{Changes to build scripts,<br>Gradle config, Makefile?}
    I -->|Yes| BUILD["build:<br>Example: build: upgrade FlatLaf to 3.7.0"]

    I -->|No| J{Changes to GitHub Actions<br>or CI configuration?}
    J -->|Yes| CI["ci:<br>Example: ci: add OWASP dependency scanning"]

    J -->|No| K{Updates a dependency<br>version?}
    K -->|Yes| DEPS["deps:<br>Example: deps: bump jsoup from 1.10.3 to 1.17.2"]

    K -->|No| CHORE["chore:<br>Example: chore: update .gitignore patterns"]

    style FEAT fill:#ccffcc,stroke:#00cc00
    style FIX fill:#ffcccc,stroke:#cc0000
    style DOCS fill:#cce5ff,stroke:#0066cc
    style STYLE fill:#e5ccff,stroke:#6600cc
    style REFACTOR fill:#fff0cc,stroke:#cc9900
    style PERF fill:#ccffe5,stroke:#009966
    style TEST fill:#ffe5cc,stroke:#cc6600
    style BUILD fill:#e5e5ff,stroke:#0000cc
    style CI fill:#ffe5e5,stroke:#cc0033
    style DEPS fill:#e5ffe5,stroke:#009900
    style CHORE fill:#f5f5f5,stroke:#999999
```

**Breaking changes (`feat!:`) are FORBIDDEN.** FreeMind CE preserves full backward compatibility with every `.mm` file ever created. We do not break the past. The `!` suffix and `BREAKING CHANGE` footer must never be used. See [CONTRIBUTING.md — Project Philosophy](../CONTRIBUTING.md#project-philosophy).

**Allowed types in full:** `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`,
`ci`, `chore`, `revert`, `deps` — enforced by gitlint on every commit.

---

## Section 15: Parallel Work / Overlapping PRs

When you discover that another open PR overlaps with your work, follow this decision tree.
For the full binding rules, see [CONTRIBUTING.md — Parallel Work Protection](../CONTRIBUTING.md#parallel-work-protection).

```mermaid
flowchart TD
    A["You discover open PR #X<br>overlaps with your work"] --> B{Are you the author<br>of PR #X?}

    B -->|Yes — same author| C["You may close/merge/reorder<br>your own PRs as needed"]
    B -->|No — different author/session| D["DO NOT close PR #X<br>DO NOT absorb its commits<br>DO NOT claim supersession"]

    D --> E["Add to YOUR PR description:<br>Related: #X"]
    E --> F["Inform the maintainer<br>that overlap exists"]
    F --> G["Maintainer reviews both<br>PRs independently"]

    G --> H{Maintainer decides<br>merge order}
    H -->|PR #X first| I["PR #X merges to main"]
    H -->|Your PR first| J["Your PR merges to main"]

    I --> K["Rebase your branch:<br>git fetch origin<br>git rebase origin/main"]
    J --> L["PR #X author rebases:<br>git fetch origin<br>git rebase origin/main"]

    K --> M["Resolve conflicts if any"]
    L --> M
    M --> N["CI re-runs on rebased branch"]
    N --> O["Normal merge flow continues"]

    style D fill:#ffcccc,stroke:#cc0000
    style E fill:#ccffcc,stroke:#00cc00
    style F fill:#ccffcc,stroke:#00cc00
```

### Key Principles

- **PR sovereignty:** Every PR belongs to its author. Only the author or maintainer may close it.
- **No supersession claims:** Saying "my PR supersedes yours" is a judgment call reserved for the maintainer.
- **Report, don't act:** When an AI agent discovers overlap, it reports to the human — it does not take action on the other PR.
- **Independent review:** Overlapping PRs are reviewed independently. Neither is subordinate to the other.
- **Merge order:** The maintainer decides which PR merges first. The second PR rebases and resolves conflicts.
