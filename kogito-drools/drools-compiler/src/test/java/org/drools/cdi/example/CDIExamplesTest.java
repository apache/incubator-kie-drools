package org.drools.cdi.example;


import static org.junit.Assert.assertEquals;
import javax.inject.Inject;

import org.drools.cdi.CDITestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CDITestRunner.class)
public class CDIExamplesTest {
    
    @Inject
    private Message defaultMsg;    
    
    @Inject @Msg1
    private Message2 m1;
    
    @Inject @Msg2
    private Message2 m2;
    
    @Inject @Msg1
    private String msg1;
    
    @Inject @Msg2
    private String msg2;

    
    @Inject @Msg("named1")
    private String msgNamed1;
    
    @Inject @Msg("named2")
    private String msgNamed2;

    @Inject @Msg("chained1")
    private String msgChained1;

    
    @Inject @Msg("chained2")
    private String msgChained2;    
    @Test
    public void testDefaultInjection() {
        assertEquals( "default.msg", defaultMsg.getText() );        
    }
    
    @Test
    public void testSimpleQualifiedInjection() {        
        assertEquals( "msg.1", msg1 );
        
        assertEquals( "msg.2", msg2 );
    }
    
    @Test
    public void testQualiferWithValueInjection() {        
        assertEquals( "msg.named1", msgNamed1 );
        assertEquals( "msg.named2", msgNamed2 );
    }    
    
    @Test
    public void testChained1Injection() {        
        assertEquals( "chained.1 msg.1", msgChained1 );
    }
    
    @Test
    public void testChained2Injection() {        
        assertEquals( "chained.2 default.msg msg.1 msg.named1", msgChained2 );
    }     
    
    @Test
    public void testNoProducers() {
        assertEquals( "msg2 - 1", m1.getText() );
        assertEquals( "msg2 - 2", m2.getText() );
    }
       
}
