# Merge, Release & Dependency Safety

This document defines the mandatory protocols for merging pull requests, updating dependencies, and releasing new versions of FreeMind CE. All contributors, maintainers, and AI agents MUST follow these protocols without exception.

## Merge Safety Protocol

### Zero-Tolerance Merge Policy

Every PR merge requires ALL of the following — no exceptions, no bypass:

| Gate | Requirement | Enforced By |
|------|------------|-------------|
| CI Matrix | ALL jobs SUCCESS (48/48) | GitHub Ruleset (`CI` aggregator) |
| GUI Tests | ALL GUI test jobs SUCCESS | GitHub Ruleset (`CI` aggregator) |
| Review | Maintainer approval | GitHub Ruleset (`required_approving_review_count: 1`) |
| Conflict-free | No merge conflicts | GitHub automatic check |
| Up-to-date | Branch synced with main | Ruleset strict status checks |
| Pre-commit | All hooks pass | `.pre-commit-config.yaml` |
| Conversations | All review threads resolved | Ruleset conversation resolution |

### Strictly Forbidden

| Action | Why It Is Forbidden |
|--------|-------------------|
| `gh pr merge --admin` | Bypasses ALL protection rules — CI, review, everything |
| `gh pr merge --auto` without explicit maintainer instruction | Removes human from the decision loop |
| Merging with ANY failing CI check | Even 47/48 green is not enough — that 1 failure may be a real bug |
| Merging without maintainer review | Applies to ALL PRs including Dependabot and bot-generated PRs |
| Force push to main | Destroys history, blocked by ruleset |
| Any other bypass of merge controls | If blocked, investigate and fix — never work around |

### When a PR Is Blocked

1. Read the CI failure logs — understand what failed and why
2. Fix the root cause on the PR branch
3. Push the fix and wait for CI to re-run
4. Only merge after ALL checks pass AND maintainer approves

Never bypass a blocker. If a blocker seems incorrect (e.g., flaky CI infrastructure), document it in the PR comments and wait for a clean run.

## Dependency Update Protocol

All dependency updates follow risk-appropriate procedures. CI passing alone is NOT sufficient — manual review is required to verify compatibility, breaking changes, and runtime behavior.

### Patch Updates (x.x.1 → x.x.2)

| Step | Action | Who |
|------|--------|-----|
| 1 | Dependabot opens PR | Automatic |
| 2 | CI matrix runs (48 jobs) — ALL must pass | Automatic |
| 3 | Review changelog for relevant fixes | Maintainer |
| 4 | Approve PR | Maintainer |
| 5 | Merge | Automatic (after approval) |

### Minor Updates (x.1.0 → x.2.0)

| Step | Action | Who |
|------|--------|-----|
| 1 | Dependabot opens PR | Automatic |
| 2 | CI matrix runs (48 jobs) — ALL must pass | Automatic |
| 3 | Review changelog for new features and deprecations | Maintainer |
| 4 | Run `make build` locally to verify | Maintainer |
| 5 | Approve PR | Maintainer |
| 6 | Merge | Automatic (after approval) |

### Major Updates (1.x → 2.x) — Special Handling Required

Major version bumps in **dependencies** may contain breaking API changes that require code adaptation. They require the most rigorous review process. Note: while the dependency's API may change, **FreeMind CE itself must never break backward compatibility** — every `.mm` file must still open correctly after any dependency update.

| Step | Action | Who |
|------|--------|-----|
| 1 | Dependabot opens PR | Automatic |
| 2 | CI matrix runs — **expect failures** | Automatic |
| 3 | Read the library's **migration guide** | Maintainer |
| 4 | Identify all breaking API changes in project code | Maintainer |
| 5 | Fix compilation errors on the Dependabot branch (push fix commits) | Maintainer |
| 6 | CI matrix re-runs — ALL 48 jobs must pass | Automatic |
| 7 | Run `make build` locally — must be BUILD SUCCESSFUL | Maintainer |
| 8 | Run `make run` — smoke-test the application | Maintainer |
| 9 | Verify no dependency conflicts (`./gradlew dependencies`) | Maintainer |
| 10 | Approve PR | Maintainer |
| 11 | Merge | Automatic (after approval) |

**If a major update cannot be made compatible:** close the Dependabot PR with a comment explaining why, and create a GitHub issue to track the migration for a future release.

### Dependency Update Rules

- Dependabot auto-merge workflow MUST NOT merge without maintainer approval
- Even patch updates can introduce regressions — human review is always required
- CI passing does NOT mean safe to merge — tests do not cover 100% of runtime behavior
- When multiple dependency PRs are open, merge smallest/safest first, largest/riskiest last
- After merging a dependency update, verify the next PR in queue is not affected by conflicts

## Release Checklist

Before merging any release-please PR:

- [ ] All CI checks pass (48/48 SUCCESS, 0 failures, 0 cancelled)
- [ ] CHANGELOG.md entries are correct and properly categorized
- [ ] `make build` passes locally on the release branch
- [ ] `make run` — application opens, basic operations work:
  - Open an existing `.mm` file
  - Add and edit nodes (text, icons, links)
  - Save the file
  - Export to at least one format (HTML, PDF, or PNG)
  - Verify no errors in `~/.freemind/log.0`
- [ ] No open Dependabot PRs with failing CI (all dependencies should be stable)
- [ ] Version number in `.release-please-manifest.json` is correct
- [ ] No WIP branches that should have been included in this release
- [ ] Previous release had no reported regressions (check GitHub Issues)

After release:

- [ ] Download release artifacts (DMG/EXE/DEB) from GitHub Release page
- [ ] Install and verify they launch correctly on at least one platform
- [ ] Check GitHub Actions release workflow completed successfully
- [ ] Verify release notes are accurate on the GitHub Release page

## GitHub Ruleset Configuration

The `main` branch is protected by GitHub Rulesets with the following configuration:

### Ruleset: `main-protection`

| Rule | Setting | Purpose |
|------|---------|---------|
| Restrict deletions | ON | Main branch cannot be deleted |
| Block force pushes | ON | No force push to main |
| Require pull request | ON | No direct push to main — ALL changes via PR |
| Required approvals | 1 | Every PR needs maintainer approval |
| Require approval from someone other than last pusher | OFF | Solo maintainer can self-approve (CI is the real gate) |
| Dismiss stale reviews | ON | New push invalidates previous approval |
| Require conversation resolution | ON | All review threads must be resolved |
| Require status checks | ON | `CI` aggregator must pass |
| Strict status checks | ON | Branch must be up-to-date with main |
| Bypass list | EMPTY | Nobody can bypass — not even admin |

### Merge Method

- **Squash merge only** — produces clean, linear history on main
- Merge commits and rebase merge are disabled
- Auto-delete head branches after merge

## Fork PR Security

External contributors submit PRs from forks. GitHub Actions handles fork PRs with restricted permissions:

| Aspect | Fork PR Behavior |
|--------|-----------------|
| CI workflow runs? | Yes — uses workflow YAML from **main** (not the fork) |
| Secrets available? | NO — `GITHUB_TOKEN` is read-only, custom secrets hidden |
| Can modify workflows? | NO — base branch workflow is used |
| Write permissions? | NO — fork PRs cannot push, create releases, etc. |
| First-time contributor | Requires maintainer approval before CI runs |

**Maintainer responsibilities for fork PRs:**
1. Review the diff carefully — fork code is untrusted
2. Check for malicious dependency changes in `build.gradle.kts`
3. Verify no secrets or credentials are included
4. Approve CI run for first-time contributors before any code executes

## Bot PR Policy

| Bot | Creates PRs | Auto-merge Allowed | Maintainer Action |
|-----|------------|-------------------|-------------------|
| **release-please** | Release PR with CHANGELOG | NO — always manual review | Apply Release Checklist, then approve |
| **Dependabot (patch)** | Dependency bump PR | YES — after CI passes | Monitor, intervene if CI fails |
| **Dependabot (minor)** | Dependency bump PR | YES — after CI passes | Review changelog, then let auto-merge proceed |
| **Dependabot (major)** | Dependency bump PR | NO — manual review required | Follow Major Update Protocol |

Dependabot auto-merge for patch/minor updates is an approved exception to the general "no auto-merge" rule. This is documented here as an explicit policy decision. Major version bumps always require manual review per the Dependency Update Protocol above.

## AI Agent Policy

AI agents (Claude Code, Cursor, Copilot, etc.) working with maintainer credentials:

- Follow the exact same PR workflow as any human contributor
- Commits are attributed to the maintainer (the human is accountable)
- AI-generated PRs MUST be reviewed by the maintainer with the same rigor as external contributions
- Never add AI attribution (`Co-Authored-By`, "Generated with..." etc.) to commits or PRs
- AI agents must run `make build` before pushing and use Serena for impact analysis (see CONTRIBUTING.md)

## Incident History

This section records incidents where merge/release protocols were violated, as lessons learned.

### 2026-03-16: Admin Merge Bypass

**What happened:** `gh pr merge --admin` was used to bypass the review requirement on PRs #21 (Rhino 1.9.1), #22 (Jazzer 0.30.0), #23 (Lucene 10.4.0), and #26 (test expansion). CI was green but maintainer review was skipped.

**Impact:** Version bumps (including a major version: Lucene 9→10) were merged without proper changelog review and migration verification. While no production bug resulted (the Lucene API fix had been applied beforehand), the process violation created unnecessary risk.

**Root cause:** Attempting to speed up the merge process by bypassing the review gate.

**Corrective action:**
1. This document was created with explicit forbidden actions list
2. CLAUDE.md Critical Rules updated to reference this document
3. Ruleset bypass list emptied — admin bypass no longer possible
4. Memory files converted to point to this document instead of containing rules
