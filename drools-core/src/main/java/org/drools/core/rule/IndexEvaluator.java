/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.InternalReadAccessor;

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
    boolean evaluate(InternalWorkingMemory workingMemory,
                     InternalReadAccessor leftExtractor,
                     Object left,
                     InternalReadAccessor rightExtractor,
                     Object right);

    boolean evaluate(InternalWorkingMemory workingMemory,
                     Object value1,
                     InternalReadAccessor extractor2,
                     Object object2);
}
