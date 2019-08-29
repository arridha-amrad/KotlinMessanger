package com.example.kotlinmessanger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.kotlinmessanger.R
import com.example.kotlinmessanger.models.ChatMessage
import com.example.kotlinmessanger.registerLogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.kotlinmessanger.models.User
import com.example.kotlinmessanger.registerLogin.LoginActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import com.example.kotlinmessanger.View.LatestMessageRow
import kotlinx.android.synthetic.main.latest_message_row.view.*


class LatestMessagesActivity : AppCompatActivity() {

  companion object{
    var currentUser: User? = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_latest_messages)

    recyclerview_latest_message.adapter = adapter
    recyclerview_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

    // set item click listener on your adapter
    adapter.setOnItemClickListener{
      item, view ->
      Log.d("LatestMessage", "123")
      val intent = Intent(this, ChatLogActivity::class.java)

      // to fix chat partner user
      val row = item as LatestMessageRow
      intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
      startActivity(intent)
    }

//    setupDummyRows()
    listenForLatestMessage()

    fetchCurrentUser()

//  check to see wheater the user login or not
    verifyUserLogin()
  }


  val latestMessageMap = HashMap<String, ChatMessage>()

  private fun refreshRecyclerViewMessages() {
    adapter.clear()
    latestMessageMap.values.forEach{
      adapter.add(LatestMessageRow(it))
    }
  }

  private fun listenForLatestMessage() {
    val fromId = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
    ref.addChildEventListener(object: ChildEventListener {
      override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val chatMessage = p0.getValue(ChatMessage::class.java)?:return
        latestMessageMap[p0.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }
      override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        val chatMessage = p0.getValue(ChatMessage::class.java)?:return
        latestMessageMap[p0.key!!] = chatMessage
        refreshRecyclerViewMessages()
      }

      override fun onChildRemoved(p0: DataSnapshot) {

      }
      override fun onChildMoved(p0: DataSnapshot, p1: String?) {

      }
      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }
  val adapter = GroupAdapter<ViewHolder>()


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
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
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
