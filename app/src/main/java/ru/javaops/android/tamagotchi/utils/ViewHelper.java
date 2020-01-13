package ru.javaops.android.tamagotchi.utils;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewHelper {

    private ViewHelper() {
    }

    public static void executeAfterViewDrawing(final View v, final Runnable cb) {
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                cb.run();
                ViewTreeObserver obs = v.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
