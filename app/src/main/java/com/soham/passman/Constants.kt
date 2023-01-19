package com.soham.passman

import com.google.firebase.auth.FirebaseUser
import com.soham.passman.adapter.RecyclerViewAdapter
import com.soham.passman.data.Data
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object Constants {
    var list: ArrayList<Data>? = null
    lateinit var homeAdapter: RecyclerViewAdapter
}