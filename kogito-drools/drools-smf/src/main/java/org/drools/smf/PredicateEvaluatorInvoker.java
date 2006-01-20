package org.drools.smf;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

/** The interface which provides the glue to the predicate eval concrete implementation */
public interface PredicateEvaluatorInvoker
    extends
    Invoker {

    public boolean invoke(Tuple tuple,
                          Declaration[] decls,
                          Map applicationData) throws Exception;    
    
}
