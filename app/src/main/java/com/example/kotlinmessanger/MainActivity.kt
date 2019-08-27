package com.example.kotlinmessanger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        register_button_register.setOnClickListener{
            performRegister()
        }

        already_have_account_textView.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity")

            // Launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_button_register.setOnClickListener{

        }

    }

    private fun performRegister(){
        val email = email_editText_register.text.toString()
        val password = password_editText_register.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password",
                Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is : $email")
        Log.d("MainActivity", "Password is : $password")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(!it.isSuccessful) return@addOnCompleteListener

            // else if successfull
            Log.d("Main", "Successfully created a user")
        }
            .addOnFailureListener{
                Log.d("Main", "Failed to create a user : ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}",
                    Toast.LENGTH_SHORT).show()

            }
    }
}
