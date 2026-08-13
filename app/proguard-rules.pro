# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }
-dontwarn java.lang.management.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Media3
-keep class androidx.media3.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.util.TableInfo { *; }
-keep class androidx.room.util.TableInfo$Column { *; }
-keep class androidx.room.util.TableInfo$ForeignKey { *; }
-keep class androidx.room.util.TableInfo$Index { *; }

# Data Models (keep for serialization)
-keep @kotlinx.serialization.Serializable class com.poweroftheword.poweroftheword.domain.model.** { *; }

# Koin
-keep class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module
-keepattributes *Annotation*
