# Google Play Console - App Access (Test Account & Review Credentials)

Jab aap Google Play Console par app publish/upload karte hain, toh Play Console **Policy > App Access** section me Google Play review team ke liye test credentials maangta hai taaki reviewer bina block hue app ke saare screens aur features check kar sake.

Yahan woh saari details hain jo aapko Play Console me fill karni hain:

---

## 1. Quick Test Account Credentials (Play Console me daalne ke liye)

Play Console me **"App access" (ऐप एक्सेस)** select karke **"All or some functionality is restricted"** chuniye aur **"Add instructions"** par click karke ye fill kijiye:

| Field Name | Value to Enter |
| :--- | :--- |
| **Instruction / Name** | `Reviewer Test Account (NexusVPN)` |
| **Username / Email** | `tester@nexusvpn.com` *(ya `user@nexusvpn.com`)* |
| **Password** | `securePass123` *(ya `Tester@2026`)* |
| **Phone number / OTP** | `None (OTP is not required. Any email + password logs in instantly, or use 1-click "Continue as Guest")` |
| **Any other instructions** | `This app has instant offline-first authentication with pre-filled demo credentials on the Login screen. No 2FA/SMS OTP is required. The reviewer can also click "Continue as Guest" directly to bypass login.` |

---

## 2. OTP ke Baare Me (Does the App Require OTP?)

- **NO OTP Required**: Is app me kisi SMS/Phone OTP ya Email OTP ki zaroorat nahi hai.
- Login screen par pehle se hi email aur password pre-filled rehta hai:
  - **Pre-filled Email**: `user@nexusvpn.com`
  - **Pre-filled Password**: `securePass123`
- Agar reviewer chahe toh bina login kiye **"Continue as Guest"** button daba kar bhi direct Home screen aur VPN features test kar sakta hai.
- Sign Up screen se koi bhi naya account bhi bina OTP ke 2 second me ban jaata hai.

---

## 3. Google Play Console Form Step-by-Step

Jab aap **Google Play Console** me jaate hain:
1. Left menu me **Policy and programs** > **App content** par click karein.
2. **App access** option par **Start** ya **Manage** par click karein.
3. Option 2 select karein: **"All or some functionality is restricted"**
4. Click **"+ Add new instructions"**:
   - **Title**: `Standard Test Account`
   - **Username/Email**: `user@nexusvpn.com`
   - **Password**: `securePass123`
   - **Does your app require 2-step verification (2FA/OTP)?**: Select **No**
   - **Notes for reviewer**: `Pre-filled credentials are on the login screen. Also, "Continue as Guest" allows full access without any credentials.`
5. Click **Save**.

---

## 4. Keystore & Release Build Info (Aapke Pass Ready Hai)

- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Keystore File**: `release.jks` / `my-upload-key.jks`
- **Alias**: `upload`
- **Password**: `android`
- **SHA-256**: `B7:40:B8:B4:2C:1E:40:98:31:49:6F:49:B1:7F:29:AD:44:42:C3:4B:DB:07:61:6C:6F:7B:73:91:20:9A:F9:C6`
