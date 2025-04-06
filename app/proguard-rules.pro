# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep the application class and its methods
-keep class dev.broken.app.vibe.** { *; }

# Keep required classes for ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}

# Keep Fragment default constructor for AndroidX
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>();
}

# Keep Android lifecycle methods
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <methods>;
}

# Material Design components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Basic Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions

# Kotlin specific
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception