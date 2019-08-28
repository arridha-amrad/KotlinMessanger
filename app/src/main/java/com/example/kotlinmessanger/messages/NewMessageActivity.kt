package com.example.kotlinmessanger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessanger.R
import com.example.kotlinmessanger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_message)

    supportActionBar?.title = "Select User"

//    val adapter = GroupAdapter<ViewHolder>()
//
//    adapter.add(UserItem())
//    adapter.add(UserItem())
//    adapter.add(UserItem())
//
//
//    recyclerView_newmessage.adapter = adapter

    fetchUsers()
  }

  companion object{
    val USER_KEY = "USER_KEY"
  }

  private fun fetchUsers() {
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {

      override fun onDataChange(p0: DataSnapshot) {
        val adapter = GroupAdapter<ViewHolder>()

        // load every single user from firebasedatabase
        p0.children.forEach {
          Log.d("NewMessage", "Fetching result : $it")
          val user = it.getValue(User::class.java)
          if (user != null) {
            adapter.add(UserItem(user))
          }
        }

        // it will make the list of users are clickable
        adapter.setOnItemClickListener{ item, view ->
          val userItem = item as UserItem
          val intent = Intent(view.context, ChatLogActivity::class.java)
          intent.putExtra(USER_KEY, userItem.user)
          startActivity(intent)

          // user can go back to new message
          finish()
        }

        recyclerView_newmessage.adapter = adapter
      }

      // this line is necessary to clear error on object: ValueEventListener
      override fun onCancelled(p0: DatabaseError) {

      }
    })


  }
}

class UserItem(val user: User): Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {
    // will be called in our list for each user object later on...

    // load the username
    viewHolder.itemView.username_textview_newmessage.text = user.username

    // load the image of user
    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_imageview_newmessage)
  }

  // this class use user_row_new_message for single user
  override fun getLayout(): Int {
    return R.layout.user_row_new_message
  }
}

