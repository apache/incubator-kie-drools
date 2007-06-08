package org.drools.agent;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This will monitor a file to a binary package.
 * @author Michael Neale
 *
 */
public class FileScanner {

    public FileScanner(String path, int poll) {
        File f = new File(path);
        Timer timer = new Timer();
        timer.schedule( task(f), poll * 60 );
    }

    private TimerTask task(final File file) {
        return new TimerTask() {
            public void run() {
                    file.lastModified();
            }
        };
    }
    
    
}
