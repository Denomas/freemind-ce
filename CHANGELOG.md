# Changelog

## [1.3.1](https://github.com/Denomas/freemind-ce/compare/v1.3.0...v1.3.1) (2026-03-14)


### Bug Fixes

* v1.3.1 packaging fixes, bug fixes, accessibility, and polish ([#18](https://github.com/Denomas/freemind-ce/issues/18)) ([92a4d32](https://github.com/Denomas/freemind-ce/commit/92a4d32fd4f64e4850ff435e2e61ac051e428ced))

## [1.3.0](https://github.com/Denomas/freemind-ce/compare/v1.2.0...v1.3.0) (2026-03-13)


### Features

* activate LaTeX Article/Book exports and add redo.svg icon ([cf844bd](https://github.com/Denomas/freemind-ce/commit/cf844bdaa2690e083a11a424a35133927267614f))
* add isWellformedXml method to HtmlTools ([0ff1464](https://github.com/Denomas/freemind-ce/commit/0ff14648c45d14cf4fa937d27ea0af448a7f625f))
* add Windows .ico icon for jpackage installer branding ([74875c3](https://github.com/Denomas/freemind-ce/commit/74875c35e794f5739f8856028c2559dadb43de87))
* convert all translation files from \uXXXX escapes to native UTF-8 ([6a0e674](https://github.com/Denomas/freemind-ce/commit/6a0e6746fcd6a7253500569b61ad0eed2db0d18f))


### Bug Fixes

* **ci:** add workflow_dispatch trigger to release-please ([c25bee3](https://github.com/Denomas/freemind-ce/commit/c25bee3ff5fb55c7d398b9d6177f58443959c41c))
* **ci:** grant checks:write permission for reusable workflow calls ([c6fbea3](https://github.com/Denomas/freemind-ce/commit/c6fbea3bed7878b0b0ee9e6c34245809d4b5360b))
* **ci:** limit screenshot summary to stay under GitHub 1024KB limit ([ed037d2](https://github.com/Denomas/freemind-ce/commit/ed037d288bcacbc8cfa252a57125694966cb0305))
* **ci:** move build.yml permissions to job-level for workflow_call compatibility ([fba70f5](https://github.com/Denomas/freemind-ce/commit/fba70f5fa1b233ce8f5637ce46a18e2eb523bb00))
* **ci:** replace base64 images with text summary in job summary ([050aa12](https://github.com/Denomas/freemind-ce/commit/050aa1292002dfb35429bd042ec6c1f3543b5f2b))
* **ci:** save showcase window screenshots as JPEG for smaller summary size ([51b2179](https://github.com/Denomas/freemind-ce/commit/51b2179d12432b6009ae6b1e7815026e3fb41195))
* **ci:** simplify screenshot summary with debug logging ([de9171e](https://github.com/Denomas/freemind-ce/commit/de9171ebe06b90121957c49d3d0f6c57ecf50cc4))
* **ci:** update ossf/scorecard-action SHA after upstream tag re-push ([8686541](https://github.com/Denomas/freemind-ce/commit/86865412e43544131d03a05a4a938f972a70e981))
* **ci:** use correct base64 flag for macOS in screenshot embedding ([455688a](https://github.com/Denomas/freemind-ce/commit/455688a47fa0b28dfb9a58a61a419bc7e9484a7b))
* disable Gradle cache for testGui task ([b60dc69](https://github.com/Denomas/freemind-ce/commit/b60dc693e0624773d5ff35a6c634c3aca33f0b5a))
* enforce UTF-8 encoding across all file I/O operations ([8411430](https://github.com/Denomas/freemind-ce/commit/8411430b79cc810b76551ab3b601230088f5c3e5))
* restore original .mm file versions and add legacy version conversion tests ([075c622](https://github.com/Denomas/freemind-ce/commit/075c62207596738c246c1ba4826c6f4c574ac226))
* **security:** move all workflow permissions to job-level ([df3dadc](https://github.com/Denomas/freemind-ce/commit/df3dadc8f8084fddaf0dc367ca696dd58d2a5b9d))
* **security:** pin GitHub Actions to SHA hashes and add permissions ([599189b](https://github.com/Denomas/freemind-ce/commit/599189b6debcae81fff2d1e8dee0d3e686f9d864))
* **security:** remove binary artifacts and harden workflow permissions ([41bc5ca](https://github.com/Denomas/freemind-ce/commit/41bc5ca2759895539ed96644242e457738b3f7cf))
* **security:** upgrade Lucene from 4.6.0 to 9.12.3 ([b477629](https://github.com/Denomas/freemind-ce/commit/b4776291e60230fce7687fe2f26e8e6a234c04c3)), closes [#11](https://github.com/Denomas/freemind-ce/issues/11)
* update showcase .mm files to version 1.1.0 and ensure .freemind dir exists ([f74754e](https://github.com/Denomas/freemind-ce/commit/f74754e04b4bfbbe8a8eb88a44b9168db55d7886))

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


### Dependencies

* bump org.gradle.toolchains.foojay-resolver-convention from 0.8.0 to 1.0.0 ([#7](https://github.com/Denomas/freemind-ce/issues/7)) ([4e51fde](https://github.com/Denomas/freemind-ce/commit/4e51fde1b473f9f67b70341c59e72e3d630e4a19))
* bump the apache group with 3 updates ([#4](https://github.com/Denomas/freemind-ce/issues/4)) ([e0963e2](https://github.com/Denomas/freemind-ce/commit/e0963e2f05051e8841492007bc305ed2ef73eb43))
* bump xml-apis:xml-apis from 1.4.01 to 2.0.2 ([#6](https://github.com/Denomas/freemind-ce/issues/6)) ([3b69d0f](https://github.com/Denomas/freemind-ce/commit/3b69d0f802e16fbc309d67cf01a9d9d9433f65fc))
