package ru.javaops.android.tamagotchi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static ru.javaops.android.tamagotchi.MainActivity.PET_NAME;

public class WalkActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String DOG = "dog";
    private final String CAT = "cat";
    private final String CTHULHU = "cthulhu";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Intent intent = getIntent();
        String petName = intent.getStringExtra(PET_NAME);
        initPetView(petName);
    }

    public void goHome(View view) {
        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPetView(String name) {
        ImageView petView = (ImageView) findViewById(R.id.pet);
        switch (name) {
            case DOG:
                petView.setImageResource(R.drawable.dog);
                break;
            case CAT:
                petView.setImageResource(R.drawable.cat);
                break;
            case CTHULHU:
                petView.setImageResource(R.drawable.cthulhu);
                break;
            default:
                petView.setImageResource(R.drawable.cthulhu);
                name = CTHULHU;
        }
        petView.setContentDescription(name);
        final String finalName = name;
        petView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "Pet " + finalName + " was touched!");
                    SoundPool soundPool = getSoundPool();
                    int sound = soundPool.load(getApplicationContext(), R.raw.cat, 1);
                    Log.d(TAG, "Sound id is : " + sound);
                    if(sound > 0) {
                        soundPool.play(sound, 1, 1, 1, 0, 1);
                    }
                }
                return false;
            }
        });
    }

    private SoundPool getSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        return new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }
}
