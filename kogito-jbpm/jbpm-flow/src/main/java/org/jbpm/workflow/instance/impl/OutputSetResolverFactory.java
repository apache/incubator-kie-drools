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
package org.jbpm.workflow.instance.impl;

import java.util.Map;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class OutputSetResolverFactory extends ImmutableDefaultFactory {

    private static final long serialVersionUID = 510l;

    private Map<String, Object> outputSet;

    public OutputSetResolverFactory(Map<String, Object> outputSet) {
        this.outputSet = outputSet;
    }

    public boolean isResolveable(String name) {
        return outputSet.containsKey(name);
    }

    public VariableResolver getVariableResolver(String name) {
        return new SimpleValueResolver(outputSet.get(name));
    }

}
