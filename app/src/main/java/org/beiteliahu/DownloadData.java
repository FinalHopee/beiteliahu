package org.beiteliahu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * Created by Sergei Mor G on 19-Apr-17.
 * Class for downloading async media images.
 *
 *
 */

public class DownloadData extends AsyncTask<Void, Void, Void> {
    private Bitmap mBitPicture; // mBitPicture -- Bitmap, downloaded picture
    private String mUri; // address where to take the picture from
    private ImageView mPicture; // where the picture to be saved on activity.
    private Context mContext; // App context, passed from activity
    private String mFileName;
    private boolean mIsPicture;

    public DownloadData(String mUri, Context mContext, String mFileName, boolean mIsPicture) {
        this.mUri = mUri;
        this.mContext = mContext;
        this.mFileName = mFileName;
        this.mIsPicture = mIsPicture;
    }

    public Bitmap getmBitPicture() {
        return mBitPicture;
    }

    public String getUri() {
        return mUri;
    }

    public void setmBitPicture(Bitmap mBitPicture) {
        this.mBitPicture = mBitPicture;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public ImageView getmPicture() {
        return mPicture;
    }

    public void setmPicture(ImageView mPicture) {
        this.mPicture = mPicture;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public boolean ismIsPicture() {
        return mIsPicture;
    }

    public void setmIsPicture(boolean mIsPicture) {
        this.mIsPicture = mIsPicture;
    }



    @Override
    protected Void doInBackground(Void... params) {
        if (ismIsPicture()) {
            try {
                setmBitPicture (BitmapFactory.decodeStream((InputStream) new URL(getUri()).getContent()));
                FileOutputStream fos = getmContext().openFileOutput(getmFileName(), Context.MODE_PRIVATE);
                mBitPicture.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                 Log.e("Download Error", "Error downloading:  " + mUri);
               //e.printStackTrace();
            }
        } else {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        getmContext().openFileOutput(getmFileName(), Context.MODE_PRIVATE));
                outputStreamWriter.write(getUri());
                outputStreamWriter.close();
            }
            catch (IOException e) {
               // Log.e("Exception", "File write failed: " + e.toString());
                Log.e("Download Error", "Error downloading:  " + mUri);
            }


        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
// Use the compress method on the Bitmap object to write image to
// the OutputStream
            FileOutputStream fos = mContext.openFileOutput(getmFileName(), Context.MODE_PRIVATE);

// Writing the bitmap to the output stream
            mBitPicture.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.d("Image","Image saved: " + getmFileName());
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            Log.d("Image","Image couldn't be saved: " + getmFileName());
        }
        //mPicture.setImageBitmap(mBitPicture);
    }
}
