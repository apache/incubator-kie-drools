package org.drools.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.spi.Evaluator;

import junit.framework.TestCase;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 * @author Michael Neale
 */
public class EvaluatorFactoryTestCase extends TestCase {

    public void testFactoryMethod() {
        assertNotNull(EvaluatorFactory.getInstance());
    }
    
    public void testObject() {
        
        assertFalse(EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE, "==" )
                    .evaluate( "foo", "bar" ));
        String foo = "foo";
        assertTrue(EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE, "==" )
                    .evaluate( foo, foo ));
        assertTrue(EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE, "!=" )
                    .evaluate( "foo", "bar" ));
        
        List list = new ArrayList();
        list.add( "foo" );
         
        assertTrue(EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE, "contains" )
                   .evaluate( "foo", list ));
        assertFalse(EvaluatorFactory.getEvaluator( Evaluator.OBJECT_TYPE, "contains" )
                   .evaluate( "bar", list ));
        
        
    }
    
}
