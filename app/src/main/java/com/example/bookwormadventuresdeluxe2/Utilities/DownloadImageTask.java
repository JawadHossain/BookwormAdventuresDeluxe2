package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * DownloadImageTask performs the asynchronous task of downloading an image from a given
 * url. Upon completion of fetching the image, the corresponding imageView is updated with
 * the resulting Bitmap.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

// https://www.tutorialspoint.com/how-to-get-a-bitmap-from-url-in-android-app
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage)
    {
        this.bmImage = bmImage;
    }

    /**
     * Asynchronously downloads an image from the given url
     *
     * @param urls
     * @return the resulting Bitmap from the download
     */
    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try
        {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return mIcon11;
    }

    /**
     * Upon completion of download, update the given imageView with the dowloaded Bitmap
     *
     * @param result
     */
    protected void onPostExecute(Bitmap result)
    {
        if (result != null)
        {
            bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bmImage.setImageBitmap(result);
        }
    }
}