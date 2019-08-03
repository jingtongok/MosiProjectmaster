package com.jttj.midtv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/7/14
 */
public class SplashActivity extends AppCompatActivity {

    TextView mAppName;
    TextView mAppDes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mAppName = findViewById(R.id.txt_name);
        mAppDes = findViewById(R.id.txt_des);

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mAppDes, "alpha", 0, 1f);
        ObjectAnimator desAnim = ObjectAnimator.ofFloat(mAppDes, "translationX", -500f, 0f);
        desAnim.setDuration(700);
        desAnim.setInterpolator(new DecelerateInterpolator());
        desAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAppName.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAppName.setVisibility(View.VISIBLE);
            }
        });
        ObjectAnimator nameAnim = ObjectAnimator.ofFloat(mAppName, "translationY", -500f, 0f);
        nameAnim.setDuration(2000);
        nameAnim.setInterpolator(new BounceInterpolator());
        nameAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                try {
                    Thread.sleep(800);
                    go2Main();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(desAnim).with(alphaAnim).before(nameAnim);
        animSet.start();
    }

    private void go2Main() {

        startActivity(new Intent(SplashActivity.this, MainActivity.class)); //
        finish();

    }
}
