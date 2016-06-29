package com.sasisa.chat.sasisachat.messages;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.sasisa.chat.sasisachat.GlobalMediaPlayer;
import com.sasisa.chat.sasisachat.HttpGetter;
import com.sasisa.chat.sasisachat.ImageCache;
import com.sasisa.chat.sasisachat.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by cherry on 07.09.2015.
 */
public class URLImageParser implements Html.ImageGetter {
    Context c;
    View container;
    private float imageSize;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public URLImageParser(View t, Context c) {
        this.c = c;
        this.container = t;

        Resources res = c.getResources();
        imageSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, res.getDisplayMetrics());
    }

    public Drawable getDrawable(final String source) {
        final URLDrawable urlDrawable = new URLDrawable();

        Canvas canvas = new Canvas();
        canvas.drawRect(0, 0, imageSize, imageSize, new Paint());
        urlDrawable.draw(canvas);

        // get the actual source
        /*ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask( urlDrawable);

        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, source);*/

        final Handler uiHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Drawable result = fetchDrawable(source);

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            // set the correct bound according to the result from HTTP call
                            urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());

                            // change the reference of the current drawable to the result
                            // from the HTTP call
                            urlDrawable.drawable = result;

                            // redraw the image by invalidating the container
                            URLImageParser.this.container.invalidate();
                        }
                    }
                });
            }

            public Drawable fetchDrawable(String urlString) {
                try {
                    Drawable drawable = ImageCache.getInstance().getDrawable(urlString);
                    if (drawable == null) {
                        InputStream is = fetch(urlString);
                        drawable = Drawable.createFromStream(is, "src");
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        float w = drawable.getIntrinsicWidth();
                        float h = drawable.getIntrinsicHeight();
                            w = imageSize / h * w;
                            h = imageSize;
                        drawable = new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, (int)w, (int)h, true));
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        ImageCache.getInstance().addDrawable(urlString, drawable);
                    }
                    return drawable;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                if (!urlString.contains("http")) {
                    urlString = String.format("http://wap.sasisa.ru/chat/%s", urlString);
                }
                InputStream is = new java.net.URL(urlString).openStream();
                return is;
            }
        }).start();

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                // set the correct bound according to the result from HTTP call
                urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();
            }
        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                Drawable drawable = ImageCache.getInstance().getDrawable(urlString);
                if (drawable == null) {
                    InputStream is = fetch(urlString);
                    drawable = Drawable.createFromStream(is, "src");
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageCache.getInstance().addDrawable(urlString, drawable);
                }
                return drawable;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            if (!urlString.contains("http")) {
                urlString = String.format("http://wap.sasisa.ru/chat/%s", urlString);
            }
            InputStream is = new java.net.URL(urlString).openStream();
            return is;
        }
    }
}