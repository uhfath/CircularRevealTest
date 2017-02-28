package com.company.user.circularrevealtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static float distance(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private static Animator getRevealAnimator(Activity activity, int x, int y, long duration) {
        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        float d1 = distance(x, y, 0, 0);
        float d2 = distance(x, y, 0, size.y);
        float d3 = distance(x, y, size.x, 0);
        float d4 = distance(x, y, size.x, size.y);
        float radius = Math.max(Math.max(Math.max(d1, d2), d3), d4);

        View view = activity.getWindow().getDecorView();
        Animator animator = (Animator) view.getTag();
        if (animator != null) {
            animator.cancel();
        }

        animator = ViewAnimationUtils.createCircularReveal(view, x, y, 0.0f, radius);
        animator.setDuration(duration);

        view.setTag(animator);
        return animator;
    }

    private static void startRevealAnimation(final Activity activity, final int x, final int y) {
        final View view = activity.getWindow().getDecorView();
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(-1, -1, 0, 0);
            }
        });
        view.setClipToOutline(true);

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        view.setBackgroundColor(color);

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver vto = view.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.removeOnPreDrawListener(this);
                }
                Animator animator = getRevealAnimator(activity, x, y, 1000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setClipToOutline(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        view.setClipToOutline(false);
                    }
                });
                animator.start();

                return false;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.activity_main);
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startRevealAnimation(MainActivity.this, (int) event.getRawX(), (int) event.getRawY());
                return false;
            }
        });
    }
}
