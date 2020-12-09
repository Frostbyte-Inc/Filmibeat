package arunkbabu90.filimibeat.ui.activity

import android.graphics.Bitmap
import android.net.*
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.databinding.ActivityProfileBinding
import arunkbabu90.filimibeat.ui.adapter.ProfileAdapter
import com.bumptech.glide.Glide
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

    private var dpPath = ""
    private var fullName = ""
    private var userName = ""
    private var email = ""

    private var isInternetConnected = false
    private var isDataLoaded = false

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

        email = auth.currentUser?.email ?: ""

        registerNetworkChangeCallback()
    }

    /**
     * Pulls all the profile data from the database
     */
    private fun fetchData() {
        binding.pbProfileLoading.visibility = View.VISIBLE

        val user = auth.currentUser
        if (user != null)
        db.collection(Constants.COLLECTION_USERS).document(user.uid).get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    val d = snapshot.result
                    if (d != null) {
                        fullName = d.getString(Constants.FIELD_FULL_NAME) ?: ""
                        dpPath = d.getString(Constants.FIELD_DP_PATH) ?: ""
                        userName = d.getString(Constants.FIELD_USER_NAME) ?: ""

                        isDataLoaded = true

                        populateViews()
                    } else {
                        Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                        isDataLoaded = false
                    }
                } else {
                    Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                    isDataLoaded = false
                }
            }
    }

    /**
     * Populate the views with loaded data
     */
    private fun populateViews() {
        if (userName.isBlank())
            userName = "Not Set"

        val profileData = arrayListOf(
            "Name" to fullName,
            "Username" to userName,
            "Email" to email
        )

        val adapter = ProfileAdapter(profileData)
        binding.rvProfile.adapter = adapter
        binding.rvProfile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        Glide.with(this).load(dpPath).placeholder(R.drawable.default_dp).into(binding.ivProfileDp)

        binding.pbProfileLoading.visibility = View.GONE
    }

    /**
     * Upload the image files selected
     * @param bitmap The image to upload
     */
    fun uploadImageFile(bitmap: Bitmap) {
        binding.pbProfileDpLoading.visibility = View.VISIBLE
        Toast.makeText(this, getString(R.string.uploading_dp), Toast.LENGTH_SHORT).show()

        // Convert the image bitmap to InputStream
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bs = ByteArrayInputStream(bos.toByteArray())

        val user = auth.currentUser
        if (user != null) {
            // Upload the image file
            val uploadPath = "${user.uid}/${Constants.DIRECTORY_PROFILE_PICTURE}/${Constants.PROFILE_PICTURE_FILE_NAME}${Constants.IMG_FORMAT_JPG}"
            val storageReference = storage.getReference(uploadPath)
            storageReference.putStream(bs).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                if (!task.isSuccessful) {
                    // Upload failed
                    Toast.makeText(this, getString(R.string.err_upload_failed), Toast.LENGTH_LONG).show()
                    return@continueWithTask null
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful && task.result != null) {
                        // Upload success; push the download URL to the database, also update the mDoctorDpPath
                        val imagePath = task.result.toString()
                        dpPath = imagePath
                        db.collection(Constants.COLLECTION_USERS).document(user.uid)
                            .update(Constants.FIELD_DP_PATH, imagePath)
                            .addOnSuccessListener { Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this, R.string.err_upload_failed, Toast.LENGTH_SHORT).show() }
                    } else {
                        Toast.makeText(this, getString(R.string.err_upload_failed), Toast.LENGTH_LONG).show()
                    }
                    binding.pbProfileDpLoading.visibility = View.GONE
                }
        }
        isUpdatesAvailable = true
    }


    /**
     * Register a callback to be invoked when network connectivity changes
     * @return True If internet is available; False otherwise
     */
    private fun registerNetworkChangeCallback(): Boolean {
        val isAvailable = BooleanArray(1)
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Internet is Available
                runOnUiThread {
                    isInternetConnected = true
                    isAvailable[0] = true
                    if (!isDataLoaded)
                        fetchData()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Internet is Unavailable
                isAvailable[0] = false
                runOnUiThread {
                    isInternetConnected = false
                }
            }
        })
        return isAvailable[0]
    }
}