package br.ufpe.cin.if710.podcast.service;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Thiago Aquino on 11/10/2017.
 */


public class DownloadService extends IntentService {
    //public static final String DOWNLOAD_COMPLETE = "br.ufpe.cin.if710.podcast.action.DOWNLOAD_COMPLETE";

    public DownloadService() {

        super("Download");
    }

    @Override
    public void onHandleIntent(Intent i) {
            try {
                //checar se tem permissao... Android 6.0+
                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                root.mkdirs();
                File output = new File(root, i.getData().getLastPathSegment());
                if (output.exists()) {
                    output.delete();
                }
                URL url = new URL(i.getData().toString());
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                FileOutputStream fos = new FileOutputStream(output.getPath());
                BufferedOutputStream out = new BufferedOutputStream(fos);
                try {
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[8192];
                    int len = 0;
                    while ((len = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                } finally {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("DOWNLOAD_COMPLETE"));
                    fos.getFD().sync();
                    out.close();
                    c.disconnect();
                }


            } catch (IOException e2) {
                Log.e(getClass().getName(), "Exception durante download", e2);
            }
    }
}

    /*
    @Override
    protected void onHandleIntent(Intent intent) {
        //checar se tem permissao... Android 6.0+
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        root.mkdirs();
        File output = new File(root, intent.getData().getLastPathSegment());


        Uri uri = intent.getData();
        Intent downloadCompleto = new Intent(DOWNLOAD_COMPLETE);
        downloadCompleto.putExtra("uri", output.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DOWNLOAD_COMPLETE));

    }
    */
