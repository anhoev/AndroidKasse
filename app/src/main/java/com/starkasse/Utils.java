package com.starkasse;

import android.Manifest;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.chromium.base.ThreadUtils.postOnUiThreadDelayed;
import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by anhtran on 19.12.17.
 */

public class Utils {
    private MainActivity context;
    private ManagerProcess managerProcess = new ManagerProcess();

    public Utils(MainActivity context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void downloadFromSmb() {
        runOnUiThread(() -> context.downloadFromSmbBtn.setBackgroundColor(Color.RED));
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
                    try {
                        deleteRecursive(new File(Environment.getExternalStorageDirectory().getPath() + "/starkasse"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
        runOnUiThread(() -> {
            context.downloadFromSmbBtn.setBackgroundColor(Color.WHITE);
            Toast.makeText(context, "FINISH !!!", Toast.LENGTH_LONG).show();
        });
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void downloadDataFromSmb() {
        runOnUiThread(() -> context.downloadDataFromSmbBtn.setBackgroundColor(Color.RED));
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
                    try {
                        deleteRecursive(new File(Environment.getExternalStorageDirectory().getPath() + "/data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void downloadIndexFromSmb() {
        runOnUiThread(() -> context.downloadIndexFromSmbBtn.setBackgroundColor(Color.RED));
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
                    try {
                        FileUtils.forceDelete(new File(context.getApplicationInfo().dataDir + "/starkasse/index"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File destFile = new File(context.getApplicationInfo().dataDir + "/starkasse/index");
                    FileUtils.copyInputStreamToFile(src.getInputStream(), destFile);
                }
            }

            Runtime.getRuntime().exec("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");
            Runtime.getRuntime().exec("chmod -R 777 " + "/sdcard/data");

            runOnUiThread(() -> {
                context.downloadIndexFromSmbBtn.setBackgroundColor(Color.WHITE);
                Toast.makeText(context, "FINISH !!!", Toast.LENGTH_LONG).show();
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
                    runOnUiThread(() -> Toast.makeText(context, srcFile.getFileName(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void copyFromUsb(String path) {
        runOnUiThread(() -> context.copyFromUsbBtn.setBackgroundColor(Color.RED));
        new android.os.Handler().postDelayed(
                () -> {
                    try {
                        Process cp1 = Runtime.getRuntime().exec(new String[]{"su", "-c", "cp -R " + path + "/starkasse " + context.getApplicationInfo().dataDir + "/"});
                        cp1.waitFor();
                        Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse"});
                        Process cp2 = Runtime.getRuntime().exec(new String[]{"su", "-c", "cp -R " + path + "/data " + Environment.getExternalStorageDirectory().getPath() + "/"});
                        cp2.waitFor();
                        Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod -R 777 " + Environment.getExternalStorageDirectory().getPath() + "/data"});
                        runOnUiThread(() -> context.copyFromUsbBtn.setBackgroundColor(Color.WHITE));
                        /*FileUtils.copyDirectory(new File(path + "/starkasse"), new File(context.getApplicationInfo().dataDir + "/starkasse/"));
                        File destDir2 = new File(Environment.getExternalStorageDirectory().getPath() + "/data");
                        FileUtils.copyDirectory(new File(path + "/data"), destDir2);*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                500);

    }

    public void runDeployScript(String path) {
        //pm install -r /sdcard/starkasse-main.apk
        runOnUiThread(() -> context.deployScriptBtn.setBackgroundColor(Color.RED));
        new android.os.Handler().postDelayed(
                () -> new Thread(() -> {
                    try {
                        Process cpDeploy = Runtime.getRuntime().exec(new String[]{"su", "-c", "cd " + path + "/deploy && sh deploy.sh"});
                        new Thread(() -> {
                            BufferedReader input = new BufferedReader(new InputStreamReader(cpDeploy.getInputStream()));
                            String line;
                            try {
                                while ((line = input.readLine()) != null) {
                                    String finalLine = line;
                                    runOnUiThread(() -> {
                                        Toast.makeText(context, finalLine, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } catch (IOException e) {
                            }
                        }).start();
                        cpDeploy.waitFor();
                        runOnUiThread(() -> {
                            context.deployScriptBtn.setBackgroundColor(Color.WHITE);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start(),
                500);
    }

    public void runDeployScript2(String path) {
        //pm install -r /sdcard/starkasse-main.apk
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "sh " + path + "/deploy/deploy2.sh"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsbPath() {
        String finalpath = "";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String[] patharray = new String[10];
            int i = 0;
            int available = 0;

            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String mount = new String();
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {// TF card
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1]);

                        patharray[i] = mount;
                        i++;

                        // check directory is exist or not
                        File dir = new File(mount);
                        if (dir.exists() && dir.isDirectory()) {
                            // do something here

                            available = 1;
                            finalpath = mount;
                            break;
                        } else {

                        }
                    }
                }
            }
            if (available == 1) {

            } else if (available == 0) {
                finalpath = patharray[0];
            }

        } catch (Exception e) {

        }
        return finalpath;
    }

    public void startMongo() {

        //waiting for connections on port
        List<String> environment = getEnv();

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/data/mongod.lock");
        if (file.exists()) {
            Log.d("mongo", "REPAIR !!!");
            try {
                new File(Environment.getExternalStorageDirectory().getPath() + "/data/").deleteOnExit();
                repairMongo(environment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("mongo", "START MONGO !!!");

        // /data/starkasse/mongod --bind_ip=127.0.0.1 --dbpath=/sdcard/data --nounixsocket --storageEngine=mmapv1 --unixSocketPrefix=/sdcard/data --noprealloc
        try {
            context.processMongo = Runtime.getRuntime().exec(
                    new String[]{context.getApplicationInfo().dataDir + "/starkasse/mongod", /*"--smallfiles",*/ "--journal"/*, "--noprealloc" */, "--dbpath=" + Environment.getExternalStorageDirectory().getPath() + "/data",
                            "--nounixsocket", "--storageEngine=mmapv1", "--unixSocketPrefix=" + Environment.getExternalStorageDirectory().getPath() + "/data"},
                    environment.toArray(new String[environment.size()])
            );

            Log.d("mongo", "step 2");

            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(context.processMongo.getInputStream()));
                Log.d("mongo", "step 3");
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        Log.d("mongo", line);
                    }
                } catch (IOException e) {
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void repairMongo(List<String> environment) throws IOException, InterruptedException {
        final Process repairProcess = Runtime.getRuntime().exec(
                // /data/data/com.starkasse.kasse/starkasse/mongod --dbpath=/sdcard/data --nounixsocket --unixSocketPrefix=/sdcard/data --repair --storageEngine=mmapv1
                new String[]{context.getApplicationInfo().dataDir + "/starkasse/mongod", "--dbpath=" + Environment.getExternalStorageDirectory().getPath() + "/data",
                        "--nounixsocket", "--storageEngine=mmapv1", "--unixSocketPrefix=" + Environment.getExternalStorageDirectory().getPath() + "/data", "--repair"},
                environment.toArray(new String[environment.size()])
        );

        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(repairProcess.getInputStream()));
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    Log.d("mongo", line);
                }
            } catch (IOException e) {
            }
        }).start();

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
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "pkill mongod"});
        } catch (IOException e) {
            e.printStackTrace();
        }

        managerProcess.kill(new File(context.getApplicationInfo().dataDir + "/starkasse/mongod"));
        if (context.processMongo != null) context.processMongo.destroy();

        try {
            FileUtils.forceDelete(new File(Environment.getExternalStorageDirectory().getPath() + "/data/mongod.lock"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNodejs() {
        final List<String> environment = getEnv();
        environment.add("FONTCONFIG_PATH=" + context.getApplicationInfo().dataDir + "/starkasse/fonts");

        try {
            if (EnvUtils.isRooted()) {
                context.processNode = Runtime.getRuntime().exec(new String[]{"su", "-c", context.getApplicationInfo().dataDir + "/starkasse/index"},
                        environment.toArray(new String[environment.size()])
                );

                /*Process p = Runtime.getRuntime().exec("su");
                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes("cd " + context.getApplicationInfo().dataDir + "/starkasse/update\n");
                dos.writeBytes("./index\n");
                dos.flush();*/

            } else {
                context.processNode = Runtime.getRuntime().exec(
                        new String[]{context.getApplicationInfo().dataDir + "/starkasse/index"},
                        environment.toArray(new String[environment.size()])
                );
            }


            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(context.processNode.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        Log.d("mongo", line);
                    }
                } catch (IOException e) {
                }
            }).start();

        } catch (IOException e) {
        }
    }

    public void stopNode() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "pkill index"});
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (context.processNode == null) return;
        try {
            context.processNode.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        managerProcess.kill(new File(context.getApplicationInfo().dataDir + "/starkasse/index"));
    }

    void startProgram() {
        context.showLoading();
        //EnvUtils.cli(MainActivity.this, "-p linux start", "-m");
        String s = EnvUtils.isRooted() ? "su" : "sh";
        final List<String> environment = getEnv();
        try {
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/index"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/out.log"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/version.json"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/phantomjs"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/mongod"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/update/index"}, environment.toArray(new String[environment.size()]));
            Runtime.getRuntime().exec(new String[]{s, "-c", "chmod 777 " + context.getApplicationInfo().dataDir + "/starkasse/update/blkid"}, environment.toArray(new String[environment.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopMongo();
        stopNode();
        startNodejs();

    }
}
