package com.starkasse;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class EnvUtils {

    /**
     * Closeable helper
     *
     * @param c closable object
     */
    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Extract file to env directory
     *
     * @param c         context
     * @param target    target directory
     * @param rootAsset root asset name
     * @param path      path to asset file
     * @return true if success
     */
    private static boolean extractFile(Context c, String target, String rootAsset, String path) {
        AssetManager assetManager = c.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(rootAsset + path);
            File fname = new File(target + path);
            fname.delete();
            out = new FileOutputStream(fname);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(in);
            close(out);
        }
        return true;
    }

    /**
     * Extract path to env directory
     *
     * @param c         context
     * @param target    target directory
     * @param rootAsset root asset name
     * @param path      path to asset directory
     * @return true if success
     */
    private static boolean extractDir(Context c, String target, String rootAsset, String path) {
        AssetManager assetManager = c.getAssets();
        try {
            String[] assets = assetManager.list(rootAsset + path);
            if (assets.length == 0) {
                if (!extractFile(c, target, rootAsset, path)) return false;
            } else {
                String fullPath = target + path;
                File dir = new File(fullPath);
                if (!dir.exists()) dir.mkdir();
                for (String asset : assets) {
                    if (!extractDir(c, target, rootAsset, path + "/" + asset))
                        return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursive remove all from directory
     *
     * @param path path to directory
     */
    private static void cleanDirectory(File path) {
        if (path == null) return;
        if (path.exists()) {
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) cleanDirectory(f);
                f.delete();
            }
            path.delete();
        }
    }

    /**
     * Recursive set permissions to directory
     *
     * @param path path to directory
     */
    private static void setPermissions(File path) {
        if (path == null) return;
        if (path.exists()) {
            path.setReadable(true, false);
            path.setExecutable(true, false);
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) setPermissions(f);
                f.setReadable(true, false);
                f.setExecutable(true, false);
            }
        }
    }

    /**
     * Check root permissions
     *
     * @return true if success
     */
    private static boolean isRooted() {
        boolean result = false;
        OutputStream stdin = null;
        InputStream stdout = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            stdin = process.getOutputStream();
            stdout = process.getInputStream();

            DataOutputStream os = null;
            try {
                os = new DataOutputStream(stdin);
                os.writeBytes("ls /data\n");
                os.writeBytes("exit\n");
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(os);
            }

            int n = 0;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stdout));
                while (reader.readLine() != null) {
                    n++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(reader);
            }

            if (n > 0) {
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(stdout);
            close(stdin);
        }
        return result;
    }

    /**
     * Make linuxdeploy script
     *
     * @param c context
     * @return true if success
     */
    private static boolean makeScript(Context c) {
        String dataDir = c.getApplicationInfo().dataDir;
        boolean result = false;
        String scriptFile = dataDir + "/bin" + "/linuxdeploy";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(scriptFile));
            bw.write("#!" + dataDir + "/bin/sh" + "\n");
            bw.write("PATH=" + dataDir + "/bin" + ":$PATH\n");
            bw.write("ENV_DIR=\"" + dataDir + "/env" + "\"\n");
            bw.write("TEMP_DIR=\"" + dataDir + "/tmp" + "\"\n");
            bw.write(". \"${ENV_DIR}/cli.sh\"\n");
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(bw);
        }
        return result;
    }

    /**
     * Execute commands from system shell
     *
     * @param c      context
     * @param params list of commands
     * @return true if success
     */
    public static boolean exec(final Context c, final String shell, final List<String> params) {
        String dataDir = c.getApplicationInfo().dataDir;
        if (params == null || params.size() == 0) {
            return false;
        }
        if ("su".equals(shell)) {
            if (!isRooted()) {
                return false;
            }
        }
        boolean result = false;
        OutputStream stdin = null;
        InputStream stdout;
        try {
            ProcessBuilder pb = new ProcessBuilder(shell);
            pb.directory(new File(dataDir + "/env"));
            // Map<String, String> env = pb.environment();
            // env.put("PATH", PrefStore.getPath(c) + ":" + env.get("PATH"));
            //if (PrefStore.isDebugMode(c)) pb.redirectErrorStream(true);
            Process process = pb.start();

            stdin = process.getOutputStream();
            stdout = process.getInputStream();

            params.add(0, "PATH=" + dataDir + "/bin" + ":$PATH");
            //if (PrefStore.isTraceMode(c)) params.add(0, "set -x");
            params.add("exit $?");

            DataOutputStream os = null;
            try {
                os = new DataOutputStream(stdin);
                for (String cmd : params) {
                    os.writeBytes(cmd + "\n");
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(os);
            }

            // show stdout log
            final InputStream out = stdout;
            (new Thread() {
                @Override
                public void run() {
                }
            }).start();

            if (process.waitFor() == 0) result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            close(stdin);
        }
        return result;
    }

    /**
     * Update operating environment
     *
     * @param c context
     * @return true if success
     */
    static boolean updateEnv(final Context c) {
        // extract bin assets
        String dataDir = c.getApplicationInfo().dataDir;

        File cliFile = new File(dataDir + "/env/cli.conf");

        if (!extractDir(c, dataDir + "/bin", "bin/all", "")) return false;
        if (!extractDir(c, dataDir + "/bin", "bin/arm", ""))
            return false;

        // extract env assets
        if (!extractDir(c, dataDir + "/env", "env", "")) return false;

        // extract web assets
        if (!extractDir(c, dataDir + "/web", "web", "")) return false;

        // make linuxdeploy script
        if (!makeScript(c)) return false;

        // make tmp directory
        File tmpDir = new File(dataDir + "/tmp");
        tmpDir.mkdirs();

        // make config directory
        File configDir = new File(dataDir + "/config");
        configDir.mkdirs();

        // create .nomedia
        File noMedia = new File(dataDir + "/env" + "/.nomedia");
        try {
            noMedia.createNewFile();
        } catch (IOException ignored) {
        }

        // set permissions
        File binDir = new File(dataDir + "/bin");
        setPermissions(binDir);
        File cgiDir = new File(dataDir + "/web" + "/cgi-bin");
        setPermissions(cgiDir);

        // install applets
        List<String> params = new ArrayList<>();
        params.add("busybox --install -s " + dataDir + "/bin");
        exec(c, "sh", params);

        EnvUtils.makeSymlink(c);

        return true;
    }

    /**
     * Make symlink on linuxdeploy script in /system/bin
     *
     * @param c context
     * @return true if success
     */
    private static boolean makeSymlink(Context c) {
        String dataDir = c.getApplicationInfo().dataDir;

        List<String> params = new ArrayList<>();
        params.add("rm -f /system/bin/linuxdeploy");
        params.add("ln -s "
                + dataDir + "/bin"
                + "/linuxdeploy /system/bin/linuxdeploy || "
                + "{ mount -o rw,remount /system; rm -f /system/bin/linuxdeploy; ln -s "
                + dataDir + "/bin"
                + "/linuxdeploy /system/bin/linuxdeploy; mount -o ro,remount /system; }");
        return exec(c, "su", params);
    }

    /**
     * Execute linuxdeploy script
     *
     * @param c    context
     * @param cmd  command
     * @param args arguments
     * @return true if success
     */
    public static boolean cli(Context c, String cmd, String args) {
        String dataDir = c.getApplicationInfo().dataDir;
        List<String> params = new ArrayList<>();
        String opts = "";
        //if (PrefStore.isDebugMode(c)) opts += "-d ";
        //if (PrefStore.isTraceMode(c)) opts += "-t ";
        if (args == null) args = "";
        else args = " " + args;
        params.add("printf '>>> " + cmd + "\n'");
        params.add(dataDir + "/bin" + "/linuxdeploy " + opts + cmd + args);
        params.add("printf '<<< " + cmd + "\n'");
        String shell = "su";
        return exec(c, shell, params);
    }

}
