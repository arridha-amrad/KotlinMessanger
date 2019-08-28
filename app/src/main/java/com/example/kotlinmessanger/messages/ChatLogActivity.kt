package com.example.kotlinmessanger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinmessanger.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import com.example.kotlinmessanger.models.User


class ChatLogActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chat_log)

//    val username = intent.getStringExtra(NewMessageActivity.USER_KEY)

    val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

    supportActionBar?.title = user.username

    val adapter = GroupAdapter<ViewHolder>()

    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())
    adapter.add(ChatToItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())

    recyclerview_chatlog.adapter = adapter
  }
}

class ChatFromItem : Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {

  }

  override fun getLayout(): Int {
    return R.layout.chat_from_row
  }
}

class ChatToItem : Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {

  }

  override fun getLayout(): Int {
    return R.layout.chat_to_row
  }
}