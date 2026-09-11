package com.thedigitaljunction.tdjhisabmate.update

import org.json.JSONObject

/**
 * Data model representing a GitHub Release fetched from the public GitHub REST API.
 */
data class GitHubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String,
    val isDraft: Boolean,
    val isPrerelease: Boolean,
    val publishedAt: String,
    val apkDownloadUrl: String?,
    val apkFileName: String?,
    val apkSize: Long
) {
    val parsedVersion: SemanticVersion?
        get() = SemanticVersion.parseOrNull(tagName)

    val displayReleaseNotes: String
        get() = if (body.isNotBlank()) body.trim() else "Bug fixes and performance improvements."

    companion object {
        fun fromJsonObject(obj: JSONObject): GitHubRelease {
            val tagName = obj.optString("tag_name", "")
            val name = obj.optString("name", tagName)
            val body = obj.optString("body", "")
            val htmlUrl = obj.optString("html_url", "https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases")
            val isDraft = obj.optBoolean("draft", false)
            val isPrerelease = obj.optBoolean("prerelease", false)
            val publishedAt = obj.optString("published_at", "")

            var apkUrl: String? = null
            var apkName: String? = null
            var apkSize: Long = 0L

            val assets = obj.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.optJSONObject(i) ?: continue
                    val assetName = asset.optString("name", "")
                    val downloadUrl = asset.optString("browser_download_url", "")
                    if (assetName.endsWith(".apk", ignoreCase = true) && downloadUrl.isNotBlank()) {
                        apkUrl = downloadUrl
                        apkName = assetName
                        apkSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            return GitHubRelease(
                tagName = tagName,
                name = name,
                body = body,
                htmlUrl = htmlUrl,
                isDraft = isDraft,
                isPrerelease = isPrerelease,
                publishedAt = publishedAt,
                apkDownloadUrl = apkUrl,
                apkFileName = apkName,
                apkSize = apkSize
            )
        }
    }
}
