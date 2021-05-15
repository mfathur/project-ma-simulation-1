package com.mfathur.projectmasimulation1.core.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Friend(
    val id: String,
    val photoUrl: String,
    val name: String,
    val phoneNumber: String,
    val origin: String
) : Parcelable