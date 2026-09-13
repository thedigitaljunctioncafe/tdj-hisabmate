# TDJ HisabMate — Your Smart Personal Finance Mate

TDJ HisabMate is a 100% offline-first, local-first personal finance application developed by **The Digital Junction**. It provides comprehensive money management, expense tracking, multi-wallet accounts, monthly budgets, savings goals, recurring transaction management, and the proactive **Hisab Guard** forgotten-expense prevention engine—with zero cloud lock-in, zero ads, and zero subscriptions.

---

## 📥 Download & Install TDJ HisabMate

TDJ HisabMate is distributed directly via **GitHub Releases** with ₹0 infrastructure cost.

### First Installation:
1. Open the [Latest GitHub Release](https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/latest).
2. Under **Assets**, download the release APK: `TDJ-HisabMate-v1.0.0.apk`.
3. Open the downloaded APK on your Android device to install.
4. If Android asks for permission to *"Install unknown apps"* from your browser or file manager:
   - Tap **Settings** on the Android system prompt.
   - Toggle **Allow from this source** to ON.
   - Return and tap **Install**.
5. Launch **TDJ HisabMate**.

### Future Updates:
TDJ HisabMate includes an **In-App GitHub Update Checker**:
- The app automatically checks GitHub Releases asynchronously.
- When a new version (e.g. `v1.0.1`) is available, a non-intrusive **Update Available** dialog appears with the release notes.
- Tapping **Update Now** downloads the updated APK directly from GitHub Releases.
- Installing the update seamlessly preserves all your existing accounts, transactions, budgets, goals, and PIN settings.

*Security Notice:* Always download TDJ HisabMate exclusively from the official GitHub repository (`https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases`). Never download APKs from untrusted third-party websites.

---

## 🔒 Privacy & Architecture

- **100% Local Storage:** All accounts, transactions, and settings are stored locally on your device in a private SQLite database managed by Android Jetpack Room.
- **Zero Telemetry / Zero Trackers:** No analytics SDKs, advertising IDs, or tracking networks.
- **No Financial Data Transmission:** The `INTERNET` permission is used strictly for public GitHub Release update checks (`api.github.com`). Financial data is never transmitted.
- **Offline Resilient:** Fully functional without an active internet connection.
- **Data Ownership:** One-tap full JSON database backups and CSV transaction exports via the Android system file picker.

Read our full [Privacy Policy](PRIVACY_POLICY.md) for details.

---

## 🛠️ Building From Source

### Prerequisites:
- JDK 21
- Android SDK (API 36 / Android 16 compatible)
- Gradle 8.13+

### Build Commands:
```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run Android Lint
./gradlew lintDebug

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease

# Build Release App Bundle (AAB)
./gradlew bundleRelease
```

---

## 📄 License & Disclaimer

**Developer:** The Digital Junction  
**Contact:** `thedigitaljunctioncafe@gmail.com`  

*Disclaimer:* TDJ HisabMate is a personal finance tracking and organization tool. It does not provide professional financial, investment, tax, legal, or credit advice.
