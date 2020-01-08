package ru.javaops.android.tamagotchi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.javaops.android.tamagotchi.utils.ViewHelper;

public class OtherActivity extends AppCompatActivity {
    private static final int ANIMATION_OFFSET = 4000;
    private static final int ANIMATION_DURATION_MILLIS = 2000;
    private static final int ANIMATION_REPEAT_THRESHOLD = 10;

    private TextView textView;
    private Animation translateAnimation;
    private Animation.AnimationListener animationListener;

    private int countAnimationRepeat = 1;
    private float height;
    private float width;
    private float yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        initViews();
        initAnimationListener();
        initAnimation();
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

    private void initAnimationListener() {
        animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                countAnimationRepeat++;
                if (countAnimationRepeat < ANIMATION_REPEAT_THRESHOLD) {
                    if (countAnimationRepeat % 2 == 0) {
                        yDelta = height - (height / countAnimationRepeat);
                    }
                    translateAnimation = new TranslateAnimation(width, width, yDelta, height);
                    startAnimation();
                } else {
                    textView.setX(width + (int) textView.getX());
                    textView.setY(height);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    private void initAnimation() {
        final Animation helloAnimation = AnimationUtils.loadAnimation(this, R.anim.hello);
        final AnimationSet animationSet = new AnimationSet(true);
        final FrameLayout frameLayout = findViewById(R.id.layout);
        ViewHelper.executeAfterViewDrawing(textView, new Runnable() {
            @Override
            public void run() {
                height = frameLayout.getHeight() - textView.getHeight();
                width = (frameLayout.getWidth() / 2) - (textView.getWidth() / 2) - (int) textView.getX();

                translateAnimation = new TranslateAnimation(width, width, textView.getY(), height);
                translateAnimation.setDuration(ANIMATION_DURATION_MILLIS);
                translateAnimation.setStartOffset(ANIMATION_OFFSET);
                translateAnimation.setAnimationListener(animationListener);

                animationSet.addAnimation(translateAnimation);
                animationSet.addAnimation(helloAnimation);
                textView.startAnimation(animationSet);
            }
        });
    }

    private void startAnimation() {
        translateAnimation.setDuration(ANIMATION_DURATION_MILLIS / countAnimationRepeat);
        translateAnimation.setAnimationListener(animationListener);
        textView.startAnimation(translateAnimation);
    }
}
