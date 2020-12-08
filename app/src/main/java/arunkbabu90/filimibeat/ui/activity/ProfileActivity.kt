package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arunkbabu90.filimibeat.databinding.ActivityProfileBinding
import arunkbabu90.filimibeat.ui.adapter.ProfileAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var adapter: ProfileAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var dpPath = ""
    private var fullName = ""
    private var email = ""

    companion object {
        var isUpdatesAvailable = false
        private const val REQUEST_CODE_PICK_IMAGE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}