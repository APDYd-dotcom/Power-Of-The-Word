# Hilt
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Activity
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.viewmodel.ViewModel
-keep class androidx.hilt.lifecycle.ViewModelFactoryModules { *; }
-keep class com.google.dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }

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
