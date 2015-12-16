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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.MVELSafeHelper;

public abstract class FieldPopulator {

    private final Object factObject;
    private final String fieldName;

    public FieldPopulator(Object factObject,
                          String fieldName) {
        this.factObject = factObject;
        this.fieldName = fieldName;
    }

    public abstract void populate(Map<String, Object> populatedData);

    protected void populateField(Object value,
                                 Map<String, Object> populatedData) {
        try {
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.putAll( populatedData );
            vars.put( "__val__",
                      value );
            vars.put( "__fact__",
                      factObject );

            MVELSafeHelper.getEvaluator().eval("__fact__." + fieldName + "= __val__",
                    vars);

        } catch ( NumberFormatException e ) {
            if ( value instanceof String && ((String) value).isEmpty() ) {
                // Empty Strings can be ignored.
            } else {
                throw e;
            }
        }
    }

}
