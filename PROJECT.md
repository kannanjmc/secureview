The key issue is that **SecureView should not continuously reapply masking while the user is typing or editing.** It should mask on file opening, allow the user to reveal and edit the actual value, and restore masking when the file is closed and reopened.

I've updated the README to make this behavior explicit and distinguish editing from masking.

# SecureView Password Masking

**SecureView** is an IntelliJ IDEA plugin that visually masks password and secret values in editor files without modifying the actual document contents. It helps prevent accidental exposure of sensitive information and shoulder-surfing while coding.

## Features

- **Automatic Secret Detection:** Identifies common sensitive property names and patterns, including `password`, `secret`, `api_key`, `token`, `credential`, and more.
- **Multiple File Formats:** Supports `.properties`, YAML, JSON, XML, Java/Kotlin assignments, and other common configuration formats.
- **Automatic Visual Masking:** Displays detected secret values as `********` when a file is opened.
- **Non-Destructive Masking:** Does not replace or modify the actual secret value just to display the masked representation.
- **Click to Reveal and Edit:** Clicking a masked value reveals the actual value, allowing the developer to edit it normally.
- **Editing-Friendly Behavior:** Once revealed, the secret remains visible while the developer is actively editing it. The plugin must not repeatedly reapply masking during typing or ordinary document changes.
- **Automatic Remasking:** When a file is closed and reopened, previously revealed secrets are automatically masked again.
- **Editor Lifecycle Management:** Handles editor creation, document changes, and editor disposal without unnecessarily interrupting the editing experience.

## Expected Behavior

The primary goal is to provide seamless password protection without interfering with normal development workflows.

### 1. Open a File

When a developer opens a file containing sensitive values, SecureView automatically detects and masks those values.

Example:

Original file content:

```
username=admin
password=MySecret123
api_key=ABC123XYZ
```

What the developer sees:

```
username=admin
password=********
api_key=********
```

### 2. Click and Reveal

When the developer clicks a masked password or secret value, SecureView reveals the actual value.

The developer can now see and edit the original value.

```
password=MySecret123
```

### 3. Edit Without Interruption

Once a secret is revealed:

- The developer can modify the value normally.
- The caret and keyboard navigation should work normally.
- The plugin must not mask the value again after every keystroke.
- Document change events must not trigger unnecessary remasking.
- The editing experience should remain consistent with a normal IntelliJ editor.

**Important:** Masking should not interfere with active editing.

### 4. Close and Reopen the File

When the developer closes the file and opens it again, SecureView should automatically detect the secret and mask it.

Example:

```
# Task: Fix SecureView IntelliJ Plugin Masking and Editing Bugs

You are working on my existing IntelliJ IDEA plugin, **SecureView Password Masking**.

GitHub repository: https://github.com/kannanjmc/secureview

Your task is to investigate and fix the existing masking implementation carefully. Do not rewrite the entire plugin or introduce unnecessary architectural changes.

## 1. Expected Functional Behavior

The plugin should work as follows:

**A. When opening a file**

Automatically detect and mask ALL password and secret values.

Example original file:

```properties
username=admin
secret=sdfsdfdsfsdfsdf
password=MyPassword123
api_key=ABC123XYZ
token=XYZ987654
```

Expected editor display:

```properties
username=admin
secret=********
password=********
api_key=********
token=********
```

**B. When clicking a masked value**

When the user clicks a masked value:

- Reveal the actual secret.
- Allow normal editing.
- Allow inserting, deleting, and replacing characters.
- Maintain normal caret positioning and keyboard navigation.
- Do not interfere with IntelliJ editor operations.

**C. While editing**

This is critical.

- Once a secret is revealed, keep it visible while editing.
- Do not remask after every keystroke.
- Do not recreate folding regions unnecessarily.
- Do not trigger full-file masking on every document change.
- Do not lose the user's edits.
- Other secrets should remain masked independently.

**D. When closing and reopening a file**

When the user closes a file and opens it again:

- Automatically detect all secrets again.
- Mask all detected values.
- Previously revealed values should not remain visible.
- Preserve the actual saved document content.

## 2. Current Bugs to Investigate

### Bug 1: First secret remains unmasked

Example:

```properties
secret=sdfsdfdsfsdfsdf
password=MyPassword123
api_key=ABC123XYZ
token=XYZ987654
```

Currently, the first `secret` value remains visible while the other passwords are masked.

Investigate why the first detected secret is not receiving a valid masking region.

Check:

- Detection results for all matching secrets.
- Start and end offsets.
- Fold region creation.
- Region validity and overlap.
- Whether iteration skips the first match.
- Whether the first detected value is incorrectly excluded.

### Bug 2: Masking happens during editing

Currently, the plugin repeatedly masks values even when the developer is actively editing them.

Investigate:

- Document listeners.
- Fold-region recreation.
- Editor events.
- Reveal state management.
- Document modification callbacks.
- Editor lifecycle handling.

A revealed secret must remain visible while being edited.

### Bug 3: Multiple passwords are not handled consistently

Every detected secret must have its own independent masking and reveal state.

Revealing or editing one password must not affect the other passwords.

## 3. Files to Inspect

Start by examining these existing components:

1. `EditorMaskingService.kt`
2. `PasswordDetector.kt`
3. `SecureViewEditorFactoryListener.kt`
4. `RevealSecretAction.kt`
5. `SecureViewSettings.kt`
6. `plugin.xml`

Also inspect existing tests and other related classes before changing anything.

## 4. Technical Requirements

- Use IntelliJ Platform SDK supported APIs.
- Preserve the existing Kotlin implementation.
- Use appropriate editor-specific masking state.
- Handle multiple secrets in a single file.
- Ensure correct document offsets after edits.
- Avoid overlapping or invalid fold regions.
- Avoid unnecessary fold-region recreation.
- Clean up editor-specific state when editors are released.
- Ensure multiple editor instances are handled correctly.
- Do not modify actual document content just to display masking.
- Do not introduce security claims that the plugin cannot guarantee.

Use IntelliJ folding APIs appropriately and verify their behavior against the current target
```

This behavior should work even when the developer has modified and saved the secret value.

### Expected Lifecycle

| User action              | Expected behavior                     |
| ------------------------ | ------------------------------------- |
| Open file                | Automatically mask secrets            |
| Click masked value       | Reveal actual value                   |
| Edit revealed value      | Keep it visible while editing         |
| Type multiple characters | Do not repeatedly remask              |
| Save file                | Preserve actual document content      |
| Close file               | Dispose editor-specific masking state |
| Reopen file              | Automatically mask secrets again      |

## Technology Stack

- **Language:** Kotlin
- **Framework:** IntelliJ Platform SDK
- **Build Tool:** Gradle with Kotlin DSL
- **Target:** IntelliJ IDEA 2024.1+ (build 241+)

## Key Components

1. **PasswordDetector:** Identifies sensitive values using heuristic pattern matching.
2. **EditorMaskingService:** Manages fold regions, masking behavior, and temporary reveal state.
3. **SecureViewEditorFactoryListener:** Handles editor creation and release events.
4. **SecureViewSettings:** Manages plugin configuration and master password settings.

## Implementation Considerations

The masking implementation should carefully manage IntelliJ editor folding and document lifecycle events.

Important considerations:

- Avoid recreating fold regions unnecessarily during document changes.
- Maintain the revealed state while a secret is actively being edited.
- Avoid overlapping or invalid fold regions.
- Recalculate secret locations safely when document content changes.
- Do not introduce recursive document change listeners.
- Ensure editor disposal releases temporary masking state.
- Reapply masking when a file is opened in a new editor session.
- Preserve the original document text throughout the masking and reveal process.

The plugin should prioritize a stable editing experience over aggressive remasking.

## Important Security Notes

SecureView is a visual display aid, not a security boundary.

Actual secret values remain accessible through document operations, search, clipboard, and other IDE functionality.

It is designed to reduce accidental visual exposure, not to provide encryption or prevent programmatic access to sensitive values.

## Installation

```
# Build the plugin
./gradlew buildPlugin

# Install from disk
# Settings → Plugins → Install Plugin from Disk
# Select the generated secureview-*.zip
```

## Development

```
# Build
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew buildPlugin

# Test
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew test
```

## Project Structure

```
secureview/
├── src/
│   ├── main/
│   │   ├── kotlin/com/kannanjmc/secureview/
│   │   │   ├── EditorMaskingService.kt
│   │   │   ├── PasswordDetector.kt
│   │   │   ├── SecureViewEditorFactoryListener.kt
│   │   │   ├── SecureViewSettings.kt
│   │   │   ├── SecureViewConfigurable.kt
│   │   │   └── RevealSecretAction.kt
│   │   └── resources/META-INF/
│   │       └── plugin.xml
│   └── test/
│       └── kotlin/com/kannanjmc/secureview/
│           └── PasswordDetectorTest.kt
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── README.md
├── LICENSE
├── CHANGELOG.md
└── PROJECT.md
```

## Version History

See CHANGELOG.md for detailed version history.

## License

MIT License. See LICENSE for details.

## Links

- **GitHub:** [https://github.com/kannanjmc/secureview](https://github.com/kannanjmc/secureview)
- **JetBrains Marketplace:** [https://plugins.jetbrains.com/plugin/35696-secureview-password-masking](https://plugins.jetbrains.com/plugin/35696-secureview-password-masking)

## **Important recommendation for fixing the actual bugs**

The README now defines the expected behavior, but the implementation needs to follow the same lifecycle.

I recommend these rules for `EditorMaskingService.kt`:

1. Create masking regions when an editor opens.
2. When a user reveals a secret, track that specific secret as temporarily revealed.
3. Do not recreate its folding region while the user is editing.
4. Do not run full-file masking after every document change.
5. Dispose temporary reveal state when the editor closes.
6. Recalculate and mask secrets when the file is reopened.

**One important distinction:** Closing and reopening should mask the value again, but the actual edited value must remain intact in the file.
