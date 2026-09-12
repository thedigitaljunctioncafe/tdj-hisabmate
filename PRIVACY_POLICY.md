# Privacy Policy for TDJ HisabMate

**Last Updated:** September 13, 2026  
**Application:** TDJ HisabMate — Your Smart Personal Finance Mate  
**Developer:** The Digital Junction  
**Contact Email:** thedigitaljunctioncafe@gmail.com  
**Repository:** [thedigitaljunctioncafe/tdj-hisabmate](https://github.com/thedigitaljunctioncafe/tdj-hisabmate)  

---

## 1. Overview & Commitment to Privacy

TDJ HisabMate is built on an uncompromising **Offline-First and Local-First Privacy Architecture**. We believe your financial data—income, expenses, account balances, budgets, notes, and savings goals—is strictly private to you.

The application does not require user registration, login credentials, phone numbers, or email addresses to function.

---

## 2. Information We Do NOT Collect

- **No Financial Data Collection:** We do not collect, transmit, store, or sell any of your financial records (e.g., transaction amounts, payee names, account numbers, budget amounts).
- **No Personal Identifiers:** We do not collect your name, phone number, physical address, or device hardware IDs.
- **No Third-Party Trackers / Analytics SDKs:** The app contains zero commercial telemetry SDKs, ad tracking networks, or behavioral analytics services.
- **No Cloud Synchronization Servers:** We do not maintain or operate remote database servers for your financial records.

---

## 3. How Your Data is Stored (Local On-Device Only)

All user-generated records are stored exclusively on your device within a secured SQLite database managed by Android Jetpack Room:
- **Accounts & Wallets:** Account names, types, and initial balances.
- **Transactions:** Dates, amounts, categories, notes, merchants, and tags.
- **Budgets & Goals:** Monthly spending limits and savings targets.
- **Recurring Transactions:** Schedules and frequencies.
- **Hisab Guard Records:** Daily review completion timestamps and forgotten expense pattern indicators.
- **Security PIN:** Stored as a salted SHA-256 hash inside Android Private Preferences DataStore.

Your data never leaves your device unless you explicitly choose to export a backup.

---

## 4. Network Connections & Update Checker

TDJ HisabMate requires the `android.permission.INTERNET` permission solely for an optional, user-accessible **GitHub Release Update Checker**:
- **Endpoint:** Queries public GitHub Releases (`api.github.com`).
- **Data Transmitted:** Standard HTTP GET request headers (User-Agent with current app version name).
- **Zero Financial Data:** No financial data, account information, or personal metrics are ever included in update check requests.
- **Offline Resilience:** If internet connectivity is unavailable, the update checker fails silently and gracefully without disrupting any core app features.

---

## 5. Data Backup, Export & Portability

- **User-Initiated Backups:** You may generate a full JSON backup or CSV export at any time via **More > Backup & Data Portability**.
- **Storage Location:** Backups are handled via the Android system file picker (Storage Access Framework / Android Sharesheet). You decide where files are saved or shared.
- **No Silent Uploads:** TDJ HisabMate never automatically or silently uploads your backup files to any remote service.

---

## 6. Data Deletion & User Control

- **Granular Deletion:** You can delete individual transactions, accounts, budgets, goals, or recurring schedules at any time within the app UI.
- **Full App Deletion:** Clearing app storage in Android Settings or uninstalling the application immediately and permanently removes all stored data from your device.
- **No Account System:** Because no remote accounts exist, there is no remote account deletion request required.

---

## 7. Informational Financial Disclaimer

TDJ HisabMate is a personal bookkeeping, expense tracking, and organizational tool. It does not provide professional financial, investment, tax, legal, or credit advice.

---

## 8. Children's Privacy

TDJ HisabMate is a general-audience personal finance utility. We do not knowingly collect any data from children or any other users.

---

## 9. Contact Us

If you have questions about this Privacy Policy or TDJ HisabMate, please contact:
- **Developer:** The Digital Junction  
- **Email:** thedigitaljunctioncafe@gmail.com  
- **GitHub:** [https://github.com/thedigitaljunctioncafe/tdj-hisabmate](https://github.com/thedigitaljunctioncafe/tdj-hisabmate)
