package com.example.images.util.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.example.images.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Drawable which loads its contents asynchronously in background.
 */
public class RemoteDrawable extends Drawable {

    private static final int CACHE_SIZE = 8 * 1024 * 1024;
    private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<String, Bitmap>(CACHE_SIZE) {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @SuppressWarnings("ConstantConditions")
        @Override
        protected int sizeOf(String key, Bitmap value) {
            if (BuildConfig.VERSION_CODE >= Build.VERSION_CODES.KITKAT) {
                return value.getAllocationByteCount();
            } else {
                return value.getByteCount();
            }
        }

    };

    private static final Executor LOADING_EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN_THREAD_HANDLER = new Handler();

    @Nullable
    private Bitmap bitmap;

    public RemoteDrawable(String url) {
        loadBitmap(url);
    }

    private void loadBitmap(String url) {
        bitmap = BITMAP_CACHE.get(url);

        if (bitmap == null) {
            loadBitmapAsyncronously(url);
        }
    }

    private void loadBitmapAsyncronously(String url) {
        LOADING_EXECUTOR.execute(() -> blockingLoadBitmap(url));
    }

    private void blockingLoadBitmap(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            try {
                Bitmap loadedBitmap = readBitmap(connection);

                applyLoadedBitmap(url, loadedBitmap);
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            // Do nothing
            // That would be a good place to show error placeholder
        }
    }

    private Bitmap readBitmap(HttpURLConnection connection) throws IOException {
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return BitmapFactory.decodeStream(input);
    }

    private void applyLoadedBitmap(String url, Bitmap loadedBitmap) {
        MAIN_THREAD_HANDLER.post(() -> {
            BITMAP_CACHE.put(url, loadedBitmap);
            bitmap = loadedBitmap;
            invalidateSelf();
        });
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmap != null
                ? bitmap.getWidth()
                : 0;
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap != null
                ? bitmap.getHeight()
                : 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // Do nothing
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // Do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

}
