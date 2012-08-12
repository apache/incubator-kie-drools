package org.drools.kproject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KProjectChangeLogCommiter {    
    private KProject kproject;
    private KProjectChangeLog changeLog;
    private FileSystem fs;
    
    public static void commit(KProject kproject, KProjectChangeLog changeLog, FileSystem fs) {
        KProjectChangeLogCommiter committer = new KProjectChangeLogCommiter(kproject, changeLog, fs);
        
        committer.commitAddedKBases();
    }
            
    private KProjectChangeLogCommiter(KProject kproject,
                                     KProjectChangeLog changeLog,
                                     FileSystem fs) {
        super();
        this.kproject = kproject;
        this.changeLog = changeLog;
        this.fs = fs;
    }

    public void commitAddedKBases() {
        for ( KBase kbase : changeLog.getAddedKBases().values() ) {
            commitAddedKBase( kbase );
        }
    }
    
    public void commitAddedKBase(KBase kbase) {        
        // create new KBase root folder
        Folder rootFld = fs.getFolder( kproject.getKBasesPath() + "/"  + kbase.getQName() );
        rootFld.create();
        
        // create new KBase folder for CDI Qualifier and Producer
        Folder namespaceFld = fs.getFolder( rootFld.getPath().toPortableString() + "/"  + kbase.getNamespace().replace( '.', '/' ) );
        namespaceFld.create();
        
        // generate KBase root properties file
        String filesStr = GenerateKBaseProjectFiles.generateKBaseFiles( kproject, kbase, fs );
        File rootFile = rootFld.getFile( kbase.getQName() + ".files.dat" );
        try {
            if ( !rootFile.exists() ) {
                rootFile.create( new ByteArrayInputStream( filesStr.getBytes() ) );
            } else {
                rootFile.setContents( new ByteArrayInputStream( filesStr.getBytes() ) );
            }
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // generate Qualifiers
        String qualifierStr = GenerateKBaseProjectFiles.generateQualifier( kbase );
        File qualifieFile = namespaceFld.getFile( kbase.getName() + ".java" );
        try {
            if ( !qualifieFile.exists() ) {
                qualifieFile.create( new ByteArrayInputStream( qualifierStr.getBytes() ) );
            } else {
                qualifieFile.setContents( new ByteArrayInputStream( qualifierStr.getBytes() ) );
            }
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // generate Producers    
        String producerStr = GenerateKBaseProjectFiles.generateProducer( kbase );
        File producerFile = namespaceFld.getFile( kbase.getName() + "Producer.java" );
        try {
            if ( !producerFile.exists() ) {
                producerFile.create( new ByteArrayInputStream( producerStr.getBytes() ) );
            } else {
                producerFile.setContents( new ByteArrayInputStream( producerStr.getBytes() ) );
            }
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        commitAddedKsessions(rootFld, kbase);
    } 
    
    public void commitAddedKsessions(Folder rootFld, KBase kBase) {
        for ( KSession kSession : kBase.getKSessions().values() ) {
            commitAddedKSession( rootFld, kBase, kSession );
        }
    }

    public void commitAddedKSession(Folder rootFld, KBase kBase, KSession kSession) {        
        // create new KBase folder for CDI Qualifier and Producer
        Folder namespaceFld = fs.getFolder( rootFld.getPath().toPortableString() + "/"  + kSession.getNamespace().replace( '.', '/' ) );
        namespaceFld.create();

        // generate Qualifiers
        String qualifierStr = GenerateKSessionProducer.generateQualifier( kSession );
        File qualifieFile = namespaceFld.getFile( kSession.getName() + ".java" );
        try {
            if ( !qualifieFile.exists() ) {
                qualifieFile.create( new ByteArrayInputStream( qualifierStr.getBytes() ) );
            } else {
                qualifieFile.setContents( new ByteArrayInputStream( qualifierStr.getBytes() ) );
            }
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // generate Producers    
        String producerStr = GenerateKSessionProducer.generateProducer( kBase, kSession );
        File producerFile = namespaceFld.getFile( kSession.getName() + "Producer.java" );
        try {
            if ( !producerFile.exists() ) {
                producerFile.create( new ByteArrayInputStream( producerStr.getBytes() ) );
            } else {
                producerFile.setContents( new ByteArrayInputStream( producerStr.getBytes() ) );
            }
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    
    }
   
}
