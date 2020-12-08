package arunkbabu90.filimibeat.ui.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.databinding.ActivityProfileBinding
import arunkbabu90.filimibeat.ui.adapter.ProfileAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: ProfileAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var userId = ""
    private var dpPath = ""
    private var fullName = ""
    private var userName = ""
    private var email = ""

    companion object {
        var isUpdatesAvailable = false
        private const val REQUEST_CODE_PICK_IMAGE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore

        userId = auth.currentUser?.uid ?: ""
        email = auth.currentUser?.email ?: ""
    }

    /**
     * Pulls all the profile data from the database
     */
    private fun fetchData() {
        binding.pbProfileLoading.visibility = View.VISIBLE

        db.collection(Constants.COLLECTION_USERS).document(userId).get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    val d = snapshot.result
                    if (d != null) {
                        fullName = d.getString(Constants.FIELD_FULL_NAME) ?: ""
                        dpPath = d.getString(Constants.FIELD_DP_PATH) ?: ""
                        userName = d.getString(Constants.FIELD_USER_NAME) ?: ""
                    } else {
                        Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Populate the views with loaded data
     */
    private fun populateViews() {

    }

    /**
     * Upload the image files selected
     * @param bitmap The image to upload
     */
    fun uploadImageFile(bitmap: Bitmap) {
        binding. .showProgressBar()
        Toast.makeText(this, com.google.firebase.database.R.string.uploading_photo, Toast.LENGTH_SHORT).show()

        // Convert the image bitmap to InputStream
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.JPG_QUALITY, bos)
        val bs = ByteArrayInputStream(bos.toByteArray())

        val user = auth.currentUser
        if (user != null) {
            // Upload the image file
            val uploadPath = "${user.uid}/${Constants.DIRECTORY_PROFILE_PICTURE}/${Constants.PROFILE_PICTURE_FILE_NAME}${Constants.IMG_FORMAT_JPG}"
            val storageReference = cloudStore.getReference(uploadPath)
            storageReference.putStream(bs).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                if (!task.isSuccessful) {
                    // Upload failed
                    Toast.makeText(this, getString(com.google.firebase.database.R.string.err_get_download_image_url), Toast.LENGTH_LONG).show()
                    return@continueWithTask null
                }
                storageReference.downloadUrl
            }
                .addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful && task.result != null) {
                        // Upload success; push the download URL to the database, also update the mDoctorDpPath
                        val imagePath = task.result.toString()
                        doctorDpPath = imagePath
                        db.collection(Constants.COLLECTION_USERS).document(user.uid)
                            .update(Constants.FIELD_PROFILE_PICTURE, imagePath)
                            .addOnSuccessListener { Toast.makeText(this, com.google.firebase.database.R.string.saved, Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this, com.google.firebase.database.R.string.err_upload_failed, Toast.LENGTH_SHORT).show() }

                        db.collection(Constants.COLLECTION_DOCTORS_LIST).document(user.uid)
                            .update(Constants.FIELD_PROFILE_PICTURE, imagePath)
                            .addOnSuccessListener { Log.d(TAG, "Dp path push to Doctor List Success") }
                            .addOnFailureListener { Log.d(TAG, "Dp path push to Doctor List Failure") }
                    } else {
                        Toast.makeText(this, getString(com.google.firebase.database.R.string.err_get_download_image_url), Toast.LENGTH_LONG).show()
                    }
                    iv_doc_profile_photo.hideProgressBar()
                }
        }
        DoctorProfileFragment.mIsUpdatesAvailable = false
    }
}