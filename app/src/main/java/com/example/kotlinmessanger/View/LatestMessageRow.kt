package com.example.kotlinmessanger.View

import com.example.kotlinmessanger.R
import com.example.kotlinmessanger.models.ChatMessage
import com.example.kotlinmessanger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {

    var chatPartnerUser:User? = null

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.latestmessage_text.text = chatMessage.text

    val chatPartnerId: String
    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
      chatPartnerId = chatMessage.toId
    } else {
      chatPartnerId = chatMessage.fromId
    }

    val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        chatPartnerUser = p0.getValue(User::class.java)
        viewHolder.itemView.textView_username_latest_message.text = chatPartnerUser?.username
        Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.imageView_latestmessage)
      }

      override fun onCancelled(p0: DatabaseError) {

      }
    })

  }

  override fun getLayout(): Int {
    return R.layout.latest_message_row
  }
}