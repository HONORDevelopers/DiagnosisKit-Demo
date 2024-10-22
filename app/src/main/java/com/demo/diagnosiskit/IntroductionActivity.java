package com.demo.DiagnosisKit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;

public class IntroductionActivity extends FragmentActivity {
    private Button start_button;
    private ImageView introduction_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        introduction_image = (ImageView) findViewById(R.id.introduction_image_icon);
        introduction_image.setImageResource(R.drawable.diagkit);
        start_button = findViewById(R.id.introduction_start_experience);
        start_button.setOnClickListener(view -> {
            Intent startIntent = new Intent(IntroductionActivity.this, MainActivity.class);
            startActivity(startIntent);
        });
    }
}