## -----------------------------------------
## Preserve line number info for crash logs
## -----------------------------------------
#-keepattributes SourceFile,LineNumberTable
#
## -----------------------------------------
## Preserve important metadata for reflection & generics
## -----------------------------------------
#-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault, Exceptions
#
## -----------------------------------------
## Fix for Gson + TypeToken (generic type reflection)
## -----------------------------------------
#-keep class com.google.gson.reflect.TypeToken
#-keep class * extends com.google.gson.reflect.TypeToken
#
## -----------------------------------------
## Keep your data models used with Gson
## -----------------------------------------
#-keep class com.mashaffer.mymtgchatbot.model.** { *; }
#
#-keepclassmembers class com.mashaffer.mymtgchatbot.model.** {
#    <fields>;
#}
#
#-keepclasseswithmembers class com.mashaffer.mymtgchatbot.model.** {
#    <init>();
#}
#
## -----------------------------------------
## Retrofit (core + converters + network interfaces)
## -----------------------------------------
#-keep class retrofit2.** { *; }
#-keep interface retrofit2.** { *; }
#-dontwarn retrofit2.Platform
#-dontwarn retrofit2.Platform$Java8
#
#-keep class retrofit2.converter.gson.** { *; }
#
## Keep your Retrofit API interfaces
#-keep class com.mashaffer.mymtgchatbot.network.** { *; }
#-keep interface com.mashaffer.mymtgchatbot.network.** { *; }
#
## -----------------------------------------
## Kotlin coroutines support (if used with Retrofit)
## -----------------------------------------
#-dontwarn kotlinx.coroutines.**
#-keep class kotlinx.coroutines.** { *; }
#
## -----------------------------------------
## Support for @Keep annotation (AndroidX)
## -----------------------------------------
#-keep @androidx.annotation.Keep class * { *; }
#-keepclasseswithmembers class * {
#    @androidx.annotation.Keep *;
#}
#
## -----------------------------------------
## Optional: Uncomment if you get XML or reflection-based crashes
## -----------------------------------------
## -keep class com.mashaffer.mymtgchatbot.ui.** { *; }
## -keep class com.mashaffer.mymtgchatbot.chat.** { *; }
## -keep class com.mashaffer.mymtgchatbot.util.** { *; }
## -keep class com.mashaffer.mymtgchatbot.GradientBackground { *; }
#
## -----------------------------------------
## Prevent stripping enum values (if used via name())
## -----------------------------------------
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## -----------------------------------------
## Suppress some noise (optional)
## -----------------------------------------
#-dontnote okhttp3.**
#-dontwarn okio.**
