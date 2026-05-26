package com.magic.loader

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback
import top.niunaijun.blackbox.app.configuration.ClientConfiguration

@HiltAndroidApp
class MagicApplication : Application() {

    @Volatile
    private var startMod = false

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        runCatching {
            BlackBoxCore.get().doAttachBaseContext(base, object : ClientConfiguration() {
                override fun getHostPackageName(): String = base.packageName
            })
        }.onFailure { it.printStackTrace() }
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.instance.init(this)
        runCatching {
            BlackBoxCore.get().doCreate()
            addLifecycleCallback()
        }.onFailure { it.printStackTrace() }
    }

    private fun addLifecycleCallback() {
        runCatching {
            BlackBoxCore.get().addAppLifecycleCallback(object : AppLifecycleCallback() {
                override fun beforeCreateApplication(
                    packageName: String?, processName: String?, context: Context?, userId: Int
                ) {
                    Log.d(TAG, "beforeCreateApplication: $packageName")
                }

                override fun beforeApplicationOnCreate(
                    packageName: String?, processName: String?, application: Application?, userId: Int
                ) {
                    Log.d(TAG, "beforeApplicationOnCreate: $packageName")
                }

                override fun afterApplicationOnCreate(
                    packageName: String?, processName: String?, application: Application?, userId: Int
                ) {
                    Log.d(TAG, "afterApplicationOnCreate: $packageName")
                    runCatching {
                        application?.registerActivityLifecycleCallbacks(object :
                            ActivityLifecycleCallbacks {
                            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
                            override fun onActivityStarted(activity: Activity) {}
                            override fun onActivityResumed(activity: Activity) {
                                if (!startMod) {
                                    runCatching { Main.start() }.onFailure { it.printStackTrace() }
                                    startMod = true
                                }
                            }
                            override fun onActivityPaused(activity: Activity) {}
                            override fun onActivityStopped(activity: Activity) {}
                            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                            override fun onActivityDestroyed(activity: Activity) {
                                startMod = false
                            }
                        })
                    }.onFailure { it.printStackTrace() }
                }
            })
        }.onFailure { it.printStackTrace() }
    }

    companion object {
        private const val TAG = "MagicApplication"
    }
}
