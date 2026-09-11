# TDJ HisabMate — Public Release & Distribution Guide

**TDJ HisabMate — Your Smart Personal Finance Mate**
Developed by **The Digital Junction**
Repository: [thedigitaljunctioncafe/tdj-hisabmate](https://github.com/thedigitaljunctioncafe/tdj-hisabmate)

---

## 🚀 Public Release Infrastructure

TDJ HisabMate uses **GitHub Releases** as its primary distribution channel. This aligns with the **₹0 Infrastructure Cost Guarantee**, ensuring completely transparent, direct, open-source-friendly distribution with zero recurring server costs.

---

## 📦 Versioning Standard (Semantic Versioning)

The project adheres to **Semantic Versioning 2.0.0** (`MAJOR.MINOR.PATCH`):
- `MAJOR`: Significant architectural changes or major redesigns.
- `MINOR`: New features (e.g., new analytics charts, currency support, export formats).
- `PATCH`: Bug fixes, security patches, and performance optimizations.

### Version Alignment:
Whenever publishing a new release, ensure version values are synchronized across:
1. `app/build.gradle.kts`:
   ```kotlin
   versionCode = 1        // Monotonically increasing integer
   versionName = "1.0.0"  // Semantic version string
   ```
2. Git tag:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0 - Public Production Release"
   git push origin v1.0.0
   ```

---

## 🛠️ Build Commands

### 1. Build Production Debug APK (for Testing)
```bash
gradle :app:assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Build Production Release APK
```bash
gradle :app:assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### 3. Build Production Android App Bundle (AAB)
```bash
gradle :app:bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

---

## 🏷️ Publishing a GitHub Release

1. **Tag the commit:**
   ```bash
   git tag -a v1.0.0 -m "TDJ HisabMate v1.0.0"
   git push origin v1.0.0
   ```
2. **Navigate to GitHub Releases:**
   `https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/new`
3. **Select Tag:** `v1.0.0`
4. **Release Title:** `TDJ HisabMate v1.0.0 — Public Release`
5. **Attach Binary Assets:**
   - Upload `TDJ-HisabMate-v1.0.0.apk` (renamed from `app-release.apk` or `app-debug.apk`)
   - (Optional) Upload `TDJ-HisabMate-v1.0.0.aab`
6. **Publish Release.**

---

## 🔄 In-App Update Notification System

TDJ HisabMate features an integrated, non-intrusive update checker:
- **API Endpoint:** `https://api.github.com/repos/thedigitaljunctioncafe/tdj-hisabmate/releases/latest`
- **Cadence:** Auto-checked at most once every 24 hours in the background.
- **Manual Trigger:** Available anytime under **More > About TDJ HisabMate > Check for Updates**.
- **Offline Resilient:** If the device is offline or GitHub is unreachable, the app operates 100% smoothly with zero errors, popups, or blockages.
- **Zero Privacy Leak:** No user IDs, telemetry, or financial data are sent in update requests.
