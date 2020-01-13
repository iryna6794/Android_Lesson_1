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
import java.util.Random;

import ru.javaops.android.tamagotchi.utils.ViewHelper;

import static ru.javaops.android.tamagotchi.MainActivity.PET_NAME;
import static ru.javaops.android.tamagotchi.utils.SoundPlayer.getSoundPool;

public class WalkActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String DOG = "dog";
    private static final String CAT = "cat";
    private static final String CTHULHU = "cthulhu";

    private static final int ANIMATION_MAX_DURATION_MILLIS = 3000;
    private static final float MOVE_DISTANCE_INDEX = 0.1f;
    private Animation.AnimationListener animationListener;
    private float maxHeight;
    private float maxWidth;
    private float minMoveDistance;
    private int animationDuration;
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
                float nextX = getRandom(maxWidth, startX);
                float nextY = getRandom(maxHeight, startY);
                translateAnimation = new TranslateAnimation(startX, nextX, startY, nextY);
                double distance = getDistanceBetweenPoints(startX, nextX, startY, nextY);
                System.out.println(distance + " distance");
                animationDuration = (int) (ANIMATION_MAX_DURATION_MILLIS / (maxHeight + maxWidth) * distance);
                startX = nextX;
                startY = nextY;
                startAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            private float getRandom(float max, float currentPoint) {
                int currentMax = (int) (max * 2 + 1);
                Random random = new Random();
                float newPoint = -max + random.nextInt(currentMax);
                while (Math.abs(newPoint - currentPoint) < minMoveDistance) {
                    newPoint = -max + random.nextInt(currentMax);
                }
                return newPoint;
            }

            private double getDistanceBetweenPoints(float startX, float nextX, float startY, float nextY) {
                float ac = Math.abs(nextY - startY);
                float cb = Math.abs(nextX - startX);
                return Math.hypot(ac, cb);
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
                minMoveDistance = (maxHeight + maxWidth) * MOVE_DISTANCE_INDEX;
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        translateAnimation.setDuration(animationDuration);
        translateAnimation.setAnimationListener(animationListener);
        petView.startAnimation(translateAnimation);
    }
}

//
//@SuppressLint("ClickableViewAccessibility")
//public class WalkActivity extends AppCompatActivity {
//    public static final String INTENT_PET_TYPE = "pet_type";
//
//    private static final double DOG_VIEW_MAGNIFICATION = 1.7;
//    private static final double MAX_DISTANCE_LIMIT_DIVIDER = 1.5;
//    private static final double MIN_DISTANCE_LIMIT_DIVIDER = 8;
//    private static final int RANDOM_TIME_DURATION_TRANSLATE = 1000;
//    private static final int MIN_TIME_DURATION_TRANSLATE = 300;
//    private static final int COEFFICIENT_TIME_DURATION_TRANSLATE = 3;
//    private static final int RANDOM_TIME_DURATION_ROTATE = 500;
//    private static final int COEFFICIENT_TIME_DURATION_ROTATE = 3;
//    private static final int DEFAULT_ROTATION = 90;
//
//    private Animator.AnimatorListener animatorListener;
//    private ImageView petView;
//    private int borderHeight;
//    private int borderWidth;
//    private int height;
//    private int width;
//    private int thisX;
//    private int thisY;
//    private int nextX;
//    private int nextY;
//    private float nextAngle;
//    private float angle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walk);
//        petView = findViewById(R.id.image_pet);
//        SoundHelper.initialSoundPool(getApplicationContext());
//        initViews();
//        initAnimatorListener();
//        initAnimation();
//    }
//
//    public void goHome(View view) {
//        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
//        startActivity(intent);
//    }
//
//    private void initViews() {
//        petView = findViewById(R.id.image_pet);
//        final PetsType petsType = PetsType.valueOf(getIntent().getStringExtra(INTENT_PET_TYPE));
//        petView.setImageResource(petsType.getDrawableResource());
//        if (PetsType.DOG == petsType) {
//            ViewGroup.LayoutParams params = petView.getLayoutParams();
//            params.height = (int) (petView.getLayoutParams().height * DOG_VIEW_MAGNIFICATION);
//            petView.setLayoutParams(params);
//        }
//        petView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    Log.d("WALK", "Touch on pet");
//                    SoundHelper.play(petsType);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void initAnimatorListener() {
//        animatorListener = new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                thisX = nextX;
//                thisY = nextY;
//                angle = nextAngle;
//                startAnimation();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        };
//    }
//
//    private void initAnimation() {
//        ViewHelper.executeAfterViewDrawing(petView, new Runnable() {
//            @Override
//            public void run() {
//                final FrameLayout layout = findViewById(R.id.layoutWalk);
//                int radius = (int) (Math.hypot(petView.getHeight(), petView.getWidth()) / 2);
//                borderWidth = radius - petView.getWidth() / 2;
//                borderHeight = radius - petView.getHeight() / 2;
//                width = layout.getWidth() - radius * 2;
//                height = layout.getHeight() - radius * 2;
//                thisX = width >> 1;
//                thisY = height >> 1;
//                petView.setX(thisX);
//                petView.setY(thisY);
//                startAnimation();
//            }
//        });
//    }
//
//    private void startAnimation() {
//        int distance;
//        do {
//            nextX = (int) (Math.random() * width) + borderWidth;
//            nextY = (int) (Math.random() * height) + borderHeight;
//            distance = (int) Math.hypot(thisX - nextX, thisY - nextY);
//        }
//        while (isPetPositionCorrect(distance));
//
//        nextAngle = (float) Math.toDegrees(Math.atan2(thisY - nextY, thisX - nextX));
//        int rotationDuration = (int) (Math.random() * RANDOM_TIME_DURATION_ROTATE +
//                Math.abs(nextAngle - angle) * COEFFICIENT_TIME_DURATION_ROTATE);
//        int translateDuration = (int) (Math.random() * RANDOM_TIME_DURATION_TRANSLATE +
//                MIN_TIME_DURATION_TRANSLATE +
//                ViewHelper.pxToDp(distance) * COEFFICIENT_TIME_DURATION_TRANSLATE);
//
//        final ObjectAnimator rotate = ObjectAnimator
//                .ofFloat(petView, View.ROTATION, nextAngle - DEFAULT_ROTATION);
//        rotate.setDuration(rotationDuration);
//
//        final PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, nextX);
//        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, nextY);
//        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(petView, pvhX, pvhY);
//        animator.setDuration(translateDuration);
//        animator.setStartDelay(rotationDuration - rotationDuration / COEFFICIENT_TIME_DURATION_ROTATE);
//
//        final AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(rotate, animator);
//        animatorSet.addListener(animatorListener);
//        animatorSet.start();
//    }
//
//    private boolean isPetPositionCorrect(int distance) {
//        return distance < Math.max(width, height) / MIN_DISTANCE_LIMIT_DIVIDER ||
//                distance > Math.max(width, height) / MAX_DISTANCE_LIMIT_DIVIDER;
//    }