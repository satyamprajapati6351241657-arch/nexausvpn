# CI/CD & Keystore Configuration Guide for NexusVPN

This project includes complete CI/CD automation to build, sign, and release both **APK** and **AAB (Android App Bundle)** files using GitHub Actions.

---

## 1. Keystore Details

The release keystore is configured in `app/build.gradle.kts`:

| Parameter | Value |
| :--- | :--- |
| **Keystore File** | `release.jks` (or fallback `my-upload-key.jks`) |
| **Keystore Format** | PKCS12 |
| **Key Alias** | `upload` |
| **Store / Key Password** | `android` (customizable via env vars) |
| **Key Algorithm** | RSA 2048-bit (SHA384withRSA) |
| **SHA-1 Fingerprint** | `BC:34:DE:0B:E4:8B:0E:7D:DB:55:49:85:EA:6F:F9:B8:F5:CC:BB:EB` |
| **SHA-256 Fingerprint** | `B7:40:B8:B4:2C:1E:40:98:31:49:6F:49:B1:7F:29:AD:44:42:C3:4B:DB:07:61:6C:6F:7B:73:91:20:9A:F9:C6` |

---

## 2. GitHub Secrets (Repository Settings -> Secrets and variables -> Actions)

Configure these repository secrets for production CI/CD signing:

1. **`KEYSTORE_BASE64`**: The base64 string from `release.jks.base64` (or created via `base64 -w 0 release.jks`).
2. **`STORE_PASSWORD`**: Keystore password (`android`).
3. **`KEY_PASSWORD`**: Key password (`android`).

*Note: The workflow has automatic fallbacks to `release.jks.base64` and password `'android'` so CI builds succeed out of the box even before adding secrets.*

---

## 3. GitHub Actions Workflow

The workflow file is located at `.github/workflows/build-release.yml`.

### Triggers:
- **Push** to `main` or `master` branches
- **Pull Request** to `main` or `master` branches
- **Tags** starting with `v*` (e.g., `v1.0.0`) -> automatically publishes a GitHub Release
- **Manual Trigger** (`workflow_dispatch`) from the Actions tab

### Generated Artifacts:
- **`app-release-aab`**: `app/build/outputs/bundle/release/app-release.aab` (Google Play Store format)
- **`app-release-apk`**: `app/build/outputs/apk/release/app-release-unsigned.apk` or signed APK
- **`app-debug-apk`**: `app/build/outputs/apk/debug/app-debug.apk` (Testing & Emulator)

---

## 4. Local CLI Build Commands

```bash
# Build Release AAB (for Google Play Console)
gradle :app:bundleRelease

# Build Release APK
gradle :app:assembleRelease

# Build Debug APK (instant testing)
gradle :app:assembleDebug
```
