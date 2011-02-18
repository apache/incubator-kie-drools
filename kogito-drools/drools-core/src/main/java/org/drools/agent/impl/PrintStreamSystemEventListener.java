/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
