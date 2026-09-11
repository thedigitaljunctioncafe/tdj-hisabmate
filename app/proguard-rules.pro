# Room Database rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Domain and Data Models (TDJ HisabMate)
-keep class com.thedigitaljunction.tdjhisabmate.data.model.** { *; }
-keepclassmembers class com.thedigitaljunction.tdjhisabmate.data.model.** { *; }
-keep class com.thedigitaljunction.tdjhisabmate.hisabguard.** { *; }

# Kotlin Coroutines and Flow
-keepnames class kotlinx.coroutines.** { *; }

# Security and Utils
-keep class com.thedigitaljunction.tdjhisabmate.ui.util.** { *; }

