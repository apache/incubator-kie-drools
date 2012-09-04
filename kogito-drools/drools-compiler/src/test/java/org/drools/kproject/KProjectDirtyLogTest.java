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
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        kproj.setKProjectPath( "src/main/resources/" );
        kproj.setKBasesPath( "src/kbases" );

        assertTrue( dirtyLog.isKProjectDirty() );
    }

    @Test
    public void testKBaseAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1  = kproj.newKBase(  "org.test1", "KBase1"  );
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );
    }

    @Test
    public void testKBaseRemoved() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );
        
        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );

        kproj.removeKBase( kbase1.getQName() );
        assertTrue( dirtyLog.getRemovedKBases().contains( kbase1.getQName() ) );
        assertFalse( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );
    }

    @Test
    public void testKBaseExistsRemovedAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        
        dirtyLog.reset();

        kproj.removeKBase( kbase1.getQName() );
        assertTrue( dirtyLog.getRemovedKBases().contains( kbase1.getQName() ) );        

        kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        assertFalse( dirtyLog.getRemovedKBases().contains( kbase1.getQName() ) );
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );
    }
    
    @Test
    public void testKBaseExistsModifyRemovedAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        
        dirtyLog.reset();
        
        kbase1.setNamespace( "org.test2" );  
        assertEquals( 1, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) );
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase1" ) );        
        
        kproj.removeKBase( kbase1.getQName() );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1") ); 
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test2.KBase1") );
        assertEquals( 0, dirtyLog.getAddedKBases().size() );
    }    
    
    @Test
    public void testKBaseDoesntExistsModify() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase(  "org.test1", "KBase1" );
        
        kproj.removeKBase( kbase1.getQName() );
                
        kbase1 = kproj.newKBase(  "org.test2", "KBase1" );
        
        assertEquals( 1, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) );
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase1" ) );
    } 
    
    @Test
    public void testKBaseExistsQNameModified() {
        KProjectImpl kproj = ( KProjectImpl ) new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        dirtyLog.reset();

        kbase1.setNamespace( "org.test2" );

        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) );
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase1" ) );
        
        kbase1.setName( "KBase2" );

        assertEquals( 2, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test2.KBase1" ) );
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase2" ) );
    }    
    
    @Test
    public void testKBaseExistsModifyRemovedAddedWithOverlappingNames() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        KBase kbase2 = kproj.newKBase( "org.test1", "KBase2" );       
        
        dirtyLog.reset();
        
        kbase1.setNamespace( "org.test2" );          
        kbase2.setName( "KBase1" );     
        
        assertEquals( 1, dirtyLog.getRemovedKBases().size() );
        assertEquals( 2, dirtyLog.getAddedKBases().size() );
        

        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase1" ) );
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test1.KBase1" ) );        
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase2" ) );
      
        kproj.removeKBase( kbase1.getQName() );
        
        assertEquals( 2, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );
        
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test2.KBase1" ) );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase2" ) );
        
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test1.KBase1" ) ); 
       
        kproj.removeKBase( kbase2.getQName() );
        assertEquals( 3, dirtyLog.getRemovedKBases().size() );
        assertEquals( 0, dirtyLog.getAddedKBases().size() );
        
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test2.KBase1" ) );
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) ); 
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase2" ) );
              
        kproj.newKBase( kbase1.getNamespace(), kbase1.getName() );
        assertEquals( 2, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );
        
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase1" ) ); 
        assertTrue( dirtyLog.getRemovedKBases().contains( "org.test1.KBase2" ) );      
        assertTrue( dirtyLog.getAddedKBases().contains( "org.test2.KBase1" ) );               
    }          

    @Test
    public void testKBaseModified() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        kbase1.setEqualsBehavior( AssertBehaviorOption.IDENTITY );
        kbase1.setEventProcessingMode( EventProcessingOption.CLOUD );

        kbase1.setEqualsBehavior( AssertBehaviorOption.EQUALITY );
 
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );

        dirtyLog.reset();

        kbase1.setEventProcessingMode( EventProcessingOption.STREAM );
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );

        dirtyLog.reset();

        kbase1.setFiles( new ArrayList<String>() );
        assertTrue( dirtyLog.getAddedKBases().contains( kbase1.getQName() ) );
        
        kproj.removeKBase( kbase1.getQName() );
        assertEquals( 1, dirtyLog.getRemovedKBases().size() );
        assertEquals( 0, dirtyLog.getAddedKBases().size() );        
        assertTrue( dirtyLog.getRemovedKBases().contains( kbase1.getQName() ) );       
    }

    @Test
    public void testKSessionAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );

        KSession kSession = kbase1.newKSession( "org.domain", "KSession1" );

        assertTrue( dirtyLog.getAddedKSessions().contains( kSession.getQName() ) );
    }

    @Test
    public void testKSessionRemoved() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );

        KSession kSession = kbase1.newKSession( "org.domain", "KSession1" );
        
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.domain.KSession1" ) );
        
        kbase1.removeKSession( kSession.getQName() );
        assertEquals( 0, dirtyLog.getRemovedKBases().size() );
        assertEquals( 1, dirtyLog.getAddedKBases().size() );        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.domain.KSession1" ) );        
        assertFalse( dirtyLog.getAddedKSessions().contains( "org.domain.KSession1" ) );
    }
    
    @Test
    public void testKSessionExistsRemovedAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        
        KSession kSession = kbase1.newKSession( "org.domain", "KSession1" );                
        
        dirtyLog.reset();

        kbase1.removeKSession( kSession.getQName() );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.domain.KSession1" ) );                
        
        kbase1.newKSession( kSession.getNamespace(), kSession.getName() ); 
        
        assertEquals( 0, dirtyLog.getRemovedKSessions().size() );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.domain.KSession1" ) );
    }    
    
    
    @Test
    public void testKSessionExistsModifyRemovedAdded() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );        
        
        KSession kSession = kbase1.newKSession( "org.test1", "KSession1" );        
        
        dirtyLog.reset();
        
        kSession.setNamespace( "org.test2" );
     
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1") );
        
        kbase1.removeKSession( kSession.getQName() );
        assertEquals( 2, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 0, dirtyLog.getAddedKSessions().size() );           
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test2.KSession1" ) ); 

        kbase1.newKSession( kSession.getNamespace(), kSession.getName() ); 
        
        assertEquals( 1, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 1, dirtyLog.getAddedKSessions().size() );  
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1" ) );
    }      
    
    @Test
    public void testKSessionDoesntExistsModify() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );
        
        KSession kSession = kbase1.newKSession( "org.test1", "KSession1" );        
        kSession.setNamespace( "org.test2" );

        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1") );
    }     

    @Test
    public void testKSessioExistsnQNameModified() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );

        KSession kSession = kbase1.newKSession( "org.test1", "KSession1" );

        dirtyLog.reset();        

        kSession.setNamespace( "org.test2" );

        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1" ) );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1") );               

        kSession.setName( "KSession2" );

        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test2.KSession1" ) );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1") );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession2" ) );
    }
    
    @Test
    public void testKsessionExistsModifyRemovedAddedWithOverlappingNames() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );

        KSession kSession1 = kbase1.newKSession( "org.test1", "KSession1" );       
        
        KSession kSession2 = kbase1.newKSession( "org.test1", "KSession2" );            
        
        dirtyLog.reset();
        
        kSession1.setNamespace( "org.test2" );
        
        kSession2.setName( "KSession1" );       
        
        assertEquals( 1, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 2, dirtyLog.getAddedKSessions().size() ); 
        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession2") );        
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1" ) );
        
        kbase1.removeKSession( kSession1.getQName() );
        assertEquals( 2, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 1, dirtyLog.getAddedKSessions().size() ); 
        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession2") );        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test2.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1" ) );

        kbase1.removeKSession( kSession2.getQName() );
        assertEquals( 3, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 0, dirtyLog.getAddedKSessions().size() ); 
        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession2") ); 
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test2.KSession1" ) );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );      
        
        kbase1.newKSession( kSession1.getNamespace(), kSession1.getName() );    
        assertEquals( 2, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 1, dirtyLog.getAddedKSessions().size() ); 
        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession2") );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1" ) );
               
        
        kbase1.newKSession( kSession2.getNamespace(), kSession2.getName() );
        assertEquals( 1, dirtyLog.getRemovedKSessions().size() );
        assertEquals( 2, dirtyLog.getAddedKSessions().size() ); 
        
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession2") );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test2.KSession1" ) );                 
    }          
    

    @Test
    public void testKSessionModified() {
        KProject kproj = new KProjectImpl();

        KProjectChangeLog dirtyLog = new KProjectChangeLog();
        kproj.setListener( dirtyLog );

        assertFalse( dirtyLog.isKProjectDirty() );

        KBase kbase1 = kproj.newKBase( "org.test1", "KBase1" );

        KSession kSession = kbase1.newKSession( "org.test1", "KSession1" );
        kSession.setClockType( ClockTypeOption.get( "pseudo" ) );
        kSession.setType( "stateless" );

        kSession.setClockType( ClockTypeOption.get( "realtime" ) );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1"  ) );

        dirtyLog.reset();

        kSession.setType( "stateful" );
        assertTrue( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1"  ) );
        
        kbase1.removeKSession( kSession.getQName() );               
        assertFalse( dirtyLog.getAddedKSessions().contains( "org.test1.KSession1") );
        assertTrue( dirtyLog.getRemovedKSessions().contains( "org.test1.KSession1" ) );
    }
    

}
