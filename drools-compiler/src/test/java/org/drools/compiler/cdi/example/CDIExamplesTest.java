/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.cdi.example;


import static org.junit.Assert.assertEquals;
import javax.inject.Inject;

import org.drools.compiler.cdi.CDITestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CDITestRunner.class)
public class CDIExamplesTest  {
    
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
    
    @BeforeClass
    public static void beforeClass() {    
        CDITestRunner.setUp();  
        
        CDITestRunner.weld = CDITestRunner.createWeld( CDIExamplesTest.class.getName(),
                                                       Msg.class.getName(), Msg1.class.getName(), Msg2.class.getName(), 
                                                       Message.class.getName(), MessageImpl.class.getName(), 
                                                       Message2.class.getName(), Message2Impl1.class.getName(), Message2Impl2.class.getName(),
                                                       MessageProducers.class.getName(), MessageProducers2.class.getName() );
        CDITestRunner.container = CDITestRunner.weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        CDITestRunner.tearDown();
    }          
    
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
