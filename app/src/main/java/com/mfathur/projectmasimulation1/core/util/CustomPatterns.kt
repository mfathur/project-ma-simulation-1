package com.mfathur.projectmasimulation1.core.util

import java.util.regex.Pattern

object CustomPatterns {

    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                ".{8,}" +
                "$"
    )
}