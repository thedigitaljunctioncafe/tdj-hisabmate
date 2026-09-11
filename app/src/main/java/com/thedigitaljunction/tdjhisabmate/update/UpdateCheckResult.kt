package com.thedigitaljunction.tdjhisabmate.update

/**
 * State representing the outcome of checking for an update.
 */
sealed class UpdateCheckResult {
    data object Idle : UpdateCheckResult()
    data object Checking : UpdateCheckResult()
    data class UpdateAvailable(
        val release: GitHubRelease,
        val currentVersion: String,
        val newVersion: String
    ) : UpdateCheckResult()
    data class UpToDate(
        val currentVersion: String
    ) : UpdateCheckResult()
    data class Error(
        val message: String,
        val isNetworkError: Boolean = true
    ) : UpdateCheckResult()
}
