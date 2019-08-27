package com.example.kotlinmessanger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        register_button_register.setOnClickListener{
            performRegister()
        }

        already_have_account_textView.setOnClickListener{
            Log.d("RegisterActivity", "Try to show login activity")

            // Launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_button_register.setOnClickListener{
            Log.d("RegisterActivity", "Try to select photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    var selectedPhotoUri:Uri? = null
    // after selecting the photo, the image will be displayed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected photo was
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            select_photo_imageview_register.setImageBitmap(bitmap)

            select_photo_button_register.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
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

        Log.d("RegisterActivity", "Email is : $email")
        Log.d("RegisterActivity", "Password is : $password")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
            if(!it.isSuccessful) return@addOnCompleteListener

            // else if successfull
            Log.d("RegisterActivity", "Successfully created a user")

            uploadImageToFirebaseStorage()
        }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed to create a user : ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}",
                    Toast.LENGTH_SHORT).show()

            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                Log.d("RegisterActivity", "Successfully uploaded the image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Log.d("RegisterActivity", "Failed")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
// before using database, set the rules write/read -> true
        var uid = FirebaseAuth.getInstance().uid ?: ""
        var ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_editText_register.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "User saved to firebase")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                // to clear previous activity and start new activity
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}

class User(val uid:String, val username:String, val profileImageUrl: String) {
    constructor(): this("", "", "")
}
