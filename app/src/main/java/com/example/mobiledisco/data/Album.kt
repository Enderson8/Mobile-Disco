package com.example.mobiledisco.data

data class Album(
    val name: String,
    val artist: String,
    val cover: ByteArray?,
    val songs: List<Song>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (name != other.name) return false
        if (artist != other.artist) return false
        if (cover != null) {
            if (other.cover == null) return false
            if (!cover.contentEquals(other.cover)) return false
        } else if (other.cover != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        return result
    }
}
