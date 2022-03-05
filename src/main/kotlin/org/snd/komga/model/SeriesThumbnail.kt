package org.snd.komga.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeriesThumbnail(
    val id: String,
    val seriesId: String,
    val type: String,
    val selected: Boolean,
)

