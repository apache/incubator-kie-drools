/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.EventSubProcessNodeFactory;

import java.util.Map;

public class EventSubprocessNodeVisitor extends CompositeContextNodeVisitor {

    private static final String FACTORY_METHOD_NAME = "eventSubProcessNode";

    public EventSubprocessNodeVisitor(Map<Class<?>, AbstractNodeVisitor> nodesVisitors) {
        super(nodesVisitors);
    }

    @Override
    protected Class<? extends CompositeContextNodeFactory> factoryClass() {
        return EventSubProcessNodeFactory.class;
    }

    @Override
    protected String factoryMethod() {
        return FACTORY_METHOD_NAME;
    }

    @Override
    protected String getDefaultName() {
        return "Event Subprocess";
    }
}
