package ru.javaops.android.tamagotchi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import ru.javaops.android.tamagotchi.utils.ViewHelper;

import static ru.javaops.android.tamagotchi.MainActivity.PET_NAME;
import static ru.javaops.android.tamagotchi.utils.SoundPlayer.getSoundPool;

public class WalkActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String DOG = "dog";
    private static final String CAT = "cat";
    private static final String CTHULHU = "cthulhu";

    private static final int ANIMATION_DURATION_MILLIS = 1000;
    private Animation.AnimationListener animationListener;
    private float maxHeight;
    private float maxWidth;
    private float startX = 0;
    private float startY = 0;
    private Animation translateAnimation = new TranslateAnimation(startX, startX, startY, startY);

    private ImageView petView;
    private SoundPool soundPool = getSoundPool();
    private final HashMap<String, Integer> sounds = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Intent intent = getIntent();
        String petName = intent.getStringExtra(PET_NAME);
        initPetView(petName);
        loadSounds();
        initAnimationListener();
        initAnimation();
    }

    public void goHome(View view) {
        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPetView(String name) {
        petView = (ImageView) findViewById(R.id.pet);
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
        final String petName = name;

        petView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "Pet " + petName + " was touched!");
                    Integer sound = sounds.get(petName);
                    if (sound != null) {
                        Log.d(TAG, "Play " + petName + " pet sound");
                        soundPool.play(sound, 1, 1, 1, 0, 1f);
                    }
                }
                return false;
            }
        });
    }

    private void loadSounds() {
        sounds.put(WalkActivity.CAT, soundPool.load(getApplicationContext(), R.raw.cat, 1));
        sounds.put(WalkActivity.DOG, soundPool.load(getApplicationContext(), R.raw.dog, 1));
        sounds.put(WalkActivity.CTHULHU, soundPool.load(getApplicationContext(), R.raw.cthulhu, 1));
    }

    private void initAnimationListener() {
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                float nextX = getRandom(maxWidth);
                float nextY = getRandom(maxHeight);
                translateAnimation = new TranslateAnimation(startX, nextX, startY, nextY);
                startX = nextX;
                startY = nextY;
                startAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            private float getRandom(float max) {
                return -max + (float) (Math.random() * max);
            }
        };
    }

    private void initAnimation() {
        final FrameLayout frameLayout = findViewById(R.id.layout);

        ViewHelper.executeAfterViewDrawing(petView, new Runnable() {
            @Override
            public void run() {
                maxHeight = (frameLayout.getHeight() - petView.getHeight()) / 2;
                maxWidth = (frameLayout.getWidth() - petView.getWidth()) / 2;
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        translateAnimation.setDuration(ANIMATION_DURATION_MILLIS);
        translateAnimation.setAnimationListener(animationListener);
        petView.startAnimation(translateAnimation);
    }
}
