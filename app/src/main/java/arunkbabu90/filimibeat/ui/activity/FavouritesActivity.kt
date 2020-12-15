package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import arunkbabu90.filimibeat.R

class FavouritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPurpleDark)
    }
}