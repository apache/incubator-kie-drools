package org.drools.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class IoUtils {

    public static int findPort() {
        for( int i = 1024; i < 65535; i++) {
            if ( validPort( i ) ) {
                return i;
            }
        }
        throw new RuntimeException( "No valid port could be found" );
    }
    
    public static boolean validPort(int port) {

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static String readFileAsString(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) { }
            }
        }
        return sb.toString();
    }

    public static void copyFile(File sourceFile, File destFile) {
        destFile.getParentFile().mkdirs();
        if(!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (IOException ioe) {
                throw new RuntimeException("Unable to create file " + destFile.getAbsolutePath(), ioe);
            }
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to copy " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath(), ioe);
        } finally {
            if(source != null) {
                try {
                    source.close();
                } catch (IOException e) { }
            }
            if(destination != null) {
                try {
                    destination.close();
                } catch (IOException e) { }
            }
        }
    }

    public static List<String> recursiveListFile(File folder) {
        return recursiveListFile(folder, "", Predicate.PassAll.INSTANCE);
    }

    public static List<String> recursiveListFile(File folder, String prefix, Predicate<? super File> filter) {
        List<String> files = new ArrayList<String>();
        for (File child : folder.listFiles()) {
            filesInFolder(files, child, prefix, filter);
        }
        return files;
    }

    private static void filesInFolder(List<String> files, File file, String relativePath, Predicate<? super File> filter) {
        if (file.isDirectory()) {
            relativePath += file.getName() + "/";
            for (File child : file.listFiles()) {
                filesInFolder(files, child, relativePath, filter);
            }
        } else {
            if (filter.apply(file)) {
                files.add(relativePath + file.getName());
            }
        }
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        long length = is.available();
        byte[] bytes = new byte[(int) length];
        is.read(bytes);
        is.close();
        return bytes;
    }
}
