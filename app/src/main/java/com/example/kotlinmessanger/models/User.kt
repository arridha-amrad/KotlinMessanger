package com.example.kotlinmessanger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// to overcome missing object in parcel
@Parcelize

class User(val uid:String, val username:String, val profileImageUrl: String): Parcelable {
  constructor(): this("", "", "")
}