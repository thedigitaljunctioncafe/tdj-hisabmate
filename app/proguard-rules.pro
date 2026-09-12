# Room Database rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Domain and Data Models (TDJ HisabMate)
-keep class com.thedigitaljunction.tdjhisabmate.data.model.** { *; }
-keepclassmembers class com.thedigitaljunction.tdjhisabmate.data.model.** { *; }
-keep class com.thedigitaljunction.tdjhisabmate.data.dao.** { *; }
-keep class com.thedigitaljunction.tdjhisabmate.data.preferences.** { *; }
-keep class com.thedigitaljunction.tdjhisabmate.hisabguard.** { *; }
-keep class com.thedigitaljunction.tdjhisabmate.update.** { *; }

# Kotlin Coroutines and Flow
-keepnames class kotlinx.coroutines.** { *; }

# Security and Utils
-keep class com.thedigitaljunction.tdjhisabmate.ui.util.** { *; }

