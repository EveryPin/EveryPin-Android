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

# Kakao
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# androidx credentials
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

# Kotlin Serialization
# Serializer for classes with named companion objects are retrieved using `getDeclaredClasses`.
# If you have any, replace classes with those containing named companion objects.
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.
-keep @kotlinx.serialization.Serializable class * { *; }
-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}