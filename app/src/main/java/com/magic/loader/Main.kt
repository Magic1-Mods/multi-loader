package com.magic.loader

import android.util.Log

object Main {
    private var initialized = false

    fun init(libraryPath: String): Boolean {
        return try {
            System.load(libraryPath)
            initialized = true
            Log.i(TAG, "Native library loaded: $libraryPath")
            true
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native library: $libraryPath", e)
            false
        }
    }

    fun start() {
        if (!initialized) {
            Log.w(TAG, "start() called but native library not initialized")
            return
        }
        try {
            nativeStart()
        } catch (e: Exception) {
            Log.e(TAG, "nativeStart() failed", e)
        }
    }

    private external fun nativeStart()

    private const val TAG = "MagicMain"
}
