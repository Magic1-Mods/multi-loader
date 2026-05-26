package com.magic.loader

import android.content.Context
import android.content.Intent
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private var previousHandler: Thread.UncaughtExceptionHandler? = null
    private var context: Context? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val sw = StringWriter()
            sw.write("=== CRASH REPORT ===\n")
            sw.write("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
            sw.write("Thread: ${thread.name} (${thread.id})\n\n")
            sw.write("${throwable.javaClass.name}: ${throwable.message}\n\n")
            sw.write("STACK TRACE:\n")
            throwable.stackTrace.forEach { sw.write("\tat $it\n") }
            var cause = throwable.cause
            var depth = 0
            while (cause != null && depth < 10) {
                sw.write("\nCaused by: ${cause.javaClass.name}: ${cause.message}\n")
                cause.stackTrace.forEach { sw.write("\tat $it\n") }
                cause = cause.cause
                depth++
            }
            sw.write("\n=== END ===\n")

            val intent = Intent(context, CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("crash_text", sw.toString())
            }
            context?.startActivity(intent)
        } catch (_: Exception) {
        }

        previousHandler?.uncaughtException(thread, throwable)
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    companion object {
        val instance: CrashHandler by lazy { CrashHandler() }
    }
}
