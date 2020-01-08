package ru.javaops.android.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OtherActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        initViews();
        final Animation helloAnimation = AnimationUtils.loadAnimation(this, R.anim.hello);
        textView.startAnimation(helloAnimation);
    }

    private void initViews() {
        textView = findViewById(R.id.title_hello);
        findViewById(R.id.button_home)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OtherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
