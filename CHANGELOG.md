# Changelog

All notable changes to this project will be documented in this file.

## [1.0.6] - 2026-11-19

### Added
- `RevealSecretAction` - Allows users to reveal masked secrets
- Keyboard shortcut: `Cmd+Option+Shift+R` (Mac) / `Ctrl+Alt+Shift+R` (Windows/Linux)
- Context menu option: Right-click on masked secret → "Reveal Secret"

### Changed
- Replaced folding-based masking with `MarkupModel` highlighters + `CustomHighlighterRenderer`
- Secrets are now masked using transparent text + custom `********` rendering
- No longer relies on IntelliJ's fold state restoration (fixes reopening issues)
- Masking is applied on file open, not during editing (prevents interference)
- Revealed secrets stay revealed while editing

### Fixed
- Secrets are masked again when closing and reopening files
- No more fold state restoration issues
- Reveal functionality works reliably

## [1.0.13] - 2026-11-12

### Changed
- Switched from IntelliJ fold regions to inlay hints for secret masking
- Inlay hints render masked text directly over secrets, avoiding fold state restoration issues
- Secrets now always show as masked (*****) regardless of file open/close cycles
- Fixed issue where revealed secrets were not being masked again when file was reopened

## [1.0.12] - 2026-11-12

### Fixed
- Fixed issue where revealed secrets were not being masked again when file was closed and reopened
- Added second pass to force fold regions to collapse after IntelliJ restores fold state
- Secrets now always start collapsed when opening a file

## [1.0.3] - 2026-10-31

### Changed
- Support IntelliJ IDEA 2024.1 and later (sinceBuild: 241)

## [1.0.2] - 2026-10-31

### Changed
- Support IntelliJ IDEA 2024.1 and later (sinceBuild: 241)

## [1.0.1] - 2026-10-31

### Changed
- Support IntelliJ IDEA 2025.3 and later (sinceBuild: 253)

## [1.0.0] - 2026-10-31

### Added
- Initial release of SecureView Password Masking
- Visual masking of secret values in editor tabs
- Support for .properties, YAML, JSON, XML, Java/Kotlin assignments
- Detection of common secret key patterns (password, secret, api_key, token, etc.)
- Automatic folding of detected secret values to display as ********

### Known Limitations
- Detection is heuristic and may miss unusual formats
- Can flag comments or unrelated fields
- Visual masking is not encryption or access control
- Folding may be unavailable in some editor types
- Single-line values only
