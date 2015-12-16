/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend.populators;

import java.util.Map;

import org.drools.core.util.MVELSafeHelper;

public class ExpressionFieldPopulator extends FieldPopulator {

    private final String expression;

    public ExpressionFieldPopulator(Object factObject,
                                    String fieldName,
                                    String expression) {
        super( factObject,
               fieldName );
        this.expression = expression;
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        populateField( MVELSafeHelper.getEvaluator().eval(expression,
                                                          populatedData),
                       populatedData );
    }
}
