package kr.ac.konkuk.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*
import kr.ac.konkuk.instagramclone.Model.User

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userInfo()

        post_comment.setOnClickListener(View.OnClickListener {
            if(add_comment!!.text.toString() == "")
            {
                Toast.makeText(this@CommentsActivity, "Please write comment first.", Toast.LENGTH_LONG).show()
            }
            else
            {
                addComment()
            }
        })
    }

    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().getReference()
            .child("Comments")
            .child(postId!!)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] =  add_comment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        add_comment!!.text.clear()

    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comment)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}