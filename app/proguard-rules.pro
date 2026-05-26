# BlackBox
-keep class top.niunaijun.blackbox.** { *; }
-keep class top.niunaijun.blackbox.core.NativeCore { *; }
-keep class top.niunaijun.blackbox.BlackBoxCore { *; }
-keep class top.niunaijun.blackbox.app.BActivityThread { *; }
-keep class top.niunaijun.blackbox.app.configuration.** { *; }
-keep class top.niunaijun.blackbox.entity.** { *; }
-keep class top.niunaijun.blackbox.core.system.user.** { *; }
-keep class top.niunaijun.blackbox.fake.hook.** { *; }

# FreeReflection
-keep class org.lsposed.hiddenapibypass.** { *; }
-dontwarn org.lsposed.hiddenapibypass.**

# BlackReflection
-keep class top.niunaijun.jnihook.** { *; }
-keep class black.** { *; }