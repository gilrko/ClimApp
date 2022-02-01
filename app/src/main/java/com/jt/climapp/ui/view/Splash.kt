package com.jt.climapp.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.jt.climapp.R
import java.util.*
import kotlin.concurrent.schedule


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        Timer("Loading", false).schedule(2000) {
            if (account != null) {
                goToMain()
            } else {
                goToLogin()
            }
        }
    }

    fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToMain() {
        val intent = Intent(this, Weather::class.java)
        startActivity(intent)
        finish()
    }
}