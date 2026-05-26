package com.magic.loader.data.model

import com.google.gson.annotations.SerializedName

data class GameConfig(
    val games: List<Game>
)

data class Game(
    val name: String,
    @SerializedName("package")
    val packageName: String,
    val version: String,
    val library: String,
    val updateUrl: String? = null,
    val description: String? = null
)
