package com.gigasource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApp extends AsyncTask<String, Void, Void> {
    private Context context;

    public void setContext(Context contextf) {
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            String PATH = "/mnt/sdcard/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "starkasse.apk");
            if (outputFile.exists()) outputFile.delete();
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = is.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
            }
            fos.close();
            is.close();

            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/starkasse.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/

            File toInstall = new File("/mnt/sdcard/Download/starkasse.apk");
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Uri apkUri = Uri.fromFile(toInstall);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);


        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }
}