/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.drools.workbench.models.datamodel.rule.visitors;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.appformer.project.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.TemplateAware;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleModelVisitorTest {

    @Test
    public void visitTemplate() {
        Map<InterpolationVariable, Integer> variableMap = new HashMap<>();
        RuleModelVisitor visitor = new RuleModelVisitor(variableMap);

        visitor.visit(new TemplateAwareIAction());

        assertTrue(variableMap.containsKey(new InterpolationVariable("test",
                                                                     DataType.TYPE_OBJECT)));
    }

    private static class TemplateAwareIAction implements IAction,
                                                         TemplateAware {

        @Override
        public Collection<InterpolationVariable> extractInterpolationVariables() {
            return Arrays.asList(new InterpolationVariable("test",
                                                           DataType.TYPE_OBJECT));
        }

        @Override
        public void substituteTemplateVariables(Function<String, String> keyToValueFunction) {
        }

        @Override
        public TemplateAware cloneTemplateAware() {
            return new TemplateAwareIAction();
        }
    }
}
