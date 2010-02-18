package org.drools.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.core.util.DroolsStreamUtils;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

/**
 * This will monitor a file to a binary package.
 * 
 * @author Michael Neale
 * 
 */
public class FileScanner extends PackageProvider {

    File[]              files;
    Map                 lastUpdated   = new HashMap();
    Map<String, String> pathToPackage = null;

    /**
     * This sets the list of files to be monitored. This takes a list of paths
     * and files (not directories).
     */
    void configure(Properties config) {
        List paths = RuleAgent.list( config.getProperty( RuleAgent.FILES ) );
        files = new File[paths.size()];
        for ( int i = 0; i < paths.size(); i++ ) {
            File file = new File( (String) paths.get( i ) );
            if ( !file.exists() ) {
                throw new IllegalArgumentException( "The file " + file.getName() + " does not exist." );
            }
            files[i] = file;
        }
    }

    /**
     * An alternative way to configure.
     */
    void setFiles(File[] files) {
        this.files = files;
    }

    /**
     * Perform the scan. If there was an error reading the packages, this will
     * not fail, it will just do nothing (as there may be a temporary IO issue).
     */
    PackageChangeInfo loadPackageChanges() {
        PackageChangeInfo changes = getChangeSet();
        return changes;
    }

    /**
     * Calculate a change set, based on last updated times. (keep a map of
     * files).
     */
    private PackageChangeInfo getChangeSet() {
        PackageChangeInfo info = new PackageChangeInfo();
        if ( this.files == null ) return info;

        if ( pathToPackage == null ) pathToPackage = new HashMap<String, String>();

        for ( int i = 0; i < files.length; i++ ) {
            File f = files[i];

            if ( !f.exists() ) {
                String name = pathToPackage.get( f.getPath() );
                if ( name != null ) {
                    info.addRemovedPackage( name );
                }
            } else if ( hasChanged( f.getPath(),
                                    this.lastUpdated,
                                    f.lastModified() ) ) {
                Package p = readPackage( f );
                if ( p != null ) {
                    info.addPackage( p );
                    pathToPackage.put( f.getPath(),
                                       p.getName() );
                }
            }
        }
        return info;
    }

    /**
     * If an exception occurs, it is noted, but ignored. Especially IO, as
     * generally they are temporary.
     */
    private Package readPackage(File pkgFile) {

        String name = pkgFile.getName();
        if ( !(name.endsWith( ".pkg" ) || name.endsWith( ".drl" ) || name.endsWith( ".xls" )) ) {
            return null;
        }
        // use reflection to load if its DRL, the provider lives in drools
        // compiler.
        if ( pkgFile.getName().endsWith( ".drl" ) ) {
            try {
                FileLoader fl = (FileLoader) Class.forName( "org.drools.compiler.SourcePackageProvider" ).newInstance();
                return fl.loadPackage( pkgFile );
            } catch ( Exception e ) {
                this.listener.exception( e );
                return null;
            }

            // use reflection to load if its XLS, the provider lives in drools
            // decision tables.
        } else if ( pkgFile.getName().endsWith( ".xls" ) ) {
            try {
                FileLoader fl = (FileLoader) Class.forName( "org.drools.decisiontable.SourcePackageProvider" ).newInstance();
                return fl.loadPackage( pkgFile );
            } catch ( Exception e ) {
                this.listener.exception( e );
                return null;
            }

        } else {

            Object o = null;
            try {

                FileInputStream fis = new FileInputStream( pkgFile );

                o = DroolsStreamUtils.streamIn( fis );

                fis.close();

            } catch ( FileNotFoundException e ) {
                this.listener.exception( e );
                this.listener.warning( "Was unable to find the file " + pkgFile.getPath() );
            } catch ( IOException e ) {
                this.listener.exception( e );
            } catch ( ClassNotFoundException e ) {
                this.listener.exception( e );
                this.listener.warning( "Was unable to load a class when loading a package. Perhaps it is missing from this application." );
            }

            if ( o instanceof KnowledgePackageImp ) {
                return ((KnowledgePackageImp) o).pkg;
            } else {
                return (Package) o;
            }
        }
    }

    boolean hasChanged(String path,
                       Map updates,
                       long fileLastModified) {

        if ( !updates.containsKey( path ) ) {
            updates.put( path,
                         new Long( fileLastModified ) );
            return true;
        } else {
            Long last = (Long) updates.get( path );
            if ( last.longValue() < fileLastModified ) {
                updates.put( path,
                             new Long( fileLastModified ) );
                return true;
            } else {
                return false;
            }
        }

    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append( "FileScanner scanning: " );
        for ( int i = 0; i < files.length; i++ ) {
            File f = files[i];
            buf.append( f.getPath() + " " );
        }
        return buf.toString();
    }

}
