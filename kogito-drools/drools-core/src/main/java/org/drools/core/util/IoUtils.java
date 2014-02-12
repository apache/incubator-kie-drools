package org.drools.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    
    public static Map<String, byte[]> indexZipFile(java.io.File jarFile) {
        Map<String, byte[]> files = new HashMap<String, byte[]>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jarFile );
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                byte[] bytes = readBytesFromInputStream( zipFile.getInputStream( entry ) );
                files.put( entry.getName(),
                           bytes );
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get all ZipFile entries: " + jarFile, e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to get all ZipFile entries: " + jarFile, e );
                }
            }
        }
        return files;
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

    public static byte[] readBytesFromInputStream(InputStream is) throws IOException {
        byte[] resultBuff = new byte[0];
        byte[] buff = new byte[2048];
        int k = -1;
        while ((k = is.read(buff, 0, buff.length)) > -1) {
            byte[] tbuff = new byte[resultBuff.length + k];
            System.arraycopy(resultBuff, 0, tbuff, 0, resultBuff.length);
            System.arraycopy(buff, 0, tbuff, resultBuff.length, k);
            resultBuff = tbuff;
        }
        return resultBuff;
    }

    public static byte[] readBytesFromZipEntry(File file, ZipEntry entry) throws IOException {
        if ( entry == null ) {
            return null;
        }

        ZipFile zipFile = null;
        byte[] bytes = null;
        try {
            zipFile = new ZipFile( file );
            bytes = IoUtils.readBytesFromInputStream(  zipFile.getInputStream( entry ) );
        } finally {
            if ( zipFile != null ) {
                zipFile.close();
            }
        }
        return bytes;

    }

    public static byte[] readBytes(File f) throws IOException {
        byte[] buf = new byte[1024];

        BufferedInputStream bais = null;
        ByteArrayOutputStream baos = null;
        try {
            bais = new BufferedInputStream( new FileInputStream( f ) );
            baos = new ByteArrayOutputStream();
            int len;
            while ( (len = bais.read( buf )) > 0 ) {
                baos.write( buf, 0, len );
            }
        } finally {
            if (  baos != null ) {
                baos.close();
            }
            if ( bais != null ) {
                bais.close();
            }
        }

        return baos.toByteArray();
    }

    public static void write(File f,
                             byte[] data) throws IOException {
        if ( f.exists() ) {
            // we want to make sure there is a time difference for lastModified and lastRead checks as Linux and http often round to seconds
            // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
            try {
                Thread.sleep( 1000 );
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to sleep" );
            }
        }

        // Attempt to write the file
        writeBytes(f, data );

        // Now check the file was written and re-attempt if it was not
        // Need to do this for testing, to ensure the texts are read the same way, otherwise sometimes you get tail \n sometimes you don't
        int count = 0;
        while ( !areByteArraysEqual(data, readBytes( f ) ) && count < 5 ) {
            // The file failed to write, try 5 times, calling GC and sleep between each iteration
            // Sometimes windows takes a while to release a lock on a file
            System.gc();
            try {
                Thread.sleep( 250 );
            } catch ( InterruptedException e ) {
                throw new RuntimeException( "This should never happen" );
            }
            writeBytes(f, data );
            count++;
        }

        //areByteArraysEqual

        if ( count == 5 ) {
            throw new IOException( "Unable to write to file:" + f.getCanonicalPath() );
        }
    }

    public static void writeBytes(File f, byte[] data) throws IOException {
        byte[] buf = new byte[1024];

        BufferedOutputStream bos = null;
        ByteArrayInputStream bais = null;

        try {
            bos = new BufferedOutputStream( new FileOutputStream(f) );
            bais = new ByteArrayInputStream( data );
            int len;
            while ( (len = bais.read( buf )) > 0 ) {
                bos.write( buf, 0, len );
            }
        } finally {
            if (  bos != null ) {
                bos.close();
            }
            if ( bais != null ) {
                bais.close();
            }
        }
    }

    public static boolean areByteArraysEqual(byte[] b1,
                                             byte[] b2) {

        if ( b1.length != b2.length ) {
            return false;
        }

        for ( int i = 0, length = b1.length; i < length; i++ ) {
            if ( b1[i] != b2[i] ) {
                return false;
            }
        }

        return true;
    }
}