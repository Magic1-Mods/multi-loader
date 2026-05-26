package com.magic.loader

import android.util.Log

object Main {
    fun load(libraryPath: String) {
        System.load(libraryPath)
        Log.i(TAG, "Native library loaded: $libraryPath")
    }

    // JNI: Java_com_magic_loader_Main_start
    external fun start()

    private const val TAG = "MagicMain"
}
