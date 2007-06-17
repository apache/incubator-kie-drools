package org.drools.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.rule.Package;

/**
 * This will monitor a file to a binary package.
 * @author Michael Neale
 *
 */
public class FileScanner {

    private File[] files;
    private Map    lastUpdated = new HashMap();

    public FileScanner() {

    }

    /**
     * This sets the list of files to be monitored.
     * This takes a list of paths and files (not directories).
     */
    public void setFiles(String[] paths) {
        files = new File[paths.length];
        for ( int i = 0; i < paths.length; i++ ) {
            File file = new File( paths[i] );
            if ( !file.exists() ) {
                throw new IllegalArgumentException( "The file " + file.getName() + " does not exist." );
            }
            files[i] = file;
        }
    }

    /**
     * Perform the scan, adding in any packages changed to the rulebase.
     * It will call remove package 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    void updateRuleBase(RuleBase rb) throws FileNotFoundException, IOException, ClassNotFoundException {
        Package[] changes = getChangeSet();
        for ( int i = 0; i < changes.length; i++ ) {
            Package p = changes[i];
            removePackage(p.getName(), rb);
            try {
                rb.addPackage( p );
            } catch ( Exception e ) {
                throw new RuntimeDroolsException( e );
            }
        }
    }

    private void removePackage(String name, RuleBase rb) {
        Package[] ps = rb.getPackages();
        if (ps == null) return;
        for ( int i = 0; i < ps.length; i++ ) {
            Package p = ps[i];
            if (p.getName().equals( name )) {
                rb.removePackage( name );
                return;
            }
        }
    }

    /**
     * Calculate a change set, based on last updated times.
     * (keep a map of files).
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private Package[] getChangeSet() throws FileNotFoundException, IOException, ClassNotFoundException {
        List list = new ArrayList();
        for ( int i = 0; i < files.length; i++ ) {
            File f = files[i];
            if ( hasChanged( f.getPath(), this.lastUpdated, f.lastModified() ) ) {
                list.add( readPackage(f) );
            }
        }
        return (Package[]) list.toArray( new Package[list.size()] );
    }

    public static Package readPackage(File pkgFile) throws IOException,
                                                  FileNotFoundException,
                                                  ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream( new FileInputStream( pkgFile ) );
        Package p1_ = (Package) in.readObject();
        in.close();
        return p1_;
    }

    boolean hasChanged(String path, Map updates, long fileLastModified) {

        if ( !updates.containsKey( path ) ) {
            updates.put( path, new Long( fileLastModified ) );
            return true;
        } else {
            Long last = (Long) updates.get( path );
            if ( last.intValue() < fileLastModified ) {
                updates.put( path, new Long( fileLastModified ) );
                return true;
            } else {
                return false;
            }
        }

    }

}
