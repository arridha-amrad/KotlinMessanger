package com.example.kotlinmessanger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener{
            val email = email_editText_login.text.toString()
            val password = password_editText_login.text.toString()
            Log.d("Login", "Email : $email")
            Log.d("Login", "Password: $password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        }

        backToRegister_textView.setOnClickListener{
            finish()
        }


    }
}