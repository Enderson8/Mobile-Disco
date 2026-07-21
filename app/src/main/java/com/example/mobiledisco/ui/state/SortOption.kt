package com.example.mobiledisco.ui.state

enum class SortField {
    NAME,
    ARTIST,
    ALBUM,
    IMPORT_DATE,
    MOST_PLAYED
}

enum class SortDirection {
    ASCENDING,
    DESCENDING
}

data class SortOrder(
    val field: SortField = SortField.NAME,
    val direction: SortDirection = SortDirection.ASCENDING
)
