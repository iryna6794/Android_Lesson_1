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

    private static final double DOG_VIEW_MAGNIFICATION = 1.5;
    private static final int RANDOM_TIME_DURATION_TRANSLATE = 1000;
    private static final int MIN_TIME_DURATION_TRANSLATE = 300;
    private static final int COEFFICIENT_TIME_DURATION_TRANSLATE = 3;
    private static final int RANDOM_TIME_DURATION_ROTATE = 500;
    private static final int COEFFICIENT_TIME_DURATION_ROTATE = 3;
    private static final int DEFAULT_ROTATION = 90;

    private Animator.AnimatorListener animatorListener;
    private ImageView petView;
    private int maxWidth;
    private int maxHeight;
    private int thisX;
    private int thisY;
    private int nextX;
    private int nextY;
    private float nextAngle;
    private float angle;

    private static final float MOVE_DISTANCE_INDEX = 0.1f;
    private float minMoveDistance;


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
            params.width = (int) (petView.getLayoutParams().width * DOG_VIEW_MAGNIFICATION);
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
                minMoveDistance = (layout.getHeight() + layout.getWidth()) * MOVE_DISTANCE_INDEX;
                maxWidth = layout.getWidth() - petView.getWidth();
                maxHeight = layout.getHeight() - petView.getHeight();
                thisX = maxWidth / 2;
                thisY = maxHeight / 2;
                petView.setX(thisX);
                petView.setY(thisY);
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        nextX = getRandom(maxWidth, thisX);
        nextY = getRandom(maxHeight, thisY);
        int distance = getDistanceBetweenPoints(thisX, nextX, thisY, nextY);

        nextAngle = (float) Math.toDegrees(Math.atan2(thisY - nextY, thisX - nextX));
        int rotationDuration = (int) (Math.random() * RANDOM_TIME_DURATION_ROTATE +
                Math.abs(nextAngle - angle) * COEFFICIENT_TIME_DURATION_ROTATE);
        int translateDuration = (int) (Math.random() * RANDOM_TIME_DURATION_TRANSLATE +
                MIN_TIME_DURATION_TRANSLATE +
                ViewHelper.pxToDp(distance) * COEFFICIENT_TIME_DURATION_TRANSLATE);

        final ObjectAnimator rotate = ObjectAnimator
                .ofFloat(petView, View.ROTATION, nextAngle - DEFAULT_ROTATION);
        rotate.setDuration(rotationDuration);

        final PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, thisX);
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, thisY);
        final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(petView, pvhX, pvhY);
        animator.setDuration(translateDuration);
        animator.setStartDelay(rotationDuration - rotationDuration / COEFFICIENT_TIME_DURATION_ROTATE);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotate, animator);
//        animatorSet.playTogether(animator);
        animatorSet.addListener(animatorListener);
        animatorSet.start();
    }

    private int getRandom(int max, int currentPoint) {
        int newPoint;
        do {
            newPoint = (int) (Math.random() * (max + 1));
        } while (Math.abs(newPoint - currentPoint) < minMoveDistance);
        return newPoint;
    }

    private int getDistanceBetweenPoints(int thisX, int nextX, int thisY, int nextY) {
        int yDif = Math.abs(nextY - thisX);
        int xDif = Math.abs(nextX - thisY);
        return (int) Math.hypot(yDif, xDif);
    }
}
