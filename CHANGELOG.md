# Changelog

## [1.2.0](https://github.com/Denomas/freemind-ce/compare/v1.1.0...v1.2.0) (2026-03-12)


### Features

* add accessibility support for screen readers ([24c9a17](https://github.com/Denomas/freemind-ce/commit/24c9a17e25e4416b9766e2c3413431f9a0de1c85))
* add clean export variants for Context Graph plugin ([5c1e1cb](https://github.com/Denomas/freemind-ce/commit/5c1e1cb9e2b6fa51d11325681da678cde2689059))
* add clean export variants to Context Graph plugin ([e9ee379](https://github.com/Denomas/freemind-ce/commit/e9ee37984bbcc50ee002c38e44f7a5e8b8aac5c2))
* add fit-to-N-pages-wide print option with improved dialog ([9993479](https://github.com/Denomas/freemind-ce/commit/9993479fdc55259ac5e87ed186186e06f32648f0))
* add FlatLaf theme customization properties ([f3a5f58](https://github.com/Denomas/freemind-ce/commit/f3a5f58da123d9485d255fba7f6b1d44bb529c92))
* add JSON export format for mind maps ([75fed78](https://github.com/Denomas/freemind-ce/commit/75fed781a21691da3ebdbab45492545597f7a90b))
* add multi-platform distribution formats (v1.2.0) ([678daef](https://github.com/Denomas/freemind-ce/commit/678daef94b513e73e732abde0813c7223b9a3f64))
* add new icon variants to icons list ([5a05e45](https://github.com/Denomas/freemind-ce/commit/5a05e45a6243a2dafe7d93274160343eaee50b8d))
* add new node icon variants (PNG + SVG) ([39505b8](https://github.com/Denomas/freemind-ce/commit/39505b8de908fcd6db6110f40c0b4881f52d0397))
* add platform-adaptive keyboard shortcuts for macOS ([6cc69da](https://github.com/Denomas/freemind-ce/commit/6cc69daba1be513a966a4c8e03b2e983ab8d462a))
* add SVG icon support with FlatLaf and PNG fallback ([d4c9782](https://github.com/Denomas/freemind-ce/commit/d4c97822ddb73e6f94692e77824511430a01ed60))
* add YAML export format for mind maps ([39c1311](https://github.com/Denomas/freemind-ce/commit/39c1311946535ba718efb18e304d1b12b366b121))
* complete Turkish translation for all UI strings ([66ef4b4](https://github.com/Denomas/freemind-ce/commit/66ef4b4717d50fa174cddd272ce0f63ad82021f2))
* modernize LaTeX plugin and export menu (v1.3.0) ([506a0f1](https://github.com/Denomas/freemind-ce/commit/506a0f1ccb1baf8556677de1cca91eaffdc466b8))
* modernize splash screen with anti-aliased progress bar ([861f85c](https://github.com/Denomas/freemind-ce/commit/861f85c33e58e811002b5e4e06000b5f5e2fadbb))


### Bug Fixes

* add explicit SpotBugs task dependencies on plugin modules ([54f96d8](https://github.com/Denomas/freemind-ce/commit/54f96d8dded24494f246460c0b963d1e07a79eec))
* add missing search and context graph plugin resource strings ([d2a68d3](https://github.com/Denomas/freemind-ce/commit/d2a68d3be0aa9a9d2f649d2db8ae1178e93d8091))
* enable SpotBugs HTML report generation ([4748858](https://github.com/Denomas/freemind-ce/commit/4748858709a0eccadce1a45683b94fd0eabfeb03))
* handle cross-drive relative URLs on Windows ([05bbb5b](https://github.com/Denomas/freemind-ce/commit/05bbb5be3365e9d612dcd26a5ad456702f81828c))
* include LaTeX equations in JSON, Markdown, and XML exports ([1c48294](https://github.com/Denomas/freemind-ce/commit/1c48294e8ae2276b0b8a201437f40c2ea3d242e1))
* internationalize hardcoded export error strings ([aa8974f](https://github.com/Denomas/freemind-ce/commit/aa8974f54ed52b16053126ca7b64d3353ce550ce))
* resolve all 43 failing tests after sourceSet separation ([8fe66c6](https://github.com/Denomas/freemind-ce/commit/8fe66c6881bac08086923d1405a25652b5a8daa1))
* resolve split pane hiding mindmap on first toggle ([97c5d60](https://github.com/Denomas/freemind-ce/commit/97c5d608967c8c54de1dc39aff15ac0adecc1d54))
* set proper FreeMind icon for window, dock, and Alt-Tab on all platforms ([846bb5c](https://github.com/Denomas/freemind-ce/commit/846bb5c817627433c959fbd9ccd5b32c535801a1))
* use platform-independent temp directory in tests ([4a706d2](https://github.com/Denomas/freemind-ce/commit/4a706d2a2a0bd67d388da010373bd616aada65f5))
* write non-ASCII characters as UTF-8 instead of XML entities ([e631737](https://github.com/Denomas/freemind-ce/commit/e631737a700d9b7f7e69ee7e77ef235d89bfc153))


### Code Refactoring

* extract HeadlessFreeMind from FreeMindMainMock into production code ([46a00fa](https://github.com/Denomas/freemind-ce/commit/46a00fadf34f6fe03264c35cf1722eeb4e25e9d9))


### Documentation

* add CONTRIBUTING.md, issue templates, PR template, and README badges ([d4828ae](https://github.com/Denomas/freemind-ce/commit/d4828ae68f1c9855bb23c65ded9ca69c8c9ff1da))
* add Serena code intelligence setup and SOP ([c096b2a](https://github.com/Denomas/freemind-ce/commit/c096b2a6424f7e3d97cc12a72bdbe9bf8b052b4a))
* update git remote references after origin/upstream rename ([ee921fb](https://github.com/Denomas/freemind-ce/commit/ee921fb5e82ad7d45b36c6d09802be76789fe485))


### Build System

* add cross-platform Makefile with auto JAVA_HOME detection ([442f776](https://github.com/Denomas/freemind-ce/commit/442f77685c199329ecbe50272a75ff6b9b12f6c5))
* add gitlint for Conventional Commits enforcement ([22ff4af](https://github.com/Denomas/freemind-ce/commit/22ff4af38764c39812639409f8c4e0ba7409d9e3))
* add SpotBugs exclude filter, OWASP suppressions, and jpackage file associations ([d9ec49b](https://github.com/Denomas/freemind-ce/commit/d9ec49b5734d69c465d271b61b632a5423217083))
* remove -CE suffix from version numbers ([eb54f27](https://github.com/Denomas/freemind-ce/commit/eb54f27e003e1258f9259ba0a2d5dd4210e0aade))
* upgrade Gradle 8.6→9.3.1, update all dependencies and CI actions ([5d72400](https://github.com/Denomas/freemind-ce/commit/5d7240083c31c04795c1c7bb1a95d95c21adf41f))


### CI/CD

* add build validation gate before release creation ([cf9775a](https://github.com/Denomas/freemind-ce/commit/cf9775a64fa0aad437831e753fd85c7bd0306e8c))
* add release-please automation and weekly OWASP security scan ([33d23ca](https://github.com/Denomas/freemind-ce/commit/33d23ca32ff3dfc1d8d5fec6b32ea085ef57d4d9))
* add SHA256 checksums, consistent naming, and workflow_dispatch to release ([7cccb1d](https://github.com/Denomas/freemind-ce/commit/7cccb1d02e19b421bd8b5a6e977d3438854d28f0))
* add test execution, Xvfb, test reporter, and JaCoCo to build workflow ([e9a1ec8](https://github.com/Denomas/freemind-ce/commit/e9a1ec858acf0e77ced3b460398944a1e9845545))
* migrate GitHub Actions to Node.js 24 ([ddd3112](https://github.com/Denomas/freemind-ce/commit/ddd3112cad473fd328a05a7ca8d782f48ac27052))
* remove duplicate build trigger on push to main ([cff99dd](https://github.com/Denomas/freemind-ce/commit/cff99dde6b2d07bbca2fc0bad4aaef9f6acf96ed))
* remove unsupported package-name input from release-please ([f093250](https://github.com/Denomas/freemind-ce/commit/f093250d858124a801055829902a064f7aad3971))
* use release-please config file for structured changelog ([9dc7fb6](https://github.com/Denomas/freemind-ce/commit/9dc7fb6a6b846a8116c94e68e74e9bcf8dc9eeb0))


### Tests

* activate property-based tests with real JAXB API integration ([2211e70](https://github.com/Denomas/freemind-ce/commit/2211e70c5cd0dc733d59afc073008dbdf39a42b6))
* separate test sourceSet from main and add JUnit 5 vintage engine ([98f716c](https://github.com/Denomas/freemind-ce/commit/98f716c238c98a17acaa57e7b8deb4a96d8f4411))
