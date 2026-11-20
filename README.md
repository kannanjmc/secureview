# SecureView Password Masking

A Kotlin IntelliJ Platform plugin that visually masks password and secret values in editor tabs without changing file contents.

## Features

- **Automatic Detection**: Detects common secret key patterns including:
  - `password`, `passwd`, `pwd`
  - `secret`, `client_secret`
  - `api_key`, `access_token`, `auth_token`, `refresh_token`, `token`
  - `private_key`, `credential`, `credentials`

- **Multi-Format Support**: Works with:
  - `.properties` files (key=value)
  - YAML (key: value)
  - JSON (quoted keys/values)
  - XML elements (`<password>value</password>`)
  - Java/Kotlin assignments

- **Visual Masking**: Displays detected secret values as `********` in the editor
- **Non-Destructive**: Does not modify the actual file contents
- **Reveal on Demand**: Press `Cmd+Option+Shift+R` (Mac) or right-click → "Reveal Secret" to temporarily show a secret
- **Reliable Masking**: Secrets are masked again when closing and reopening files

## Requirements

- JDK 21
- IntelliJ IDEA 2025.3 or a compatible later build
- Gradle (the Gradle wrapper is included)

## Building

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Clean build
./gradlew clean buildPlugin
```

The distributable ZIP is written under `build/distributions/`.

## Installation

### Local Installation

1. Build with `./gradlew buildPlugin`
2. In IntelliJ IDEA open Settings/Preferences → Plugins → gear icon → Install Plugin from Disk
3. Select the ZIP from `build/distributions/` and restart the IDE

### Marketplace Installation

Available on the [JetBrains Marketplace](https://plugins.jetbrains.com/) (coming soon)

## Usage

Once installed, the plugin automatically masks detected secret values in your editor tabs.

### Masking

Secrets are automatically masked when you open a file. The masking applies to:
- Property files
- Configuration files
- Source code with secret assignments

### Revealing Secrets

To temporarily reveal a masked secret:

1. Place your cursor on the masked secret
2. Press `Cmd+Option+Shift+R` (Mac) or `Ctrl+Alt+Shift+R` (Windows/Linux)
3. Or right-click on the secret and select "Reveal Secret"

The secret stays revealed while you edit it. Other secrets remain masked independently.

### Re-masking

When you close and reopen a file, all secrets are automatically masked again - even if you had revealed them before closing.

## Detection Coverage

- Assignment forms: `key=value`, `key: value`, quoted JSON keys/values, Java/Kotlin assignments
- XML element text: `<password>value</password>`
- Single-line values only

## Important Limitations

⚠️ **Security Warning**: This is a display aid, not a security boundary.

- Detection is heuristic, not a full parser. It can miss unusual formats and can flag comments or unrelated fields.
- Visual masking is not encryption or access control. The real value remains in the document and may still appear in:
  - Search results
  - Copied text
  - Previews
  - External tools
  - Logs
  - Other plugins
- A masked value can be revealed by pressing the reveal shortcut or using the context menu. Do not treat this as a security boundary.

## Development

### Project Structure

```
src/
├── main/
│   ├── kotlin/com/kannanjmc/secureview/
│   │   ├── EditorMaskingService.kt    # Core masking logic
│   │   ├── PasswordDetector.kt        # Secret detection patterns
│   │   ├── SecureViewEditorFactoryListener.kt  # Editor lifecycle hooks
│   │   └── RevealSecretAction.kt      # Reveal secret action
│   └── resources/META-INF/
│       └── plugin.xml                 # Plugin manifest
└── test/
    └── kotlin/com/kannanjmc/secureview/
        └── PasswordDetectorTest.kt    # Unit tests
```

### Publishing

To publish a new version:

1. Update the version in `build.gradle.kts`
2. Update `CHANGELOG.md`
3. Set the JetBrains Marketplace token:
   ```bash
   export JETBRAINS_MARKETPLACE_TOKEN=your_token_here
   ```
4. Publish:
   ```bash
   ./gradlew publishPlugin
   ```

## License

MIT License - see [LICENSE](LICENSE) file for details

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and feature requests, please use the [GitHub Issues](https://github.com/kannanjmc/secureview/issues) page.

## Acknowledgments

Built with the [IntelliJ Platform Gradle Plugin](https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html)
