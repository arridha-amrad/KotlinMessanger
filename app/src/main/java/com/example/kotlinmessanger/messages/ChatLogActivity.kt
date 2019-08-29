package com.example.kotlinmessanger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessanger.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import com.example.kotlinmessanger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.example.kotlinmessanger.models.ChatMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.imageView_chat_to_row


class ChatLogActivity : AppCompatActivity() {

  companion object {
    val TAG = "Chatlog"
  }

  val adapter = GroupAdapter<ViewHolder>()

  var toUser: User? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chat_log)

    recyclerview_chatlog.adapter = adapter

    toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

    supportActionBar?.title = toUser?.username

//    setUpDummyData()
    listenForMessages()


    button_chatlog.setOnClickListener {
      Log.d(TAG, "Attempt to send message")
      performSendMessage()
    }
  }


  private fun listenForMessages() {
    val fromId = FirebaseAuth.getInstance().uid
    val toId = toUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
    ref.addChildEventListener(object : ChildEventListener {
      override fun onChildAdded(p0: DataSnapshot, p1: String?) {
        val chatMessage = p0.getValue(ChatMessage::class.java)

        if (chatMessage != null) {
          Log.d(TAG, chatMessage.text)
          if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            val currentUser = LatestMessagesActivity.currentUser ?: return
            // elvis operator avoiding error mismatch
            adapter.add(ChatFromItem(chatMessage.text, currentUser))
          } else {

            adapter.add(ChatToItem(chatMessage.text, toUser!!))
          }
        }
      }

      override fun onChildRemoved(p0: DataSnapshot) {

      }

      override fun onChildChanged(p0: DataSnapshot, p1: String?) {

      }

      override fun onChildMoved(p0: DataSnapshot, p1: String?) {

      }

      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }


  private fun performSendMessage() {

    // how we send message to firebase
    val text = editext_chatlog.text.toString()
    val fromId = FirebaseAuth.getInstance().uid
    val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
    val toId = user.uid
    val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
    val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()


    if (fromId == null) return

    val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

    ref.setValue(chatMessage)
      .addOnSuccessListener {
        Log.d(TAG, "Saved our chat Message : ${ref.key}")
        editext_chatlog.text.clear()
        recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
      }

    toRef.setValue(chatMessage)

    // for latest message activity
    val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
    latestMessageRef.setValue(chatMessage)
    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
    latestMessageToRef.setValue(chatMessage)

  }


  class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
      viewHolder.itemView.textView_chat_from_row.text = text
// load user image to change the star
      val uri = user.profileImageUrl
      val targetImageView = viewHolder.itemView.imageView_chat_from_row
      Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
      return R.layout.chat_from_row
    }
  }

  class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
      viewHolder.itemView.textView_chat_to_row.text = text

      // load user image to change the star
      val uri = user.profileImageUrl
      val targetImageView = viewHolder.itemView.imageView_chat_to_row
      Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
      return R.layout.chat_to_row
    }
  }
}
