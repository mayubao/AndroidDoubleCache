package com.likefunnythings.androiddoublecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.likefunnythings.androiddoublecache.util.DoubleCacheManager;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends ActionBarActivity {

    private ImageView imageView;

    private String urlPath = "http://kanmeizi.likefunnythings.com/wp-content/themes/iphoto/images/logo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        /*
        ((ImageView)findViewById(R.id.imageView)).post(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://kanmeizi.likefunnythings.com/wp-content/themes/iphoto/images/logo.png");
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    InputStream is = conn.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        */
    }

    private void init() {
        imageView = (ImageView) this.findViewById(R.id.imageView);

        new AsyncTask<String, Integer, Bitmap>() {

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                super.onPostExecute(bitmap);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                bitmap = DoubleCacheManager.getInstance(MainActivity.this).getBitmap(urlPath);
                if(null == bitmap){
                    bitmap = getImageFromNetWork();
                    DoubleCacheManager.getInstance(MainActivity.this).putBitmap(urlPath, bitmap);
                }
                return bitmap;
            }
        }.execute("");
    }


    /**
     * 获取网络图片
     * @return
     */
    private Bitmap getImageFromNetWork(){
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlPath);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
