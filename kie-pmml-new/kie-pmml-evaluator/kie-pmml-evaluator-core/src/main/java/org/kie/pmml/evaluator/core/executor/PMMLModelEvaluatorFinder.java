package org.kie.pmml.evaluator.core.executor;

import java.util.List;

/**
 * Actual implementation is required to retrieve a
 * <code>List&lt;PMMLModelEvaluator&gt;</code> out from the classes found in the classpath
 */
public interface PMMLModelEvaluatorFinder {

    /**
     * Retrieve all the <code>PMMLModelExecutor</code> implementations in the classpath
     * @param refresh pass <code>true</code> to reload classes from classpath; <code>false</code> to use cached ones
     * @return
     */
    List<PMMLModelEvaluator> getImplementations(boolean refresh);
}