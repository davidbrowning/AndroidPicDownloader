package com.dmbrowning21.networkpicturedownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/*credit to Sarbajit Mukherjee for much of the logic and several lines of code*/

public class MainActivity extends Activity implements View.OnClickListener {
    final static String DYERS_URL  = "http://www.ibiblio.org/wm/paint/auth/hiroshige/dyers.jpg";
    final static String MOON_PINE_URL = "http://www.ibiblio.org/wm/paint/auth/hiroshige/moonpine.jpg";
    final static String PLUM_ESTATE_URL = "http://www.ibiblio.org/wm/paint/auth/hiroshige/plum.jpg";
    final static String USHIMACHI_URL = "http://www.ibiblio.org/wm/paint/auth/hiroshige/takanawa.jpg";
    ImageView daView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button dyers_Btn = (Button) findViewById(R.id.dyers);
        Button moonPine_Btn = (Button) findViewById(R.id.moonPine);
        Button plumEstate_Btn = (Button) findViewById(R.id.plumEstate);
        Button ushimachi_Btn = (Button) findViewById(R.id.ushimachi);
        daView = (ImageView) findViewById(R.id.imageView);

        dyers_Btn.setOnClickListener(this);
        moonPine_Btn.setOnClickListener(this);
        plumEstate_Btn.setOnClickListener(this);
        ushimachi_Btn.setOnClickListener(this);

    }

    ProgressDialog myProgressBar;
    int status = 0;
    Handler myHandler = new Handler();
    long fileSize = 0;
    static final String dl = "Downloading...";
    static final String PATH = "/data/data/com.dmbrowning21.networkpicturedownloader/dlfile.jpg";

    @Override
    public void onClick(View v) {
        myProgressBar = new ProgressDialog(v.getContext());
        myProgressBar.setCancelable(true);
        myProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        myProgressBar.setProgress(0);
        myProgressBar.setMax(100);
        myProgressBar.show();
        final String activeURL;

        if(v.getId() == R.id.dyers){
            activeURL = DYERS_URL;
        } else if (v.getId() == R.id.moonPine) {
            activeURL = MOON_PINE_URL;
        } else if (v.getId() == R.id.plumEstate){
            activeURL = PLUM_ESTATE_URL;
        }
        else{
            activeURL = USHIMACHI_URL;
        }

        status = 0;
        fileSize = 0;

        Thread thread = new Thread()
        {
            public void run() {
             networkFileDownload(activeURL);
            }
        };
        thread.start();
    }

    public void networkFileDownload(String s){
        while (status < 100){
            int counter = 0;
            try{
                URL url = new URL(s);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(PATH, false);

                byte data[] = new byte[1024];
                long total = 0;

                while ((counter = input.read(data)) != -1) {
                    total += counter;

                    // writing data to file
                    output.write(data, 0, counter);
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    status = (int) ((total * 100) / lengthOfFile);
                    // update the progress bar
                    myHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            myProgressBar.setProgress(status);
                            if (status == 100) {
                                myProgressBar.setMessage("done");
//                                mProgressBar.setMessage(FILE_PATH);
                                daView.setImageDrawable(Drawable
                                        .createFromPath(PATH));
                            }
                        }
                    });
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();


            }catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }
        if(status >= 100){
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            myProgressBar.dismiss();
        }
    }
}
