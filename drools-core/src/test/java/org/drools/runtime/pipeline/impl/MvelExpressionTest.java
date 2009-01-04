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

public class MvelExpressionTest extends TestCase {
    public void testExpression() {
        MockClass mock = new MockClass();
               
        Callable callable = PipelineFactory.newCallable();
        Action action = PipelineFactory.newMvelAction( "this.setValues( [0, 1, 2, 3, 4] ) " );
        callable.addReceiver( action );
        Expression expr = PipelineFactory.newMvelExpression( "this.values" );
        action.addReceiver( expr );
        expr.addReceiver( callable );          
        
        assertNull( mock.getValues() );
        List<Integer> list = ( List<Integer> ) callable.call( mock, new BasePipelineContext( Thread.currentThread().getContextClassLoader() ) );

        
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
