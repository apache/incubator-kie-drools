/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.testscenarios.backend.populators;

import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.FieldData;


public class CollectionFieldPopulator extends FieldPopulator {


    private final String expression;

    public CollectionFieldPopulator(Object factObject, CollectionFieldData field) {
        super(factObject, field.getName());
        this.expression = createExpression(field);
    }

    private String createExpression(CollectionFieldData field) {
        String result = "[";

        int index = 1;
        for (FieldData fieldData : field.getCollectionFieldList()) {
            result += fieldData.getValue().replace("=", "");
            if (index < field.getCollectionFieldList().size()) {
                result += ",";
            }
            index++;
        }

        return result + "]";
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        populateField(MVELSafeHelper.getEvaluator().eval(expression,
                populatedData),
                populatedData);
    }
}
