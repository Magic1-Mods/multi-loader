package com.magic.loader.util

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoExtractor @Inject constructor() {

    fun getNativeLibDir(context: Context): File {
        val dir = File(context.filesDir, "native_libs")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun extractSo(context: Context, assetPath: String, targetName: String): File {
        val targetDir = getNativeLibDir(context)
        val targetFile = File(targetDir, targetName)
        if (targetFile.exists()) return targetFile
        context.assets.open(assetPath).use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        targetFile.setExecutable(true)
        return targetFile
    }

    fun loadSo(libraryPath: String): Boolean = try {
        System.load(libraryPath)
        true
    } catch (e: UnsatisfiedLinkError) {
        false
    }
}
