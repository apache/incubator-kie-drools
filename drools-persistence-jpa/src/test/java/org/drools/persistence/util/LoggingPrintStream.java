package org.drools.persistence.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingPrintStream extends PrintStream {

    private Logger logger;
    private StringBuilder buffer = new StringBuilder();
    
    public LoggingPrintStream(OutputStream outputStream) { 
        super(outputStream);
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        try {
            logger = LoggerFactory.getLogger(Class.forName(className));
        } catch (ClassNotFoundException e) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
    }
    
    protected void log(String s) { 
        logger.debug(s);
    }
    
    public void write(int b) {
        log(String.valueOf(b));
    }
    
    public void write(byte buf[], int off, int len) {
        if (buf == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > buf.length) || (len < 0) ||
               ((off + len) > buf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0 ; i < len ; i++) {
            write(buf[off + i]);
        }
        for (int i = 0 ; i < len ; i++) {
            write(buf[off + i]);
        }
    }

    private void write(byte b) {
        buffer.append(Byte.toString(b));
    }
    
    private void write(String s) { 
        buffer.append(s);
    }
    
    private void newLine() { 
        synchronized(buffer) { 
            log(buffer.toString());
            buffer.delete(0, buffer.length());
        }
    }
    
    public void print(boolean b) {
        write(b ? "true" : "false");
    }
    
    public void print(char c) {
        write(String.valueOf(c));
    }
    
    public void print(int i) {
        write(String.valueOf(i));
    }
    
    public void print(long l) {
        write(String.valueOf(l));
    }
    
    public void print(float f) {
        write(String.valueOf(f));
    }
    
    public void print(double d) {
        write(String.valueOf(d));
    }
    
    public void print(char s[]) {
        write(String.valueOf(s));
    }
    
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }
    
    public void print(Object obj) {
        write(String.valueOf(obj));
    }
    
    public void println() {
        newLine();
    }
    
    public void println(boolean x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(char x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(int x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(long x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(float x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(double x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(char x[]) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(String x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
    
    public void println(Object x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }
}