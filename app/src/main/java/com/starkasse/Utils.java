package com.starkasse;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.security.jce.JceSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.auth.NtlmAuthenticator;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.Directory;
import com.hierynomus.smbj.share.DiskShare;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by anhtran on 19.12.17.
 */

public class Utils {
    private MainActivity context;

    public Utils(MainActivity context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void downloadFromSmb() {
        try {
            FileUtils.forceDelete(new File(context.getApplicationInfo().dataDir + "/starkasse"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.downloadFromSmbBtn.setBackgroundColor(Color.RED);
            }
        });
        SmbConfig cfg = SmbConfig.builder().
                withAuthenticators(new NtlmAuthenticator.Factory()).
                withSecurityProvider(new JceSecurityProvider(new BouncyCastleProvider())).
                build();
        SMBClient client = new SMBClient(cfg);

        try {
            try (Connection connection = client.connect("192.168.1.5")) {
                Session session = connection.authenticate(AuthenticationContext.anonymous());

                // Connect to Share
                try (DiskShare share = (DiskShare) session.connectShare("nas-disk1")) {
                    Directory directory = share.openDirectory("android_release\\starkasse\\", EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
                    File destDir = new File(context.getApplicationInfo().dataDir + "/starkasse");
                    doCopyDirectory(directory, destDir, share, "");
                }
            }

            Runtime.getRuntime().exec("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");
            Runtime.getRuntime().exec("chmod -R 777 " + "/sdcard/data");

        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.downloadFromSmbBtn.setBackgroundColor(Color.WHITE);
                Toast.makeText(context, "FINISH !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void downloadDataFromSmb() {
        try {
            deleteRecursive(new File(Environment.getExternalStorageDirectory().getPath() + "/data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.downloadDataFromSmbBtn.setBackgroundColor(Color.RED);
            }
        });
        SmbConfig cfg = SmbConfig.builder().
                withAuthenticators(new NtlmAuthenticator.Factory()).
                withSecurityProvider(new JceSecurityProvider(new BouncyCastleProvider())).
                build();
        SMBClient client = new SMBClient(cfg);

        try {
            try (Connection connection = client.connect("192.168.1.5")) {
                Session session = connection.authenticate(AuthenticationContext.anonymous());

                // Connect to Share
                try (DiskShare share = (DiskShare) session.connectShare("nas-disk1")) {
                    Directory directory2 = share.openDirectory("android_release\\data\\", EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
                    File destDir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/data");
                    destDir2.setWritable(true);
                    doCopyDirectory(directory2, destDir2, share, "");
                }
            }

            Runtime.getRuntime().exec("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");
            Runtime.getRuntime().exec("chmod -R 777 " + Environment.getExternalStorageDirectory().getPath() + "/data");

        } catch (Exception e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.downloadDataFromSmbBtn.setBackgroundColor(Color.WHITE);
                Toast.makeText(context, "FINISH !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void downloadIndexFromSmb() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                context.downloadIndexFromSmbBtn.setBackgroundColor(Color.RED);
            }
        });
        SmbConfig cfg = SmbConfig.builder().
                withAuthenticators(new NtlmAuthenticator.Factory()).
                withSecurityProvider(new JceSecurityProvider(new BouncyCastleProvider())).
                build();
        SMBClient client = new SMBClient(cfg);

        try {
            try (Connection connection = client.connect("192.168.1.5")) {
                Session session = connection.authenticate(AuthenticationContext.anonymous());

                // Connect to Share
                try (DiskShare share = (DiskShare) session.connectShare("nas-disk1")) {
                    com.hierynomus.smbj.share.File src = share.openFile("android_release\\starkasse\\index",
                            EnumSet.of(AccessMask.GENERIC_READ),
                            null,
                            SMB2ShareAccess.ALL,
                            SMB2CreateDisposition.FILE_OPEN,
                            null);
                    FileUtils.forceDelete(new File(context.getApplicationInfo().dataDir + "/starkasse/index"));
                    File destFile = new File(context.getApplicationInfo().dataDir + "/starkasse/index");
                    FileUtils.copyInputStreamToFile(src.getInputStream(), destFile);
                }
            }

            Runtime.getRuntime().exec("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");
            Runtime.getRuntime().exec("chmod -R 777 " + "/sdcard/data");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.downloadIndexFromSmbBtn.setBackgroundColor(Color.WHITE);
                    Toast.makeText(context, "FINISH !!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doCopyDirectory(com.hierynomus.smbj.share.Directory srcDir, File destDir, DiskShare share, String prePathDest)
            throws Exception {

        Field field = srcDir.getClass().getSuperclass().getDeclaredField("fileName");
        field.setAccessible(true);
        String prePath = (String) field.get(srcDir);

        // recurse
        List<FileIdBothDirectoryInformation> srcFiles = srcDir.list(FileIdBothDirectoryInformation.class);
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (final FileIdBothDirectoryInformation srcFile : srcFiles) {
            if (srcFile.getFileName().equals(".") || srcFile.getFileName().equals("..")) continue;
            final File dstFile = new File(destDir, srcFile.getFileName());
            if (srcFile.getEndOfFile() == 0) {
                Directory directory = share.openDirectory(prePath + "\\" + srcFile.getFileName(), EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
                doCopyDirectory(directory, dstFile, share, srcFile.getFileName() + "/");
            } else {
                try {
                    com.hierynomus.smbj.share.File src = share.openFile(prePath + "\\" + srcFile.getFileName(),
                            EnumSet.of(AccessMask.GENERIC_READ),
                            null,
                            SMB2ShareAccess.ALL,
                            SMB2CreateDisposition.FILE_OPEN,
                            null);
                    File destFile = new File(destDir.getPath() + "/" + srcFile.getFileName());
                    FileUtils.copyInputStreamToFile(src.getInputStream(), destFile);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, srcFile.getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void startMongo() {
        try {

            List<String> environment = getEnv();

            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/data/mongod.lock");
            if (file.exists()) {
                repairMongo(environment);
            }


            // /data/starkasse/mongod --bind_ip=127.0.0.1 --dbpath=/data/starkasse/data --nounixsocket --storageEngine=mmapv1 --unixSocketPrefix=/data/starkasse/data
            context.processMongo = Runtime.getRuntime().exec(
                    new String[]{context.getApplicationInfo().dataDir + "/starkasse/mongod", "--dbpath=" + Environment.getExternalStorageDirectory().getPath() + "/data",
                            "--nounixsocket", "--storageEngine=mmapv1", "--unixSocketPrefix=" + Environment.getExternalStorageDirectory().getPath() + "/data"},
                    environment.toArray(new String[environment.size()])
            );

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(context.processMongo.getInputStream()));
                    String line;
                    try {
                        while ((line = input.readLine()) != null) {
                            Log.d("mongo", line);
                        }
                    } catch (IOException e) {
                    }
                }
            }).start();


        } catch (Exception e) {
        }
    }

    public void repairMongo(List<String> environment) throws IOException, InterruptedException {
        Process repairProcess = Runtime.getRuntime().exec(
                // /data/data/com.starkasse.kasse/starkasse/mongod --dbpath=/sdcard/data --nounixsocket --unixSocketPrefix=/sdcard/data --repair --storageEngine=mmapv1
                new String[]{context.getApplicationInfo().dataDir + "/starkasse/mongod", "--dbpath=" + Environment.getExternalStorageDirectory().getPath() + "/data",
                        "--nounixsocket", "--storageEngine=mmapv1", "--unixSocketPrefix=" + Environment.getExternalStorageDirectory().getPath() + "/data", "--repair"},
                environment.toArray(new String[environment.size()])
        );
        repairProcess.waitFor();
    }

    @NonNull
    public List<String> getEnv() {
        Map<String, String> map = System.getenv();
        List<String> environment = new ArrayList<>();
        for (String key : map.keySet()) {
            environment.add(key + "=" + map.get(key));
        }
        return environment;
    }

    public void stopMongo() {
        if (context.processMongo != null) context.processMongo.destroy();
        //File file = new File(context.getApplicationInfo().dataDir + "/starkasse/data/mongod.lock");
        //file.deleteOnExit();
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "pkill mongod"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNodejs() {
        final List<String> environment = getEnv();
        environment.add("FONTCONFIG_PATH=" + context.getApplicationInfo().dataDir + "/starkasse/fonts");

        try {
            String s = EnvUtils.isRooted() ? "su" : "sh";
            context.processNode = Runtime.getRuntime().exec(
                    new String[]{s, "-c", context.getApplicationInfo().dataDir + "/starkasse/index"},
                    environment.toArray(new String[environment.size()])
            );

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader input = new BufferedReader(new InputStreamReader(context.processNode.getInputStream()));
                    String line;
                    try {
                        while ((line = input.readLine()) != null) {
                            Log.d("mongo", line);
                        }
                    } catch (IOException e) {
                    }
                }
            }).start();

        } catch (IOException e) {
        }
    }

    public void stopNode() {
        if (context.processNode == null) return;
        try {
            context.processNode.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "pkill index"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void startProgram() {
        context.showLoading();
        //EnvUtils.cli(MainActivity.this, "-p linux start", "-m");
        String s = EnvUtils.isRooted() ? "su" : "sh";
        final List<String> environment = getEnv();
        try {
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/index"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/version.json"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/phantomjs"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/mongod"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/update/index"}, environment.toArray(new String[environment.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startMongo();
        startNodejs();
    }
}
