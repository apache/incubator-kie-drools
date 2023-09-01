/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.alphasupport;

import java.util.Collection;
import java.util.List;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.compiler.alphanetbased.evaluator.ColumnValidator;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

public class ColumnValidatorTemplate extends ColumnValidator {

    @Override
    protected List<UnaryTest> validationInputTests() {
        return ColumnValidatorX.getInstance().getUnaryTests();
    }

    // TODO DT-ANC this should be a string
    @Override
    protected DMNType dmnType() {
        return null;
    }

    @Override
    protected String validValues() {
        return "VALID_VALUES";
    }

    @Override
    protected String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    protected String decisionTableName() {
        return "DECISION_TABLE_NAME";
    }

    private static ColumnValidatorTemplate INSTANCE;

    public static ColumnValidatorTemplate getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ColumnValidatorTemplate();
        }
        return INSTANCE;
    }
}
