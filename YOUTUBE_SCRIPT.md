# SecureView Password Masking - YouTube Video Script

## Video Title Ideas
- "How I Protect Passwords in My Code Editor (IntelliJ Plugin)"
- "Stop Shoulder-Surfing: Mask Secrets in IntelliJ IDEA"
- "SecureView: Visual Password Masking for Developers"
- "Protect Sensitive Data While Coding - IntelliJ Plugin Demo"

## Video Script

### Scene 1: Introduction (0:00 - 0:30)
**Visual:** Developer sitting at desk, looking at screen showing code with visible passwords
**Narration:**
> "Hey everyone! Today I'm showing you how I protect passwords and secrets in my code editor. 
> 
> You know how uncomfortable it feels when someone walks by and sees your API keys or passwords? 
> Or when you're screen sharing and don't want to expose sensitive data?
> 
> I built an IntelliJ plugin called SecureView that masks secrets visually - without changing your actual files."

### Scene 2: Problem Demonstration (0:30 - 1:00)
**Visual:** Open a file with visible secrets
```properties
# application.properties
username=admin
password=MySecretPassword123
api_key=sk_live_abc123xyz
secret=supersecretvalue
token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
```
**Narration:**
> "Here's a typical config file. See how the password, API key, and token are all visible? 
> Anyone walking by can see these. Even when I'm screen sharing, this is exposed.
> 
> This is a real problem for developers who work in open offices or share screens frequently."

### Scene 3: Install Plugin (1:00 - 1:30)
**Visual:** Show IntelliJ Settings → Plugins → Install from Disk
**Narration:**
> "Let me show you how to install SecureView. It's super simple - just download the plugin ZIP and install it from IntelliJ's plugin settings.
> 
> Once installed, it automatically detects secrets in your files. No configuration needed."

### Scene 4: Masking Demo (1:30 - 2:30)
**Visual:** Open the same file - secrets now show as ********
```properties
username=admin
password=********
api_key=********
secret=********
token=********
```
**Narration:**
> "Look at that! All the secrets are now masked as ********. 
> 
> But here's the cool part - your actual file hasn't changed. The real values are still there, just hidden visually.
> 
> This protects you from shoulder-surfing and accidental exposure during screen shares."

### Scene 5: Reveal Functionality (2:30 - 3:30)
**Visual:** Place cursor on masked secret, press Cmd+Option+Shift+R
**Narration:**
> "But what if you need to see the actual value? Just place your cursor on the masked secret and press Cmd+Option+Shift+R. 
> 
> Or right-click and select 'Reveal Secret'. 
> 
> The secret is revealed only when you need it - temporary and controlled."

### Scene 6: Editing While Revealed (3:30 - 4:30)
**Visual:** Edit the revealed secret, show it stays revealed
**Narration:**
> "You can edit the revealed secret normally - insert, delete, replace characters. 
> 
> The secret stays revealed while you're editing. No annoying remasking while you work. 
> 
> Other secrets remain masked independently."

### Scene 7: Close and Reopen (4:30 - 5:30)
**Visual:** Close file, reopen it - secrets are masked again
**Narration:**
> "When you close the file and reopen it, all secrets are masked again automatically. 
> 
> Even if you revealed them before closing. This ensures you're always protected by default."

### Scene 8: Multiple Secrets Demo (5:30 - 6:30)
**Visual:** Show file with multiple secrets on different lines
```java
public class Config {
    private String password = "********";
    private String apiKey = "********";
    private String secret = "********";
    private String normalText = "This is not masked";
}
```
**Narration:**
> "It works across different file formats - Java, properties, YAML, JSON, XML. 
> 
> It detects password, secret, api_key, token, and other common patterns. 
> 
> Each secret is masked independently."

### Scene 9: Security Disclaimer (6:30 - 7:00)
**Visual:** Show disclaimer text
**Narration:**
> "Important note: This is a visual masking tool, not encryption. 
> 
> The actual values remain in your file and are accessible to search, copy/paste, and other IDE features. 
> 
> It's designed to prevent accidental visual exposure, not to provide security against determined attackers."

### Scene 10: Conclusion (7:00 - 7:30)
**Visual:** Show GitHub repo and plugin download
**Narration:**
> "SecureView is open source and available on GitHub. 
> 
> If you work with sensitive data and want to prevent shoulder-surfing, give it a try. 
> 
> Link in the description. Thanks for watching!"

## B-Roll Ideas
- Developer typing code
- Close-up of screen showing masked secrets
- Screen sharing scenario
- Office environment with people walking by
- IntelliJ IDE interface
- Plugin installation process
- Keyboard shortcut demonstration

## Text Overlays
- "Shoulder-surfing protection"
- "Visual masking only - not encryption"
- "Cmd+Option+Shift+R to reveal"
- "Works with Java, properties, YAML, JSON, XML"
- "Open source on GitHub"

## Tags/Hashtags
#IntelliJ #Java #Security #Privacy #DeveloperTools #OpenSource #Kotlin #Programming
