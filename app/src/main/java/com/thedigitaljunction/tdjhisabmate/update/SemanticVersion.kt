package com.thedigitaljunction.tdjhisabmate.update

/**
 * Represents a Semantic Version (e.g. 1.0.0, v1.0.10, 2.0.0-rc1).
 * Provides robust, non-string-based version comparison:
 * 1.0.10 is recognized as newer than 1.0.9.
 * Leading 'v' or 'V' prefixes and whitespace are stripped automatically.
 */
data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null,
    val rawString: String = "$major.$minor.$patch${if (preRelease != null) "-$preRelease" else ""}"
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        if (this.major != other.major) {
            return this.major.compareTo(other.major)
        }
        if (this.minor != other.minor) {
            return this.minor.compareTo(other.minor)
        }
        if (this.patch != other.patch) {
            return this.patch.compareTo(other.patch)
        }

        // Standard SemVer: Release version without pre-release is higher than pre-release version
        // e.g. 1.0.0 > 1.0.0-beta
        return when {
            this.preRelease == null && other.preRelease != null -> 1
            this.preRelease != null && other.preRelease == null -> -1
            this.preRelease != null && other.preRelease != null -> this.preRelease.compareTo(other.preRelease)
            else -> 0
        }
    }

    fun isNewerThan(other: SemanticVersion): Boolean = this > other

    fun isOlderThan(other: SemanticVersion): Boolean = this < other

    companion object {
        private val VERSION_REGEX = Regex("""^[vV]?(\d+)(?:\.(\d+))?(?:\.(\d+))?(?:-(.+))?$""")

        /**
         * Parses a version string like "1.0.0", "v1.2.3", "v1.0", "2.0.0-rc1" safely.
         * Returns null on completely non-version strings.
         */
        fun parseOrNull(versionStr: String?): SemanticVersion? {
            if (versionStr.isNullOrBlank()) return null
            val trimmed = versionStr.trim()
            val match = VERSION_REGEX.matchEntire(trimmed) ?: return null

            val major = match.groupValues[1].toIntOrNull() ?: 0
            val minor = if (match.groupValues[2].isNotEmpty()) match.groupValues[2].toIntOrNull() ?: 0 else 0
            val patch = if (match.groupValues[3].isNotEmpty()) match.groupValues[3].toIntOrNull() ?: 0 else 0
            val preRelease = if (match.groupValues[4].isNotEmpty()) match.groupValues[4] else null

            return SemanticVersion(
                major = major,
                minor = minor,
                patch = patch,
                preRelease = preRelease,
                rawString = trimmed
            )
        }

        /**
         * Parses a version string or returns a fallback 0.0.0 version without throwing exceptions.
         */
        fun parseOrDefault(versionStr: String?, default: SemanticVersion = SemanticVersion(0, 0, 0)): SemanticVersion {
            return parseOrNull(versionStr) ?: default
        }
    }
}
