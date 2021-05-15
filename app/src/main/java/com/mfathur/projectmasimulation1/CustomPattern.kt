package com.mfathur.projectmasimulation1

import java.util.regex.Pattern

object CustomPattern {

    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                ".{8,}" +
                "$"
    )
}