package org.beiteliahu;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by smgotth on 4/18/2017.
 */

public class SplashActivity extends AppCompatActivity  {
    private DownloaderSplash mDownloader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: check if user has internet enabled.
        loadImagesMainActivity();


    }

/**
* Loads images for MainActivity
* mainLogoBackground  --  http://www.beiteliahu.org/wp-content/themes/prosto2/img/golden/bg_page_title_inner.jpg
* mainLogo  --  http://www.beiteliahu.org/wp-content/uploads/2012/05/BE-logo-trnsp-e1336390354943.png
*
*/
    private void loadImagesMainActivity () {
        String mainLogoBackground = "http://www.beiteliahu.org/wp-content/themes/prosto2/img/golden/bg_page_title_inner.jpg";
        String mainLogo = "http://www.beiteliahu.org/wp-content/uploads/2012/05/BE-logo-trnsp-e1336390354943.png";
        String mainSourceCode = "http://www.beiteliahu.org/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        loadFiles(directory.getAbsolutePath(), "main_title_background.jpg", mainLogoBackground, true);
        loadFiles(directory.getAbsolutePath(), "main_logo.png", mainLogo, true);
        loadFiles(directory.getAbsolutePath(), "mainSourceCode", mainSourceCode, false);
    }

/**
* This method checks basic images if stored and uses those, else downloads.
* @param path Provides pathname to an image.
* @param fileName Name of an image file.
 **/
    private void loadFiles(String path, String fileName, String uri, boolean isImage)
    {
        File file = new File(path,fileName);
        if (file.exists()) {
            Log.d("File","exists:  " + file.getAbsolutePath());
        } else {
            Log.d("File","does not exists:  " + file.getAbsolutePath());
            mDownloader = new DownloaderSplash(uri,this,fileName,isImage);
            mDownloader.execute();
        }

    }
    private class DownloaderSplash extends AsyncTask<Void, Void, Void> {
        String uri, filename, mFileContents;
        boolean isImage;
        Context context;
        Bitmap bitImage;

        private DownloaderSplash(String uri, Context context, String filename, boolean isImage) {
            this.uri = uri;
            this.filename = filename;
            this.isImage = isImage;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (isImage) {
                try {
                    InputStream inputStream = new URL(uri).openStream();
                    bitImage = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("Download Error", "Error downloading image:  " + uri);
                    e.printStackTrace();
                }
            } else {
                mFileContents = downloadFile(uri);
                if (mFileContents == null) {
                    Log.d("Download Error", "Error downloading");
                }
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(mFileContents.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            FileOutputStream foStream;
            if( filename.equalsIgnoreCase("mainSourceCode")  ) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    foStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    bitImage.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                    foStream.close();
                } catch (Exception e) {
                    Log.d("saveImage", "Exception 2, Something went wrong!");
                    e.printStackTrace();
                }
            }
        }
        private String downloadFile (String uri) {
            StringBuilder tempBuffer = new StringBuilder();
            try {
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("Download Error", "The response code was " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charRead = isr.read(inputBuffer);
                    if(charRead <= 0) {
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }
                return tempBuffer.toString();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Download Error", "IO exception reading data: " + e.getMessage());
            }
            return null; //in case of error
        }
    }
}
