package org.drools.kproject;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.conf.ClockTypeOption;
import org.junit.Test;

public class KProjectDirtyLogTest {

    @Test
    public void testKProjectModified() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        kproj.setKProjectPath( "src/main/resources/" );
        kproj.setKBasesPath( "src/kbases" );

        assertTrue( dirtyLog.isKProjectDirty() );
    }

    @Test
    public void testKBaseAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        
        kproj.addKBase( kbase1 );
        assertEquals( kbase1, dirtyLog.getAddedKBases().get( kbase1.getQName() ) );
    }

    @Test
    public void testKBaseRemoved() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        
        kproj.addKBase( kbase1 );
        assertEquals( kbase1, dirtyLog.getAddedKBases().get( kbase1.getQName() ) );

        kproj.removeKBase( kbase1 );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
    }

    @Test
    public void testKBaseExistsRemovedAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        dirtyLog.reset();

        kproj.removeKBase( kbase1 );
        assertEquals( kbase1, dirtyLog.getRemovedKBases().get( kbase1.getQName() ) );        

        kproj.addKBase( kbase1 );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
    }
    
    @Test
    public void testKBaseExistsModifyRemovedAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        dirtyLog.reset();
        
        kproj.removeKBase( kbase1 );
        kbase1.setNamespace( "org.test2" );
        kproj.addKBase( kbase1 );   
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        
        kproj.removeKBase( kbase1 );
        assertEquals( kbase1, dirtyLog.getRemovedKBases().get( kbase1.getQName() ) ); 
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );

        kproj.addKBase( kbase1 );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
    }    
    
    @Test
    public void testKBaseDoesntExistsModify() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        kproj.removeKBase( kbase1 );
        kbase1.setNamespace( "org.test2" );
        kproj.addKBase( kbase1 );   
        
        assertFalse( dirtyLog.getModifiedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertTrue( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
    } 
    
    @Test
    public void testKBaseExistsQNameModified() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        dirtyLog.reset();

        kproj.removeKBase( kbase1 );
        kbase1.setNamespace( "org.test2" );
        kproj.addKBase( kbase1 );

        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );

        kproj.removeKBase( kbase1 );
        kbase1.setName( "KBase2" );
        kproj.addKBase( kbase1 );

        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );
    }    
    
    @Test
    public void testKBaseExistsModifyRemovedAddedWithOverlappingNames() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KBase kbase2 = new KBase( "org.test1", "KBase2" );
        kproj.addKBase( kbase2 );        
        
        dirtyLog.reset();
        
        kproj.removeKBase( kbase1 );
        kbase1.setNamespace( "org.test2" );
        kproj.addKBase( kbase1 );   
        
        kproj.removeKBase( kbase2 );
        kbase2.setName( "KBase1" );
        kproj.addKBase( kbase2 );        
        
        
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );
        assertEquals( "org.test1.KBase2", dirtyLog.getModifiedKBases().get( kbase2.getQName() ) );
        
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase2.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase2.getQName() ) );
        
        
        kproj.removeKBase( kbase1 );
        assertEquals( kbase1, dirtyLog.getRemovedKBases().get( kbase1.getQName() ) ); 
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );
        
        
        kproj.removeKBase( kbase2 );
        assertEquals( kbase2, dirtyLog.getRemovedKBases().get( kbase2.getQName() ) ); 
        assertEquals( "org.test1.KBase2", dirtyLog.getModifiedKBases().get( kbase2.getQName() ) );        

        kproj.addKBase( kbase1 );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        
        kproj.addKBase( kbase2 );
        assertFalse( dirtyLog.getRemovedKBases().containsKey( kbase2.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase2.getQName() ) );        
    }          

    @Test
    public void testKBaseModified() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kbase1.setEqualsBehavior( AssertBehaviorOption.IDENTITY );
        kbase1.setEventProcessingMode( EventProcessingOption.CLOUD );

        kproj.addKBase( kbase1 );

        kbase1.setEqualsBehavior( AssertBehaviorOption.EQUALITY );
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );

        dirtyLog.reset();

        kbase1.setEventProcessingMode( EventProcessingOption.STREAM );
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );

        dirtyLog.reset();

        kbase1.setFiles( new ArrayList<String>() );
        assertEquals( "org.test1.KBase1", dirtyLog.getModifiedKBases().get( kbase1.getQName() ) );
        
        kproj.removeKBase( kbase1 );       
        assertTrue( dirtyLog.getModifiedKBases().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().containsKey( kbase1.getQName() ) );
        assertTrue( dirtyLog.getRemovedKBases().containsKey( kbase1.getQName() ) );        
    }

    @Test
    public void testKSessionAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KSession kSession = new KSession( "org.domain", "KSession1" );
        kbase1.addKSession( kSession );

        assertEquals( kSession, dirtyLog.getAddedKSessions().get( kSession.getQName() ) );
    }

    @Test
    public void testKSessionRemoved() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KSession kSession = new KSession( "org.domain", "KSession1" );
        
        kbase1.addKSession( kSession );
        assertEquals( kSession, dirtyLog.getAddedKSessions().get( kSession.getQName() ) );
        
        kbase1.removeKSession( kSession );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );        
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
    }
    
    @Test
    public void testKSessionExistsRemovedAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        KSession kSession = new KSession( "org.domain", "KSession1" );        
        kbase1.addKSession( kSession );        
        
        dirtyLog.reset();

        kbase1.removeKSession( kSession );
        assertEquals( kSession, dirtyLog.getRemovedKSessions().get( kSession.getQName() ) );        

        kbase1.addKSession( kSession ); 
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kbase1.getQName() ) );
    }    
    
    
    @Test
    public void testKSessionExistsModifyRemovedAdded() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        
        KSession kSession = new KSession( "org.test1", "KSession1" );        
        kbase1.addKSession( kSession );  
        
        dirtyLog.reset();
        
        kbase1.removeKSession( kSession );
        kSession.setNamespace( "org.test2" );
        kbase1.addKSession( kSession );   
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
        
        kbase1.removeKSession( kSession );
        assertEquals( kSession, dirtyLog.getRemovedKSessions().get( kSession.getQName() ) ); 
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );

        kbase1.addKSession( kSession ); 
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
    }      
    
    @Test
    public void testKSessionDoesntExistsModify() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );
        
        KSession kSession = new KSession( "org.test1", "KSession1" );        
        kbase1.addKSession( kSession );          
        
        kbase1.removeKSession( kSession );
        kSession.setNamespace( "org.test2" );
        kbase1.addKSession( kSession );    
        
        assertFalse( dirtyLog.getModifiedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
        assertTrue( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
    }     

    @Test
    public void testKSessioExistsnQNameModified() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KSession kSession = new KSession( "org.test1", "KSession1" );
        kbase1.addKSession( kSession );

        dirtyLog.reset();        

        kbase1.removeKSession( kSession );
        kSession.setNamespace( "org.test2" );
        kbase1.addKSession( kSession );

        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );

        kbase1.removeKSession( kSession );
        kSession.setName( "KSession2" );
        kbase1.addKSession( kSession );

        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );
    }
    
    @Test
    public void testKsessionExistsModifyRemovedAddedWithOverlappingNames() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KSession kSession1 = new KSession( "org.test1", "KSession1" );
        kbase1.addKSession( kSession1 );       
        
        KSession kSession2 = new KSession( "org.test1", "KSession2" );
        kbase1.addKSession( kSession2 );            
        
        dirtyLog.reset();
        
        kbase1.removeKSession( kSession1 );
        kSession1.setNamespace( "org.test2" );
        kbase1.addKSession( kSession1 );
        
        kbase1.removeKSession( kSession2 );
        kSession2.setName( "KSession1" );
        kbase1.addKSession( kSession2 );       
        
        
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession1.getQName() ) );
        assertEquals( "org.test1.KSession2", dirtyLog.getModifiedKSessions().get( kSession2.getQName() ) );
        
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession1.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession1.getQName() ) );
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession2.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession2.getQName() ) );
        
        kbase1.removeKSession( kSession1 );
        assertEquals( kSession1, dirtyLog.getRemovedKSessions().get( kSession1.getQName() ) );
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession1.getQName() ) );

        kbase1.removeKSession( kSession2 );
        assertEquals( kSession2, dirtyLog.getRemovedKSessions().get( kSession2.getQName() ) );
        assertEquals( "org.test1.KSession2", dirtyLog.getModifiedKSessions().get( kSession2.getQName() ) );        
        
        kbase1.addKSession( kSession1 );    
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession1.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession1.getQName() ) );        
        
        kbase1.addKSession( kSession2 );    
        assertFalse( dirtyLog.getRemovedKSessions().containsKey( kSession2.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession2.getQName() ) );                  
    }          
    

    @Test
    public void testKSessionModified() {
        KProject kproj = new KProject();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        KBase kbase1 = new KBase( "org.test1", "KBase1" );
        kproj.addKBase( kbase1 );

        KSession kSession = new KSession( "org.test1", "KSession1" );
        kSession.setClockType( ClockTypeOption.get( "pseudo" ) );
        kSession.setType( "stateless" );

        kbase1.addKSession( kSession );

        kSession.setClockType( ClockTypeOption.get( "realtime" ) );
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );
        assertTrue( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );

        dirtyLog.reset();

        kSession.setType( "stateful" );
        assertEquals( "org.test1.KSession1", dirtyLog.getModifiedKSessions().get( kSession.getQName() ) );
        
        kbase1.removeKSession( kSession );       
        assertTrue( dirtyLog.getModifiedKSessions().containsKey( kSession.getQName() ) );
        assertFalse( dirtyLog.getAddedKSessions().containsKey( kSession.getQName() ) );
        assertTrue( dirtyLog.getRemovedKSessions().containsKey( kSession.getQName() ) );
    }
    

}
