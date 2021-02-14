package kr.ac.konkuk.instagramclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        save_new_post_btn.setOnClickListener { uploadImage() }

        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this@AddPostActivity)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }

    }

    private fun uploadImage() {
        when {
            // 작업을 수행할 앱이 없습니다 error
            imageUri == null -> Toast.makeText(
                this,
                "Please select image first.",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(
                this,
                "Please write your description.",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding new Post")
                progressDialog.setMessage("Please wait, we are adding your picture post...")
                progressDialog.show()

                val fileRef =
                    storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key

                            val userMap = HashMap<String, Any>()
                            userMap["postid"] = postId!!
                            userMap["description"] = description_post.text.toString().toLowerCase()
                            userMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            userMap["postimage"] = myUrl

                            ref.child(postId).updateChildren(userMap)

                            Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_LONG)
                                .show()

                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                        }

                    }
                    )

            }

        }
    }
}