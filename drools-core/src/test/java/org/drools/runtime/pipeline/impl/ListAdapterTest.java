package org.drools.runtime.pipeline.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.ListAdapter;
import org.drools.runtime.pipeline.PipelineFactory;

import junit.framework.TestCase;

public class ListAdapterTest extends TestCase {
    public void testListAdapter() {
        MockClass mock = new MockClass();
        List list = new ArrayList();
        
        Action action = PipelineFactory.newMvelAction( "this.set = true" );   
        
        ListAdapter listAdapter = new ListAdapterImpl( list, true );        
        action.setReceiver( listAdapter );
        
        assertFalse( mock.isSet() );
        action.receive( mock, new BasePipelineContext( Thread.currentThread().getContextClassLoader() ) );

        assertEquals( 1, list.size());
        assertSame( mock, list.get( 0 ) );
        assertTrue( mock.isSet() );
    }
    
    public static class MockClass {        
        private boolean set;

        public boolean isSet() {
            return set;
        }

        public void setSet(boolean set) {
            this.set = set;
        }
                
    }
}
