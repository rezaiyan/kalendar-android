# ===========================================
# Kalendar App ProGuard Rules
# ===========================================

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===========================================
# Android Framework Rules
# ===========================================

# Keep all Android framework classes
-keep class android.** { *; }
-keep class androidx.** { *; }

# Keep all widget-related classes
-keep class * extends android.appwidget.AppWidgetProvider { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidget { *; }

# ===========================================
# Jetpack Compose Rules
# ===========================================

# Keep Compose runtime
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep Compose UI
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep Glance Widget classes
-keep class androidx.glance.** { *; }
-keep class androidx.glance.appwidget.** { *; }
-keep class androidx.glance.material3.** { *; }

# ===========================================
# DataStore Rules
# ===========================================

# Keep DataStore classes
-keep class androidx.datastore.** { *; }
-keep class androidx.datastore.preferences.** { *; }

# ===========================================
# Persian Date Library Rules
# ===========================================

# Keep Persian Date library classes
-keep class saman.zamani.persiandate.** { *; }
-keep class saman.zamani.persiandate.PersianDate { *; }
-keep class saman.zamani.persiandate.PersianDateFormat { *; }

# ===========================================
# Kalendar App Specific Rules
# ===========================================

# Keep all classes in the main package
-keep class com.alirezaiyan.kalendar.** { *; }

# Keep widget receiver classes
-keep class * extends com.alirezaiyan.kalendar.widget.ModernCalendarWidgetReceiver { *; }

# Keep widget action classes
-keep class com.alirezaiyan.kalendar.widget.actions.** { *; }

# Keep calendar system classes
-keep class com.alirezaiyan.kalendar.calendar.** { *; }

# Keep data model classes
-keep class com.alirezaiyan.kalendar.data.** { *; }

# Keep MainActivity
-keep class com.alirezaiyan.kalendar.MainActivity { *; }

# ===========================================
# Reflection and Serialization Rules
# ===========================================

# Keep classes that might be accessed via reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===========================================
# Kotlin Specific Rules
# ===========================================

# Keep Kotlin metadata
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# ===========================================
# Widget Specific Rules
# ===========================================

# Keep widget provider classes
-keep class * extends android.appwidget.AppWidgetProvider {
    public void onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[]);
    public void onEnabled(android.content.Context);
    public void onDisabled(android.content.Context);
    public void onDeleted(android.content.Context, int[]);
}

# ===========================================
# Optimization Rules
# ===========================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove debug code
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(...);
    static void checkNotNullParameter(...);
    static void checkReturnedValueIsNotNull(...);
    static void checkNotNullReturnValue(...);
    static void checkFieldIsNotNull(...);
    static void checkNotNullField(...);
}

# ===========================================
# Additional Optimization
# ===========================================

# Remove unused code
-dontwarn **
-ignorewarnings

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep constructors of classes that are used via reflection
-keepclassmembers class * {
    public <init>(...);
}

# Keep classes that implement Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep classes that implement Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}