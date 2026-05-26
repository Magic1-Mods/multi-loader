package com.magic.loader.data.model

data class GameConfig(
    val games: List<Game>
)

data class Game(
    val name: String,
    val packageName: String,
    val version: String,
    val library: String,
    val updateUrl: String? = null,
    val description: String? = null
)
