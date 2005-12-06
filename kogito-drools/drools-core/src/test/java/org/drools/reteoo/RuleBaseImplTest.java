package org.drools.reteoo;

import org.drools.DroolsTestCase;
import org.drools.WorkingMemory;

public class RuleBaseImplTest extends DroolsTestCase
{
    RuleBaseImpl ruleBase;
    
    WorkingMemory wm1;
    WorkingMemory wm2;
    WorkingMemory wm3;
    WorkingMemory wm4;
    
    public void setUp()
    {
        this.ruleBase = new RuleBaseImpl( new Rete() );
        
        this.wm1 = ruleBase.newWorkingMemory();
        this.wm2 = ruleBase.newWorkingMemory();
        this.wm3 = ruleBase.newWorkingMemory();
        this.wm4 = ruleBase.newWorkingMemory();        
    }
    
    public void testKeepReference() throws Exception
    {
        /* Make sure the RuleBase is referencing all 4 Working Memories */
        assertLength( 4,
                      ruleBase.getWorkingMemories() );        
        assertTrue( ruleBase.getWorkingMemories().contains( wm1 ) );
        assertTrue( ruleBase.getWorkingMemories().contains( wm2 ) );
        assertTrue( ruleBase.getWorkingMemories().contains( wm3 ) );
        assertTrue( ruleBase.getWorkingMemories().contains( wm4 ) );          
    }
    
    public void testWeakReference() throws Exception
    {                      
        /* nulling these two so the keys should get garbage collected */
        wm2 = null;        
        wm4 = null;
        
        /* Run GC */
        System.gc();        
        Thread.sleep( 200 ); //Shouldn't need to sleep, but put it in anyway        
        
        /* Check we now only have two keys */
        assertLength( 2,
                      ruleBase.getWorkingMemories() );
        
        /* Make sure the correct keys were removed */
        assertTrue( ruleBase.getWorkingMemories().contains( wm1 ) );
        assertFalse( ruleBase.getWorkingMemories().contains( wm2 ) );
        assertTrue( ruleBase.getWorkingMemories().contains( wm3 ) );
        assertFalse( ruleBase.getWorkingMemories().contains( wm4 ) );
               
    }
    
    public void testDispose() throws Exception
    {
        /* Now lets test the dispose method on the WorkingMemory itself.
         * dispose doesn't need GC */
        wm3.dispose();                
                
        /* Check only wm3 was removed */
        assertLength( 3,
                      ruleBase.getWorkingMemories() );        
        assertFalse( ruleBase.getWorkingMemories().contains( wm3 ) );          
    }
    
    public void testNoKeepReference() throws Exception
    {
        WorkingMemory wm5 = this.ruleBase.newWorkingMemory( false );
        WorkingMemory wm6 = this.ruleBase.newWorkingMemory( false );
        assertLength( 4,
                      ruleBase.getWorkingMemories() );          
        assertFalse( ruleBase.getWorkingMemories().contains( wm5 ) );
        assertFalse( ruleBase.getWorkingMemories().contains( wm6 ) );
    }    
    

}
