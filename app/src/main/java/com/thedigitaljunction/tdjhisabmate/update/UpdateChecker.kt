package com.thedigitaljunction.tdjhisabmate.update

import android.util.Log
import com.thedigitaljunction.tdjhisabmate.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

/**
 * Lightweight, 100% offline-resilient GitHub release update checker.
 * Uses public GitHub REST API endpoint with ₹0 infrastructure dependencies.
 */
class UpdateChecker(
    private val endpointUrl: String = DEFAULT_GITHUB_API_URL
) {
    companion object {
        const val TAG = "UpdateChecker"
        const val DEFAULT_GITHUB_API_URL = "https://api.github.com/repos/thedigitaljunctioncafe/tdj-hisabmate/releases/latest"
        const val GITHUB_REPO_URL = "https://github.com/thedigitaljunctioncafe/tdj-hisabmate"
        const val GITHUB_RELEASES_URL = "https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases"
        const val CONNECT_TIMEOUT_MS = 8000
        const val READ_TIMEOUT_MS = 8000
    }

    suspend fun checkForUpdate(
        currentVersionName: String = BuildConfig.VERSION_NAME
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(endpointUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "TDJ-HisabMate-Android/${BuildConfig.VERSION_NAME}")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseBody = reader.use { it.readText() }
                val jsonObject = JSONObject(responseBody)
                val release = GitHubRelease.fromJsonObject(jsonObject)

                // Skip drafts or pre-releases
                if (release.isDraft || release.isPrerelease) {
                    return@withContext UpdateCheckResult.UpToDate(currentVersion = currentVersionName)
                }

                val remoteVersion = release.parsedVersion
                val localVersion = SemanticVersion.parseOrNull(currentVersionName)

                if (remoteVersion != null && localVersion != null) {
                    if (remoteVersion.isNewerThan(localVersion)) {
                        return@withContext UpdateCheckResult.UpdateAvailable(
                            release = release,
                            currentVersion = currentVersionName,
                            newVersion = remoteVersion.rawString
                        )
                    } else {
                        return@withContext UpdateCheckResult.UpToDate(currentVersion = currentVersionName)
                    }
                } else {
                    return@withContext UpdateCheckResult.UpToDate(currentVersion = currentVersionName)
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                // No release published yet on GitHub repository
                return@withContext UpdateCheckResult.UpToDate(currentVersion = currentVersionName)
            } else {
                Log.w(TAG, "GitHub API returned HTTP $responseCode")
                return@withContext UpdateCheckResult.Error(
                    message = "Server returned code $responseCode",
                    isNetworkError = true
                )
            }
        } catch (e: UnknownHostException) {
            Log.d(TAG, "Offline or unreachable: ${e.message}")
            return@withContext UpdateCheckResult.Error(
                message = "Unable to connect. Please check your internet connection.",
                isNetworkError = true
            )
        } catch (e: SocketTimeoutException) {
            Log.d(TAG, "Connection timed out: ${e.message}")
            return@withContext UpdateCheckResult.Error(
                message = "Connection timed out. Please try again later.",
                isNetworkError = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check for updates", e)
            return@withContext UpdateCheckResult.Error(
                message = e.localizedMessage ?: "Unable to check for updates right now.",
                isNetworkError = false
            )
        } finally {
            connection?.disconnect()
        }
    }
}
