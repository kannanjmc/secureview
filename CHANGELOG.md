# Changelog

All notable changes to this project will be documented in this file.

## [1.0.1] - 2026-10-31

### Changed
- Support all IntelliJ IDEA versions from 2023.1 onwards (sinceBuild: 231)
- Removed version restriction to support future IntelliJ IDEA releases

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
