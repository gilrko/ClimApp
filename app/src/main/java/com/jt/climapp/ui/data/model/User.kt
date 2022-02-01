package com.jt.climapp.ui.data.model

import io.realm.RealmObject

open class User () : RealmObject() {
    var name: String? = null
    var email: String? = null
    var photo: String? = null
}