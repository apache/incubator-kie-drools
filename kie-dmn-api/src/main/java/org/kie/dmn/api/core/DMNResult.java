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
 * An instance that encapsulates all the information
 * resulting from a DMN service invocation
 */
public interface DMNResult {

    /**
     * Returns the full context, including
     * all input variables and additional
     * variables and decisions set during
     * the service invocation
     *
     * @return the resulting DMN context
     */
    DMNContext getContext();

    /**
     * Returns a list containing all the results
     * of the decisions executed
     *
     * @return A list with the result of the
     *         decisions
     */
    List<DMNDecisionResult> getDecisionResults();

    /**
     * Returns the result of a single decision.
     *
     * @param name the name of the decision
     *
     * @return the result of the decision
     *         or null if the decision was not
     *         evaluated.
     */
    DMNDecisionResult getDecisionResultByName( String name );

    /**
     * Returns the result of a single decision.
     *
     * @param id the id of the decision
     *
     * @return the result of the decision
     *         or null if the decision was not
     *         evaluated.
     */
    DMNDecisionResult getDecisionResultById( String id );

    /**
     * Returns a list of all the messages produced
     * during the DMN service invocation.
     *
     * @return list of messages
     */
    List<DMNMessage> getMessages();

    /**
     * Returns a list of all the messages produced
     * during the DMN service invocation, filtered
     * by the list of severities given.
     *
     * @param sevs the list of severities to filter
     *             the messages by
     *
     * @return filtered list of messages
     */
    List<DMNMessage> getMessages(DMNMessage.Severity... sevs);

    /**
     * A helper method to quick check for the presence
     * of error messages. The actual error messages can
     * be retrieved by invoking <code>#getMessages()</code>
     *
     * @return true if there are any error messages,
     *         false otherwise.
     */
    boolean hasErrors();

}
