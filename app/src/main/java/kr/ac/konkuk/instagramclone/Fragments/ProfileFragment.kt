package kr.ac.konkuk.instagramclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kr.ac.konkuk.instagramclone.AccountsSettingActivity
import kr.ac.konkuk.instagramclone.R
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kr.ac.konkuk.instagramclone.Model.Post
import kr.ac.konkuk.instagramclone.Model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId : String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //!! -> not null assert
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid)
        {
            view.edit_account_settings_btn.text = "Edit Profile"
        }

        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }

        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)


        view.edit_account_settings_btn.setOnClickListener {
            val getButtonText = view.edit_account_settings_btn.text.toString()

            when
            {
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountsSettingActivity::class.java))

                getButtonText == "Follow" -> {

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(itl.toString())
                                .child("Following").child(profileId)
                                .setValue(true)
                    }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers").child(itl.toString())
                                .setValue(true)
                    }

                }

                getButtonText == "Following" -> {

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(itl.toString())
                                .child("Following").child(profileId)
                                .removeValue()
                        }

                    firebaseUser?.uid.let { itl ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers").child(itl.toString())
                                .removeValue()
                        }

                    }
                }
        }

        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowAndFollowingButtonStatus()
    {
        val followingRef = firebaseUser?.uid.let { itl ->
            FirebaseDatabase.getInstance().reference
                    .child("Follow").child(itl.toString())
                    .child("Following")
        }

        if ( followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(p0: DataSnapshot)
                {
                    if (p0.child(profileId).exists())
                    {
                        view?.edit_account_settings_btn?.text = "Following"
                    }
                    else
                    {
                        view?.edit_account_settings_btn?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError)
                {

                }

            })
        }

    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })
    }

    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followings")

        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user!!.getUsername()
                    view?.full_name_profile_frag?.text = user!!.getFullname()
                    view?.bio_profile_frag?.text = user!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}