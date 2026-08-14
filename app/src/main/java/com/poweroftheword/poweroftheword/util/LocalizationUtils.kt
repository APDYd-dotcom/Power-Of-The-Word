package com.poweroftheword.poweroftheword.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocalizationUtils {
    fun getLocalizedContext(context: Context, language: String): Context {
        val locale = when (language.uppercase()) {
            "FR" -> Locale.FRENCH
            "SW" -> Locale("sw")
            "KI", "RN" -> Locale("rn")
            else -> Locale.ENGLISH
        }

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    fun getLocalizedString(context: Context, resId: Int, language: String, vararg formatArgs: Any): String {
        val localizedContext = getLocalizedContext(context, language)
        return localizedContext.getString(resId, *formatArgs)
    }
}
