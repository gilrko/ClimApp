package com.jt.climapp.ui.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.jt.climapp.R
import com.jt.climapp.databinding.ActivityLoginBinding
import com.jt.climapp.ui.data.model.User
import io.realm.Realm
import java.lang.Exception


class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    private lateinit var binding: ActivityLoginBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            signIn(googleSignInClient)
        }
    }

    private fun signIn(googleSignInClient: GoogleSignInClient) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            Log.w(TAG, "signInResult:success code=" + account.email)
            saveUser(account)
            nextActivity()
        } catch (e: ApiException) {
            Toast.makeText(this, "Ocurrió un error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUser(account: GoogleSignInAccount) {
        try {
            realm.beginTransaction()
            val user = User()
            user.email = account.email.toString()
            user.name = account.displayName.toString()
            user.photo = account.photoUrl.toString()

            realm.copyToRealm(user)
            realm.commitTransaction()

            Toast.makeText(this, "Bienvenido " + account.displayName, Toast.LENGTH_SHORT).show()


        } catch (e:Exception){
            var asd = e.message
        }
    }

    fun nextActivity() {
        val intent = Intent(this, Splash::class.java)
        startActivity(intent)
        finish()
    }
}


