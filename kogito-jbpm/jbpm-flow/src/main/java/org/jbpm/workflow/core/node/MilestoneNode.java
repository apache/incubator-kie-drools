/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.node;

import java.util.function.Predicate;

import org.kie.api.definition.process.Connection;
import org.kie.api.runtime.process.ProcessContext;

import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;

/**
 * Default implementation of a milestone node.
 */
public class MilestoneNode extends StateBasedNode implements Constrainable {

    private static final long serialVersionUID = 510L;

    /**
     * String representation of the conditionPredicate. Not used at runtime
     */
    private String condition;
    private Predicate<ProcessContext> conditionPredicate;

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(Predicate<ProcessContext> conditionPredicate) {
        this.conditionPredicate = conditionPredicate;
    }

    public boolean canComplete(ProcessContext context) {
        return conditionPredicate == null || conditionPredicate.test(context);
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throwValidationException(connection, "only accepts default incoming connection type!");
        }
        if (getFrom() != null && !Boolean.parseBoolean(System.getProperty("jbpm.enable.multi.con"))) {
            throwValidationException(connection, "cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throwValidationException(connection, "only accepts default outgoing connection type!");
        }
        if (getTo() != null && !Boolean.parseBoolean(System.getProperty("jbpm.enable.multi.con"))) {
            throwValidationException(connection, "cannot have more than one outgoing connection!");
        }
    }

    private static void throwValidationException(Connection connection, String msg) {
        throw new IllegalArgumentException("This type of node ["
                + connection.getFrom().getMetaData().get(UNIQUE_ID) + ", "
                + connection.getFrom().getName() + "] "
                + msg);
    }

}
