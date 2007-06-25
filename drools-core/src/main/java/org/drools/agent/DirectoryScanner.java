package org.drools.agent;

import java.io.File;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.rule.Package;

/**
 * This will scan a directory for files to watch for a change.
 * It will update the list of files only if they number of files in a directory changes.
 * 
 * @author Michael Neale
 */
public class DirectoryScanner extends PackageProvider {

    private File[]      currentList;
    private FileScanner scanner;
    private File        dir;

    void configure(Properties config) {
        String d = config.getProperty( RuleAgent.DIRECTORY );

        //now check to see whats in them dir...
        dir = new File( d );
        if ( !(dir.isDirectory() && dir.exists()) ) {
            throw new IllegalArgumentException( "The directory " + d + "is not valid." );
        }

        this.currentList = dir.listFiles();
        scanner = new FileScanner();
        scanner.setFiles( currentList );

    }

    Package[] loadPackageChanges()  {
        if ( currentList.length != dir.listFiles().length ) {
            listener.info( "Extra files detected in the directory " + dir.getPath() );
            currentList = dir.listFiles();
            scanner = new FileScanner();
            scanner.setFiles( currentList );
        }
        return scanner.loadPackageChanges();
    }
    
    public String toString() {
        String s = "DirectoryScanner";
        if (dir != null) {
            s = s + " scanning dir: " + dir.getPath();
        }
        if (currentList != null) {
            s = s + " found " + currentList.length + " file(s).";            
        }
        return s;
    }

}
