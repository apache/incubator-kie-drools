package org.drools.smf;

import java.net.URL;

import junit.framework.TestCase;

/**
 * Test out the semantics reader with some mocks.
 * @author Michael Neale
 */
public class SemanticsReaderTest extends TestCase {
    
    public void testLoadMockSemanticConfig() throws Exception {
        SemanticsReader reader = SemanticsReader.getInstance();
        
        URL url = this.getClass().getResource("/org/drools/smf/example.conf");
        
        SemanticModule module = reader.read(url);
        assertNotNull(module);
        
        FunctionsFactory factory = (FunctionsFactory) module.getFunctionsFactory("functions");
        assertNotNull(factory);
        
        ReturnValueEvaluatorFactory retFactory = (ReturnValueEvaluatorFactory) module.getReturnValueEvaluatorFactory("retval");
        assertNotNull(retFactory);
        
        PredicateEvaluatorFactory predicateFactory = (PredicateEvaluatorFactory) module.getPredicateEvaluatorFactory("predicate");
        assertNotNull(predicateFactory);
        
        
    }
    
    
}
