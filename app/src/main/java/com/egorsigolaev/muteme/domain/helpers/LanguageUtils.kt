package com.egorsigolaev.muteme.domain.helpers

import java.util.*

object LanguageUtils {
    fun getLanguage(): String{
        return Locale.getDefault().language
    }
}