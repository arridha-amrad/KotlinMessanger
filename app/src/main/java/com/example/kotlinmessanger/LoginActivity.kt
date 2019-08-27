package com.example.kotlinmessanger

import android.content.Intent
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

            val intent = Intent(this, LatestMessagesActivity::class.java)
            // to clear previous activity and start new activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        backToRegister_textView.setOnClickListener{
            finish()
        }


    }
}