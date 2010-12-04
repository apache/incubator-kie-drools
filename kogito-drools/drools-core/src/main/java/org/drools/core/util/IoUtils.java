package org.drools.core.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

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

}
