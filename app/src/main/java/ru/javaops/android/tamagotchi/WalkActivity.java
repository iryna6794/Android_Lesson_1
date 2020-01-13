package ru.javaops.android.tamagotchi;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ru.javaops.android.tamagotchi.enums.PetsType;
import ru.javaops.android.tamagotchi.utils.SoundHelper;
import ru.javaops.android.tamagotchi.utils.ViewHelper;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

@SuppressLint("ClickableViewAccessibility")
public class WalkActivity extends AppCompatActivity {
    public static final String INTENT_PET_TYPE = "pet_type";

    private static final double DOG_VIEW_MAGNIFICATION = 1.7;
    private static final double MAX_DISTANCE_LIMIT_DIVIDER = 1.5;
    private static final double MIN_DISTANCE_LIMIT_DIVIDER = 8;
    private static final int RANDOM_TIME_DURATION_TRANSLATE = 1000;
    private static final int MIN_TIME_DURATION_TRANSLATE = 300;
    private static final int COEFFICIENT_TIME_DURATION_TRANSLATE = 3;
    private static final int RANDOM_TIME_DURATION_ROTATE = 500;
    private static final int COEFFICIENT_TIME_DURATION_ROTATE = 3;
    private static final int DEFAULT_ROTATION = 90;

    private Animator.AnimatorListener animatorListener;
    private ImageView petView;
    private int borderHeight;
    private int borderWidth;
    private int height;
    private int width;
    private int thisX;
    private int thisY;
    private int nextX;
    private int nextY;
    private float nextAngle;
    private float angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        petView = findViewById(R.id.image_pet);
        SoundHelper.initialSoundPool(getApplicationContext());
        initViews();
        initAnimatorListener();
        initAnimation();
    }

    public void goHome(View view) {
        Intent intent = new Intent(WalkActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        petView = findViewById(R.id.image_pet);
        final PetsType petsType = PetsType.valueOf(getIntent().getStringExtra(INTENT_PET_TYPE));
        petView.setImageResource(petsType.getDrawableResource());
        if (PetsType.DOG == petsType) {
            ViewGroup.LayoutParams params = petView.getLayoutParams();
            params.height = (int) (petView.getLayoutParams().height * DOG_VIEW_MAGNIFICATION);
            petView.setLayoutParams(params);
        }
        petView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("WALK", "Touch on pet");
                    SoundHelper.play(petsType);
                    return true;
                }
                return false;
            }
        });
    }

    private void initAnimatorListener() {
        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                thisX = nextX;
                thisY = nextY;
                angle = nextAngle;
                startAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    private void initAnimation() {
        ViewHelper.executeAfterViewDrawing(petView, new Runnable() {
            @Override
            public void run() {
                final FrameLayout layout = findViewById(R.id.layoutWalk);
                int radius = (int) (Math.hypot(petView.getHeight(), petView.getWidth()) / 2);
                borderWidth = radius - petView.getWidth() / 2;
                borderHeight = radius - petView.getHeight() / 2;
                width = layout.getWidth() - radius * 2;
                height = layout.getHeight() - radius * 2;
                thisX = width >> 1;
                thisY = height >> 1;
                petView.setX(thisX);
                petView.setY(thisY);
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        int distance;
        do {
            nextX = (int) (Math.random() * width) + borderWidth;
            nextY = (int) (Math.random() * height) + borderHeight;
            distance = (int) Math.hypot(thisX - nextX, thisY - nextY);
        }
        while (isPetPositionCorrect(distance));

        nextAngle = (float) Math.toDegrees(Math.atan2(thisY - nextY, thisX - nextX));
        int rotationDuration = (int) (Math.random() * RANDOM_TIME_DURATION_ROTATE +
                Math.abs(nextAngle - angle) * COEFFICIENT_TIME_DURATION_ROTATE);
        int translateDuration = (int) (Math.random() * RANDOM_TIME_DURATION_TRANSLATE +
                MIN_TIME_DURATION_TRANSLATE +
                ViewHelper.pxToDp(distance) * COEFFICIENT_TIME_DURATION_TRANSLATE);

        final ObjectAnimator rotate = ObjectAnimator
                .ofFloat(petView, View.ROTATION, nextAngle - DEFAULT_ROTATION);
        rotate.setDuration(rotationDuration);

        final PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, nextX);
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, nextY);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(petView, pvhX, pvhY);
        animator.setDuration(translateDuration);
        animator.setStartDelay(rotationDuration - rotationDuration / COEFFICIENT_TIME_DURATION_ROTATE);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotate, animator);
        animatorSet.addListener(animatorListener);
        animatorSet.start();
    }

    private boolean isPetPositionCorrect(int distance) {
        return distance < Math.max(width, height) / MIN_DISTANCE_LIMIT_DIVIDER ||
                distance > Math.max(width, height) / MAX_DISTANCE_LIMIT_DIVIDER;
    }
}
