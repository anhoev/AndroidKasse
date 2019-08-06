package com.starkasse;

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
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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
                    shell("cp -R " + path + "/starkasse " + context.getApplicationInfo().dataDir + "/");
                    shell("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");
                    shell("cp -R " + path + "/data " + Environment.getExternalStorageDirectory().getPath() + "/");
                    shell("chmod -R 777 " + Environment.getExternalStorageDirectory().getPath() + "/data");
                    runOnUiThread(() -> context.copyFromUsbBtn.setBackgroundColor(Color.WHITE));
                },
                500);

    }

    public void runDeployScript(String path) {
        //pm install -r /sdcard/starkasse-main.apk
        runOnUiThread(() -> context.deployScriptBtn.setBackgroundColor(Color.RED));
        new android.os.Handler().postDelayed(
                () -> new Thread(() -> {
                    try {
                        Process cpDeploy = shellSpawn("cd " + path + "/deploy && sh deploy.sh", null);
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
        shellSpawn("cd " + path + "/deploy && sh deploy2.sh", null);
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

                if (line.contains("fat") || line.contains("vfat")) {// TF card
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        //for (String column : columns)
                        //    if (column.contains("/mnt")) mount = mount.concat(column);

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

    static public Process shellSpawn(String cmd, String[] env) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(EnvUtils.isRooted() ? "su" : "sh", env);
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(String.format("%s\n", cmd));
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    static public void shell(String cmd) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(EnvUtils.isRooted() ? "su" : "sh");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(String.format("%s\n", cmd));
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMongo() {
        shell("pkill mongod");
        managerProcess.kill(new File(context.getApplicationInfo().dataDir + "/starkasse/mongod"));
        if (context.processMongo != null) context.processMongo.destroy();

        try {
            FileUtils.forceDelete(new File(Environment.getExternalStorageDirectory().getPath() + "/data/mongod.lock"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNodejs() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            Log.d("node", ste.toString());
        }

        final List<String> environment = getEnv();
        environment.add("FONTCONFIG_PATH=" + context.getApplicationInfo().dataDir + "/starkasse/fonts");
        try {
            if (EnvUtils.isRooted()) {
                Log.d("node", "start from apk !!!");
                shell("pm grant com.starkasse.kasse android.permission.ACCESS_FINE_LOCATION");
                shell("pm grant com.starkasse.kasse android.permission.ACCESS_COARSE_LOCATION");
                context.processNode = shellSpawn(context.getApplicationInfo().dataDir + "/starkasse/index", environment.toArray(new String[environment.size()]));
            } else {
                Log.d("node", "start from apk without root!!!");
                context.processNode = Runtime.getRuntime().exec(new String[]{context.getApplicationInfo().dataDir + "/starkasse/index"}, environment.toArray(new String[environment.size()])
                );
            }

            new Thread(() -> {
                BufferedReader input = new BufferedReader(new InputStreamReader(context.processNode.getInputStream()));
                String line;
                try {
                    while ((line = input.readLine()) != null) {
                        Log.d("node", line);
                    }
                } catch (IOException e) {
                }
            }).start();

            new Thread(() -> {
                BufferedReader input2 = new BufferedReader(new InputStreamReader(context.processNode.getErrorStream()));
                String line;
                try {
                    while ((line = input2.readLine()) != null) {
                        Log.d("node_err", line);
                    }
                } catch (IOException e) {
                }
            }).start();

        } catch (IOException e) {
        }
    }

    public void stopNode() {
        if (context.processNode == null) return;
        try {
            context.processNode.destroy();
        } catch (Exception ignored) {
        }
        try {
            managerProcess.kill(new File(context.getApplicationInfo().dataDir + "/starkasse/index"));
            managerProcess.kill(new File(context.getApplicationInfo().dataDir + "/starkasse/update/index"));
        } catch (Exception ignored) {
        }
    }

    public String getArg1() {
        String arg1 = "-c";
        if (!(new File("/system/xbin/su").exists())) arg1 = "root";
        return arg1;
    }

    void startProgram() {
        Log.d("node", "startProgram");
        context.showLoading();
        //EnvUtils.cli(MainActivity.this, "-p linux start", "-m");

        shell("chmod -R 777 " + context.getApplicationInfo().dataDir);
        shell("chmod -R 777 " + context.getApplicationInfo().dataDir + "/starkasse");

        stopNode();
        if (new File("/sdcard").exists()) {
            startNodejs();
        } else {
            Log.d("node", "sdcard doesn't exists");
            startNodejs();
            //MainActivity.reboot();
            //(new Handler()).postDelayed(this::startNodejs, 6000);
        }
    }
}
