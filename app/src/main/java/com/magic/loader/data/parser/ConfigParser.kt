package com.magic.loader.data.parser

import android.content.Context
import com.google.gson.Gson
import com.magic.loader.data.model.GameConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigParser @Inject constructor(
    private val gson: Gson
) {
    fun parse(context: Context): GameConfig {
        val json = context.assets.open("config.json").bufferedReader().use { it.readText() }
        return gson.fromJson(json, GameConfig::class.java)
    }
}
