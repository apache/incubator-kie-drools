package org.drools.agent.impl;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.SystemEventListener;

public class PrintStreamSystemEventListener
    implements
    SystemEventListener {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy:MM:DD HH:MM:SS");
    
    private PrintStream print = System.out;
    
    public PrintStreamSystemEventListener() {
        
    }
    
    public PrintStreamSystemEventListener(PrintStream print) {
        this.print = print;
    }
    
    private String time() {
        return dateFormat.format( new Date() );
    }

    public void debug(String message) {
        print.println( "[" +  time() + ":debug] " + message );
    }
    
    public void debug(String message,
                      Object object) {
        print.println( "[" +  time() + ":debug] " + message + " object=" + object );
    }    

     public void exception(String message, Throwable e) {
        print.println( "[" +  time() + ":exception] " + message );
        e.printStackTrace( print );
    }

    public void exception(Throwable e) {
        print.println( "[" +  time() + ":exception]" );
        e.printStackTrace( print );
    }

    public void info(String message) {
        print.println( "[" +  time() + ":info] " + message );
    }
    
    public void info(String message,
                     Object object) {
        print.println( "[" +  time() + ":info] " + message + " object=" + object);        
    }    

    public void warning(String message) {
        print.println( "[" +  time() + ":warning] " + message );
    }

    public void warning(String message,
                        Object object) {
        print.println( "[" +  time() + ":debug] " + message + " object=" + object );
    }

}
