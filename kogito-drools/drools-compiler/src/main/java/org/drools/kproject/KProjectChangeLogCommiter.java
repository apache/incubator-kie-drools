package org.drools.kproject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class KProjectChangeLogCommiter {
    private KProject          kProject;
    private KProjectChangeLog changeLog;
    private FileSystem        fs;

    /**  Generates only the qualifiers and producers for the modified items in the KProjectChangeLog
     * 
     * @param kProject
     * @param changeLog
     * @param fs
     */
    public static void commit(KProject kProject,
                              KProjectChangeLog changeLog,
                              FileSystem fs) {
        KProjectChangeLogCommiter committer = new KProjectChangeLogCommiter( kProject, changeLog, fs );

        committer.commitRemovedKBases();
        committer.commitAddedKBases();
        
        committer.commitRemovedKSessions();
        committer.commitAddedKSessions();        

        changeLog.reset();
    }

    /**  Generates qualifiers and producers for the entire KProject
     * 
     * @param kProject
     * @param changeLog
     * @param fs
     */
    public static void commit(KProject kProject,
                              FileSystem fs) {
        KProjectChangeLogCommiter committer = new KProjectChangeLogCommiter( kProject, null, fs );
        for ( KBase kBase : kProject.getKBases().values() ) {
            committer.commitAddedKBase( kBase);
            for ( KSession kSession : kBase.getKSessions().values() ) {
                Folder rootFld = fs.getFolder( kProject.getKBasesPath() + "/" + kBase.getQName() );
                committer.commitAddedKSession( rootFld, kBase, kSession );
            }
        }
    }   
    
    private KProjectChangeLogCommiter(KProject kproject,
                                      KProjectChangeLog changeLog,
                                      FileSystem fs) {
        super();
        this.kProject = kproject;
        this.changeLog = changeLog;
        this.fs = fs;
    }

    public void commitAddedKBases() {
        for ( String kBaseQName : changeLog.getAddedKBases() ) {
            commitAddedKBase( kProject.getKBases().get( kBaseQName ) );
        }
    }

    public void commitAddedKBase(KBase kbase) {
        // create new KBase root folder
        Folder rootFld = fs.getFolder( kProject.getKBasesPath() + "/" + kbase.getQName() );
        rootFld.create();

        // create new KBase folder for CDI Qualifier and Producer
        Folder namespaceFld = fs.getFolder( rootFld.getPath().toPortableString() + "/" + kbase.getNamespace().replace( '.', '/' ) );
        namespaceFld.create();

        // generate KBase root properties file
        String filesStr = GenerateKBaseProjectFiles.generateKBaseFiles( kProject, kbase, fs );
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

        //commitAddedKsessions( rootFld, kbase );
    }
    
    public void commitRemovedKBases() {
        for ( String kBaseQName : changeLog.getRemovedKBases() ) {
            commitRemovedKBase( kProject.getKBases().get( kBaseQName ) );
        }
    }

    public void commitRemovedKBase(KBase kbase) {
        for ( String kBaseQName : changeLog.getRemovedKBases() ) {
            Folder rootFld = fs.getFolder( kProject.getKBasesPath() + "/" + kBaseQName );
            fs.remove( rootFld );
        }
    }    

    public void commitAddedKSessions() {
        for ( String kSessionQName : changeLog.getAddedKSessions() ) {
            KSessionImpl kSession = (KSessionImpl) changeLog.getKSessions().get( kSessionQName );
            Folder rootFld = fs.getFolder( kProject.getKBasesPath() + "/" + kSession.getKBase().getQName() );
            commitAddedKSession( rootFld, kSession.getKBase(), kSession );
        }
    }

    //    public void commitAddedKsessions(Folder rootFld,
    //                                     KBase kBase) {
    //        for ( KSession kSession : kBase.getKSessions().values() ) {
    //            commitAddedKSession( rootFld, kBase, kSession );
    //        }
    //    }

    public void commitAddedKSession(Folder rootFld,
                                    KBase kBase,
                                    KSession kSession) {
        // create new KSession folder for CDI Qualifier and Producer
        Folder namespaceFld = fs.getFolder( rootFld.getPath().toPortableString() + "/" + kSession.getNamespace().replace( '.', '/' ) );
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

    public void commitRemovedKSessions() {
        for ( String kSessionQName : changeLog.getRemovedKSessions() ) {
            KSessionImpl kSession = (KSessionImpl) changeLog.getKSessions().get( kSessionQName );
            Folder rootFld = fs.getFolder( kProject.getKBasesPath() + "/" + kSession.getKBase().getQName() );
            commitRemovedKSession( rootFld, kSession.getKBase(), kSession );
        }
    }

    public void commitRemovedKSession(Folder rootFld,
                                      KBase kBase,
                                      KSession kSession) {
        // @TODO currently leaves nested folders, need to delete, if no nested KSession folders.
        // get KSession folder for CDI Qualifier and Producer
        Folder namespaceFld = fs.getFolder( rootFld.getPath().toPortableString() + "/" + kSession.getNamespace().replace( '.', '/' ) );

        // generate Qualifiers
        File qualifieFile = namespaceFld.getFile( kSession.getName() + ".java" );
        fs.remove( qualifieFile );

        // generate Producer
        File producerFile = namespaceFld.getFile( kSession.getName() + "Producer.java" );
        fs.remove( producerFile );
    }

}
