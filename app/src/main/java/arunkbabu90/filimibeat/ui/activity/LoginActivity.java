package arunkbabu90.filimibeat.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import arunkbabu90.filimibeat.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.iv_tmdb_logo) ImageView mLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ImageView iv = findViewById(R.id.iv_tmdb_logo);

        // Load the TMDB Logo into the bottom image view
        Glide.with(this).load(R.drawable.tmdb_logo_short).into(iv);
    }

    public void onForgotPasswordClick(View view) {
    }

    public void onSignUpTextViewClick(View view) {
    }

    public void onLoginClick(View view) {
    }
}