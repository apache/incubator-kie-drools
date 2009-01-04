package org.drools.runtime.pipeline.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.Callable;
import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.ListAdapter;
import org.drools.runtime.pipeline.PipelineFactory;
import org.drools.runtime.pipeline.Splitter;
import org.mvel2.MVEL;
import org.mvel2.optimizers.OptimizerFactory;

import junit.framework.TestCase;

public class IterateSplitterTest extends TestCase {
    public void testSplitter() {
        MockClass mock = new MockClass();
        List<Integer> list = new ArrayList<Integer>();
        
        Action action = PipelineFactory.newMvelAction( "this.setValues( [0, 1, 2, 3, 4] ) " );  
        Expression expr = PipelineFactory.newMvelExpression( "this.values" );
        action.addReceiver( expr );
                
        Splitter splitter = PipelineFactory.newIterateSplitter();
        expr.addReceiver( splitter );
        
        ListAdapter listAdapter = PipelineFactory.newListAdapter( list, true );        
        splitter.addReceiver( listAdapter );
        
        assertNull( mock.getValues() );
        action.receive( mock, new BasePipelineContext( Thread.currentThread().getContextClassLoader() ) );

        
        System.out.println( list.get( 0 ));
        assertEquals( 5, list.size());
        assertEquals( 0,list.get( 0 ).intValue() );
        assertEquals( 4,list.get( 4 ).intValue() );
    }
    
    public static class MockClass {        
        private List values;

        public List getValues() {
            return values;
        }

        public void setValues(List values) {
            this.values = values;
        }                              
    }
}
