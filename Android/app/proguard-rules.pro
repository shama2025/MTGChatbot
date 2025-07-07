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

#-----------------------------------------------------------------------
# START: Custom Proguard Rules for Retrofit, Gson, and Generic Types
#-----------------------------------------------------------------------

# Gson uses generic type information stored in a class file when working with fields.
# Proguard removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Keep platform-related parts of Retrofit, especially the generic type information.
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8

# Retain declared checked exceptions for use by a Proxy instance (used by Retrofit).
-keepattributes Exceptions

# Keep Retrofit classes and interfaces
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

# Keep your API interface
-keep class com.mashaffer.mymtgchatbot.network.** { *; }

# Keep your data classes (models) used in API responses, as they need to be deserialized
-keep class com.mashaffer.mymtgchatbot.model.** { *; }

#-----------------------------------------------------------------------
# END: Custom Proguard Rules
#-----------------------------------------------------------------------

