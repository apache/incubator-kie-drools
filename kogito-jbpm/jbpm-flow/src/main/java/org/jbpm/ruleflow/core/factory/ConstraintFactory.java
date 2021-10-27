/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.core.factory;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;

public class ConstraintFactory<T extends SplitFactory<?>> {
    private T parent;
    private ConstraintImpl constraintImpl;

    public ConstraintFactory(T parent, long toNodeId, String name, String type, String dialect, String constraint) {
        this.parent = parent;
        constraintImpl = new ConstraintImpl();
        constraintImpl.setName(name);
        constraintImpl.setType(type);
        constraintImpl.setDialect(dialect);
        constraintImpl.setConstraint(constraint);
        parent.getSplit().addConstraint(
                new ConnectionRef(name, toNodeId, Node.CONNECTION_DEFAULT_TYPE), constraintImpl);
    }

    public ConstraintFactory<T> priority(int priority) {
        constraintImpl.setPriority(priority);
        return this;
    }

    public ConstraintFactory<T> withDefault(boolean def) {
        constraintImpl.setDefault(def);
        return this;
    }

    public ConstraintFactory<T> metadata(String name, Object value) {
        constraintImpl.setMetaData(name, value);
        return this;
    }

    T done() {
        return parent;
    }

}
