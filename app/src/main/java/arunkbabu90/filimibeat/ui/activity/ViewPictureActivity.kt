package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arunkbabu90.filimibeat.databinding.ActivityViewPictureBinding

class ViewPictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}