package com.example.kotlinmessanger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kotlinmessanger.R
import com.example.kotlinmessanger.registerLogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.kotlinmessanger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LatestMessagesActivity : AppCompatActivity() {

  companion object{
    var currentUser: User? = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_latest_messages)

    fetchCurrentUser()

//  check to see wheater the user login or not
    verifyUserLogin()
  }

  private fun fetchCurrentUser() {
    var uid = FirebaseAuth.getInstance().uid
    var ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {

      override fun onDataChange(p0: DataSnapshot) {
        currentUser = p0.getValue(User::class.java)
        Log.d("LatestMessage", "Current user : ${currentUser?.username}")
      }

      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }

  private fun verifyUserLogin() {
    var uid = FirebaseAuth.getInstance().uid

    if (uid == null) {
      // return the user to register page
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.menu_new_message -> {
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
      }
      R.id.menu_sign_out -> {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.nav_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }
}
