package org.drools.kproject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ProjectWriter {    
    private FileSystem fs;
    

    public ProjectWriter(FileSystem fs) {
        this.fs = fs;
    }

    public void write(KProject kproject) {
        writeKBasePaths(kproject);        
        writerKProject(kproject);
        writerKBases(kproject);
        writerKBaseProducers(kproject);
    }
    
    public void writeKBasePaths(KProject kproject) {
//        String[] kbases = kproject.getKbasePaths().keySet().toArray( new String[kproject.getKbasePaths().size()] );
//        Arrays.sort( kbases );
//
        Properties props = new Properties();
//
//        for ( String kbaseQName : kbases ) {
//            Folder folder = fs.getFolder( kproject.getKbasePaths().get( kbaseQName ) );
//            props.setProperty( "kbase." + kbaseQName, folder.getPath().toPortableString() );
//        }
//        
        props.setProperty( "kproject", kproject.getKProjectPath() );
        
        try {
            saveProperties(fs.getRootFolder().getFile( "kbasePaths.properties" ), props);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }    
    
    public void writerKProject(KProject kproject) {
        String[] kbases = kproject.getKBases().keySet().toArray( new String[kproject.getKBases().size()] );
        Arrays.sort( kbases );                        

        StringBuilder sbuilder = new StringBuilder();
        boolean first = true;
        for ( String kbaseQName : kbases ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( kbaseQName );
            first = false;
        }
        
        Properties props = new Properties();
        props.setProperty( "kbaseEntries", sbuilder.toString() );

        try {
            Folder f = fs.getFolder( kproject.getKProjectPath() );
            f.create();
            File file = f.getFile( "kproject.properties" );
            saveProperties(file, props);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }   
    
    public void writerKBases(KProject kproject) {
        String[] kbases = kproject.getKBases().keySet().toArray( new String[kproject.getKBases().size()] );
        Arrays.sort( kbases );

        for ( String kbaseQName : kbases ) {
            writeKBase( kproject, kbaseQName );            
        }        
    }

    private void writeKBase(KProject kproject,
                            String kbaseQName) {
        KBase kbase = kproject.getKBases().get( kbaseQName );
        Folder kbasePath = null; //fs.getFolder( kproject.getKBases().get( kbaseQName ).getPath() );
        
        Properties props = new Properties();
        props.setProperty( "namespace", kbase.getNamespace() );
        props.setProperty( "name", kbase.getName() );
        
        props.setProperty( "eventProcessingMode", kbase.getEventProcessingMode().toExternalForm() );            
        props.setProperty( "equalsBehavior", kbase.getEqualsBehavior().toString()  );                

        Collections.sort( kbase.getAnnotations() );
        StringBuilder sbuilder = new StringBuilder();
        boolean first = true;   
        for ( String str : kbase.getAnnotations() ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( str );                
            first = false;
        }
        props.setProperty( "annotations", sbuilder.toString() );
        
        List<String> files = new ArrayList<String>(kbase.getFiles().size());
        for ( String file : kbase.getFiles() ) {
            files.add(  fs.getFile( file ).getPath().toRelativePortableString( kbasePath.getPath() ) );
        }
        
        Collections.sort( files );
        sbuilder = new StringBuilder();
        first = true;   
        for ( String file : files ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }                
            sbuilder.append( file );                
            first = false;
        }
        
        props.setProperty( "files", sbuilder.toString() );
        
        List<String> ksessions = new ArrayList( kbase.getKSessions().keySet() );                
        Collections.sort( ksessions );
        
        sbuilder = new StringBuilder();
        first = true;
        for ( String ksessionQName : ksessions ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( ksessionQName );
            writeKSession(ksessionQName, kbase, kproject, props );
            first = false;            
        }
        props.setProperty( "ksessions", sbuilder.toString() );
        
        try {                
            kbasePath.create();
            saveProperties( kbasePath.getFile( kbase.getQName() + ".properties" ), props);
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }        
    }
    
    public void writeKSession(String ksessionQName,
                               KBase kbase,
                               KProject kproject, 
                               Properties props) {
        KSession ksession = kbase.getKSessions().get( ksessionQName );
        
        props.setProperty( ksessionQName + ".namespace", ksession.getNamespace() );        
        props.setProperty( ksessionQName + ".name", ksession.getName() );        
        props.setProperty( ksessionQName + ".type", ksession.getType() );        
        props.setProperty( ksessionQName + ".clockType", ksession.getClockType().toString() );
        
        StringBuilder sbuilder = new StringBuilder();
        boolean first = true;   
        for ( String str : kbase.getAnnotations() ) {
            if ( !first ) {
                sbuilder.append( ", " );
            }
            sbuilder.append( str );                
            first = false;
        }    
        props.setProperty( ksessionQName + ".annotations", sbuilder.toString() );                
    }

    public void writerKBaseProducers(KProject kproject) {
        String[] kbases = kproject.getKBases().keySet().toArray( new String[kproject.getKBases().size()] );
        Arrays.sort( kbases );

        try {        
            for ( String kbaseQName : kbases ) {                
                writeKBaseProducer(  kproject.getKBases().get( kbaseQName ), kproject );
            }
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }
    
    public void writeKBaseProducer(KBase kbase,
                                   KProject kproject) throws IOException {
        Folder folder = null; //fs.getFolder( kproject.getKBases().get( kbase.getQName() ).getPath() );
        
        String s = GenerateKBaseProjectFiles.generateProducer( kbase );

        folder = folder.getFolder( kbase.getNamespace().replace( '.', '/' ) );
        folder.create();
        
        File file = folder.getFile( kbase.getName() + "Producer.java" );
        if ( file.exists() ) {
            file.setContents( new ByteArrayInputStream( s.getBytes() ));
        } else {
            file.create( new ByteArrayInputStream( s.getBytes() ) );
        }

        s = GenerateKBaseProjectFiles.generateQualifier( kbase );
        file = folder.getFile(  kbase.getName() + ".java" );
        if ( file.exists() ) {
            file.setContents( new ByteArrayInputStream( s.getBytes() ) );
        } else {
            file.create( new ByteArrayInputStream( s.getBytes() ) );
        }
    }   
    
    public void writerKSessionProducers(KBase kbase, KProject kproject) {
        String[] kbases = kproject.getKBases().keySet().toArray( new String[kproject.getKBases().size()] );
        Arrays.sort( kbases );

        try {        
            for ( String kbaseQName : kbases ) {                
                writeKBaseProducer(  kproject.getKBases().get( kbaseQName ), kproject );
            }
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }    
    
    private static void saveProperties(File file, Properties props) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        props.store( baos, null );
        baos.close();

        if ( file.exists() ) {
            file.setContents( new ByteArrayInputStream( baos.toByteArray() )  );
        } else {
            file.create( new ByteArrayInputStream( baos.toByteArray() ) );
        }
    }
}
