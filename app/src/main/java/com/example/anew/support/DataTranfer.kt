package com.example.anew.support

import com.example.anew.model.User

object DataTranfer {
    // save user to global when load data
    val userCache = mutableMapOf<String, User>()
}
