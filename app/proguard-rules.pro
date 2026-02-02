# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# BASE RULES
# Keep annotations & signatures
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Kotlin metadata (VERY IMPORTANT)
-keep class kotlin.Metadata { *; }

# Suppress warnings
-dontwarn kotlin.**
-dontwarn org.jetbrains.annotations.**

# Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }

# Tooling & previews (safe even if stripped in release)
-dontwarn androidx.compose.ui.tooling.**


# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }

# Generated Hilt components
-keep class **_HiltComponents { *; }
-keep class **_HiltModules { *; }

# Android entry points
-keep class * extends androidx.activity.ComponentActivity
-keep class * extends androidx.fragment.app.Fragment
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.app.Application



# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson (VERY IMPORTANT)
-keep class com.google.gson.** { *; }

# Keep model classes used in JSON
-keep class com.theralieve.** { *; }

# Gson reflection
#-keepclassmembers class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Entities & DAOs
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**


# DataStore
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Coil
-keep class coil.** { *; }
-dontwarn coil.**


# Square Mobile Payments SDK
-keep class com.squareup.sdk.** { *; }
-dontwarn com.squareup.sdk.**

# Mock reader UI (debug safe)
-keep class com.squareup.sdk.reader.mock.** { *; }

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Navigation
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# Splash Screen
-keep class androidx.core.splashscreen.** { *; }

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-dontwarn java.sql.JDBCType
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory




