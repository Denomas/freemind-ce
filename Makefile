# FreeMind CE - Development Makefile
# Cross-platform: macOS (Homebrew/MacPorts), Linux (apt/dnf/sdkman), Windows (WSL/Git Bash)
# Run 'make help' to see all available targets.

# ── Java Home Detection ──────────────────────────────────────────────
# Priority: 1) existing JAVA_HOME env  2) OS-specific auto-detect  3) fail with message
# Override: JAVA_HOME=/path/to/jdk make build

ifndef JAVA_HOME
  UNAME_S := $(shell uname -s 2>/dev/null || echo Windows)

  ifeq ($(UNAME_S),Darwin)
    # macOS: try Homebrew first, then MacPorts, then /usr/libexec
    JAVA_HOME := $(shell \
      if [ -d "/opt/homebrew/opt/openjdk@21" ]; then \
        echo "/opt/homebrew/opt/openjdk@21"; \
      elif [ -d "/usr/local/opt/openjdk@21" ]; then \
        echo "/usr/local/opt/openjdk@21"; \
      elif [ -d "/opt/homebrew/opt/openjdk" ]; then \
        echo "/opt/homebrew/opt/openjdk"; \
      elif [ -d "/usr/local/opt/openjdk" ]; then \
        echo "/usr/local/opt/openjdk"; \
      elif command -v /usr/libexec/java_home >/dev/null 2>&1; then \
        /usr/libexec/java_home --version 21 2>/dev/null || /usr/libexec/java_home 2>/dev/null; \
      fi)
  else ifeq ($(UNAME_S),Linux)
    # Linux: try common JDK 21 paths, then SDKMAN, then update-alternatives
    JAVA_HOME := $(shell \
      if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then \
        echo "/usr/lib/jvm/java-21-openjdk-amd64"; \
      elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then \
        echo "/usr/lib/jvm/java-21-openjdk"; \
      elif [ -d "/usr/lib/jvm/java-21" ]; then \
        echo "/usr/lib/jvm/java-21"; \
      elif [ -d "$$HOME/.sdkman/candidates/java/current" ]; then \
        echo "$$HOME/.sdkman/candidates/java/current"; \
      elif command -v javac >/dev/null 2>&1; then \
        dirname $$(dirname $$(readlink -f $$(which javac))); \
      fi)
  endif

  ifeq ($(JAVA_HOME),)
    $(warning *** JAVA_HOME not set and JDK 21 not found automatically.)
    $(warning *** Install JDK 21 or set JAVA_HOME manually:)
    $(warning ***   macOS:   brew install openjdk@21)
    $(warning ***   Ubuntu:  sudo apt install openjdk-21-jdk)
    $(warning ***   Fedora:  sudo dnf install java-21-openjdk-devel)
    $(warning ***   SDKMAN:  sdk install java 21-open)
    $(warning ***   Manual:  JAVA_HOME=/path/to/jdk make <target>)
    $(error JAVA_HOME is required)
  endif
endif

export JAVA_HOME

# ── Gradle Configuration ─────────────────────────────────────────────
GRADLE       := ./gradlew
GRADLE_FLAGS := --no-configuration-cache
GRADLE_CMD   = JAVA_HOME=$(JAVA_HOME) $(GRADLE)

.PHONY: help build run debug clean test test-gui test-performance test-chaos coverage jaxb javadoc \
        package package-mac package-win package-linux \
        dist-zip install-dist check audit audit-full info all

.DEFAULT_GOAL := help

# ── General ──────────────────────────────────────────────────────────
##@ General

help: ## Show this help message
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} \
		/^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 } \
		/^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

info: ## Show detected Java and system info
	@echo "JAVA_HOME  = $(JAVA_HOME)"
	@echo "Java       = $$($(JAVA_HOME)/bin/java -version 2>&1 | head -1)"
	@echo "OS         = $$(uname -s 2>/dev/null || echo Windows) $$(uname -m 2>/dev/null)"
	@echo "Gradle     = $$($(GRADLE_CMD) --version $(GRADLE_FLAGS) 2>/dev/null | grep '^Gradle' || echo 'run make build first')"

# ── Development ──────────────────────────────────────────────────────
##@ Development

build: ## Build the project (compile + test)
	$(GRADLE_CMD) :freemind:build $(GRADLE_FLAGS)

run: ## Run FreeMind CE
	$(GRADLE_CMD) :freemind:run $(GRADLE_FLAGS)

debug: ## Run in debug mode (attach debugger on port 5005)
	$(GRADLE_CMD) :freemind:run $(GRADLE_FLAGS) --debug-jvm

clean: ## Clean all build artifacts
	$(GRADLE_CMD) clean $(GRADLE_FLAGS)

test: ## Run tests only
	$(GRADLE_CMD) :freemind:test $(GRADLE_FLAGS)

test-gui: ## Run GUI tests with screenshots (requires display)
	$(GRADLE_CMD) :freemind:testGui $(GRADLE_FLAGS)

test-performance: ## Run performance tests
	$(GRADLE_CMD) :freemind:testPerformance $(GRADLE_FLAGS)

test-chaos: ## Run chaos/resilience tests
	$(GRADLE_CMD) :freemind:testChaos $(GRADLE_FLAGS)

coverage: ## Run tests with JaCoCo coverage report
	$(GRADLE_CMD) :freemind:test :freemind:jacocoTestReport $(GRADLE_FLAGS)
	@echo "Coverage report: freemind/build/reports/jacoco/test/html/index.html"

check: ## Run build + all quality checks
	$(GRADLE_CMD) check $(GRADLE_FLAGS)

# ── Security ────────────────────────────────────────────────────────
##@ Security

audit: ## Run security audit — Grype (fast, ~30s, fails on High+)
	@command -v grype >/dev/null 2>&1 || { echo "Error: grype not found. Install: mise install grype"; exit 1; }
	grype dir:build --only-fixed --fail-on high
	@echo ""
	@echo "Full OWASP report: make audit-full"

audit-full: ## Run full OWASP dependency-check (slower, detailed HTML report)
	$(GRADLE_CMD) :freemind:dependencyCheckAnalyze $(GRADLE_FLAGS)
	@echo ""
	@echo "Report: freemind/build/reports/dependency-check-report.html"

##@ Code Generation

jaxb: ## Regenerate JAXB classes from XSD schema
	$(GRADLE_CMD) :freemind:generateJaxb $(GRADLE_FLAGS)

javadoc: ## Generate API documentation
	$(GRADLE_CMD) javadoc $(GRADLE_FLAGS)
	@echo "Javadoc: freemind/build/docs/javadoc/index.html"

# ── Packaging ────────────────────────────────────────────────────────
##@ Packaging

package: ## Build native package for current OS
	@case "$$(uname -s 2>/dev/null)" in \
		Darwin) $(MAKE) package-mac ;; \
		Linux)  $(MAKE) package-linux ;; \
		*)      echo "Auto-detect failed. Use: make package-mac|package-win|package-linux" ;; \
	esac

package-mac: ## Build macOS DMG package
	$(GRADLE_CMD) :freemind:jpackageMac $(GRADLE_FLAGS)

package-win: ## Build Windows EXE installer
	$(GRADLE_CMD) :freemind:jpackageWin $(GRADLE_FLAGS)

package-linux: ## Build Linux DEB package
	$(GRADLE_CMD) :freemind:jpackageLinux $(GRADLE_FLAGS)

dist-zip: ## Create distribution ZIP archive
	$(GRADLE_CMD) :freemind:distZip $(GRADLE_FLAGS)

install-dist: ## Create local distribution layout
	$(GRADLE_CMD) :freemind:installDist $(GRADLE_FLAGS)

# ── Aliases ──────────────────────────────────────────────────────────
##@ Aliases

all: build ## Alias for build
