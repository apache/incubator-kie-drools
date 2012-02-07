package org.drools.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.junit.Test;

public class MarkTest {
    
    byte[] MARKER_START = "!#".getBytes();
    byte[] MARKER_END = "#!".getBytes();
    byte[] MARKER_MID = "|".getBytes();
    
    @Test
    public void test1() throws IOException {
        String s = "!#1000|0000010|xxxxxxxxxxxxx|#!";
        
        boolean success;
        byte[] bytes = s.getBytes();
        
        
        ObjectInputStream stream = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        byte[] b = new byte[] { stream.readByte(), stream.readByte() };
        if ( bytesEquals(b, MARKER_START) ) {
            return;
        }
        
        b = new byte[] { stream.readByte(), stream.readByte(),  stream.readByte(),  stream.readByte() };
        System.out.println( "version1:" +  Integer.valueOf( new String( b ) ) );
        
        b = new byte[] { stream.readByte(), stream.readByte() };
        
        System.out.println( new String( b ) ) ;
        
        
        
        
        
//        System.out.println( s.getBytes().length );
//        
//        System.out.println( Integer.valueOf( "0001" ) );
    }
    
    public boolean bytesEquals(byte[] b1, byte[] b2) {
        if ( b1.length != b2.length ) {
            return false;
        }
        
        for ( int i = 0; i < b1.length; i++ ) {
            if ( b1[i] != b2[i] ) {
                return false;
            }
        }
        
        return true;
    }
}
