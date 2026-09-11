package com.thedigitaljunction.tdjhisabmate

import com.thedigitaljunction.tdjhisabmate.update.GitHubRelease
import com.thedigitaljunction.tdjhisabmate.update.SemanticVersion
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GitHubReleaseParserTest {

    @Test
    fun `parse valid GitHub release JSON with APK asset`() {
        val json = """
            {
                "tag_name": "v1.0.1",
                "name": "TDJ HisabMate v1.0.1",
                "body": "• Fixed budget progress bar\n• Improved export speed",
                "html_url": "https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/tag/v1.0.1",
                "draft": false,
                "prerelease": false,
                "published_at": "2026-09-11T12:00:00Z",
                "assets": [
                    {
                        "name": "TDJ-HisabMate-v1.0.1.apk",
                        "size": 18500000,
                        "browser_download_url": "https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/download/v1.0.1/TDJ-HisabMate-v1.0.1.apk"
                    }
                ]
            }
        """.trimIndent()

        val jsonObject = JSONObject(json)
        val release = GitHubRelease.fromJsonObject(jsonObject)

        assertEquals("v1.0.1", release.tagName)
        assertEquals("TDJ HisabMate v1.0.1", release.name)
        assertEquals("• Fixed budget progress bar\n• Improved export speed", release.displayReleaseNotes)
        assertEquals("https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/tag/v1.0.1", release.htmlUrl)
        assertFalse(release.isDraft)
        assertFalse(release.isPrerelease)
        assertEquals("https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/download/v1.0.1/TDJ-HisabMate-v1.0.1.apk", release.apkDownloadUrl)
        assertEquals("TDJ-HisabMate-v1.0.1.apk", release.apkFileName)
        assertEquals(18500000L, release.apkSize)

        val parsedVersion = release.parsedVersion
        assertNotNull(parsedVersion)
        assertEquals(1, parsedVersion!!.major)
        assertEquals(0, parsedVersion.minor)
        assertEquals(1, parsedVersion.patch)
    }

    @Test
    fun `parse release JSON with no APK asset falls back gracefully`() {
        val json = """
            {
                "tag_name": "v1.0.2",
                "name": "TDJ HisabMate v1.0.2",
                "body": "",
                "html_url": "https://github.com/thedigitaljunctioncafe/tdj-hisabmate/releases/tag/v1.0.2",
                "draft": false,
                "prerelease": false,
                "assets": []
            }
        """.trimIndent()

        val release = GitHubRelease.fromJsonObject(JSONObject(json))
        assertNull(release.apkDownloadUrl)
        assertNull(release.apkFileName)
        assertEquals("Bug fixes and performance improvements.", release.displayReleaseNotes)
    }

    @Test
    fun `update decision correctly distinguishes newer from current or older versions`() {
        val current = SemanticVersion.parseOrNull("1.0.0")!!
        val newer = SemanticVersion.parseOrNull("1.0.1")!!
        val same = SemanticVersion.parseOrNull("v1.0.0")!!
        val older = SemanticVersion.parseOrNull("0.9.9")!!

        assertTrue(newer.isNewerThan(current))
        assertFalse(same.isNewerThan(current))
        assertFalse(older.isNewerThan(current))
    }
}
