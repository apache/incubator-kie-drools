/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.api.core;

import java.util.List;

/**
 * Stores the result of the evaluation of a decision
 *
 */
public interface DMNDecisionResult {

    enum DecisionEvaluationStatus {
        NOT_EVALUATED, SUCCEEDED, SKIPPED, FAILED;
    }

    /**
     * Returns the decision ID
     *
     * @return the decision ID
     */
    String getDecisionId();

    /**
     * Returns the decision name
     *
     * @return the decision name
     */
    String getDecisionName();

    /**
     * Returns the evaluation status
     * of this decision.
     *
     * @return SUCCEEDED if the evaluation completed
     *         without errors.
     */
    DecisionEvaluationStatus getEvaluationStatus();

    /**
     * Returns the result of the evaluation
     * of the decision
     *
     * @return the result of the decision
     */
    Object getResult();

    /**
     * Returns a list of DMN messages generated
     * during the evaluation of this decision.
     *
     * @return a list of messages, or an empty list if
     *         no message was generated
     */
    List<DMNMessage> getMessages();

    /**
     * Returns true if any error occurred during evaluation.
     *
     * @return true if any error ocurred during evaluation.
     */
    boolean hasErrors();

}
