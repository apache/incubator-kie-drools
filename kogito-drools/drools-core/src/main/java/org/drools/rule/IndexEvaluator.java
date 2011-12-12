package org.drools.rule;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.InternalReadAccessor;

import java.io.Serializable;

public interface IndexEvaluator extends Serializable {
    /**
     * Evaluates the expression using the provided parameters.
     *
     * This method is used for internal indexing and hashing,
     * when drools needs to extract and evaluate both left and
     * right values at once.
     *
     * For instance:
     *
     * Person( name == $someName )
     *
     * This method will be used to extract and evaluate both
     * the "name" attribute and the "$someName" variable at once.
     *
     * @param workingMemory
     *        The current working memory
     * @param leftExtractor
     *        The extractor to read the left value. In the above example,
     *        the "$someName" variable value.
     * @param left
     *        The source object from where the value of the variable is
     *        extracted.
     * @param rightExtractor
     *        The extractor to read the right value. In the above example,
     *        the "name" attribute value.
     * @param right
     *        The right object from where to extract the value. In the
     *        above example, that is the "Person" instance from where to
     *        extract the "name" attribute.
     *
     * @return Returns true if evaluation is successful. false otherwise.
     */
    public boolean evaluate(InternalWorkingMemory workingMemory,
                            InternalReadAccessor leftExtractor,
                            Object left,
                            InternalReadAccessor rightExtractor,
                            Object right);
}
