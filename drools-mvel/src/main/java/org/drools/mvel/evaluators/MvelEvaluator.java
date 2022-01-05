/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.evaluators;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.spi.Evaluator;

public interface MvelEvaluator extends Evaluator {

    /**
     * Evaluates the expression using the provided parameters.
     *
     * This method is used when evaluating left-activated
     * beta-constraints, i.e., a fact attribute against a variable
     * value, that is activated from the left.
     *
     * For instance:
     *
     * Person( name == $someName )
     *
     * This method will be used when a new $someName variable is
     * bound. So it will cache the value of $someName and will
     * iterate over the right memory (Person instances) evaluating
     * each occurrence.
     *
     * @param reteEvaluator
     *        The current working memory
     * @param context
     *        The previously cached context, including the left value
     *        and the extractor for the right value.
     * @param right
     *        The right object, from where to extract the value. In the
     *        above example, that is the "Person" instance from where to
     *        extract the "name" attribute.
     *
     * @return Returns true if evaluation is successful. false otherwise.
     */
    boolean evaluateCachedLeft(ReteEvaluator reteEvaluator,
                               VariableRestriction.VariableContextEntry context,
                               InternalFactHandle right);

    /**
     * Evaluates the expression using the provided parameters.
     *
     * This method is used when evaluating right-activated
     * beta-constraints, i.e., a fact attribute against a variable
     * value, that is activated from the right.
     *
     * For instance:
     *
     * Person( name == $someName )
     *
     * This method will be used when a new Person instance is evaluated.
     * So it will cache the value of the "Person" instance and will
     * iterate over the left memory comparing it to each "$someName" bound
     * values.
     *
     * @param reteEvaluator
     *        The current working memory
     * @param context
     *        The previously cached context, including the right value
     *        and the extractor for the left value.
     * @param left
     *        The left object, from where to extract the bound variable.
     *        In the above example, that is the "$someName" variable value.
     *
     * @return Returns true if evaluation is successful. false otherwise.
     */
    boolean evaluateCachedRight(ReteEvaluator reteEvaluator,
                                VariableRestriction.VariableContextEntry context,
                                InternalFactHandle left);
}
