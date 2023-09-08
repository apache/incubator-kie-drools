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
package org.drools.mvel;

import java.util.ArrayList;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.test.model.Cheese;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;

public class MVELConstraintTestUtil extends MVELConstraint {

    static {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;
    }

    public MVELConstraintTestUtil( String expression, FieldValue fieldValue, ReadAccessor extractor) {
        super(null, expression, null, findConstraintTypeForExpression(expression), fieldValue, extractor, null);
    }

    public MVELConstraintTestUtil( String expression, Declaration declaration, ReadAccessor extractor) {
        super(new ArrayList<String>(), expression, new Declaration[] { declaration }, null, null, findConstraintTypeForExpression(expression), declaration, extractor, expression.contains(":="));
    }

    public MVELConstraintTestUtil( String expression, String operator, Declaration declaration, ReadAccessor extractor) {
        this(expression, ConstraintTypeOperator.decode(operator), declaration, extractor);
    }

    public MVELConstraintTestUtil(String expression, ConstraintTypeOperator constraintType, Declaration declaration, ReadAccessor extractor) {
        super(new ArrayList<String>(), expression, new Declaration[] { declaration }, null, null, constraintType, declaration, extractor, expression.contains(":="));
    }

    @Override
    protected ParserConfiguration getParserConfiguration(ValueResolver valueResolver) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.addImport(Cheese.class);
        return parserConfiguration;
    }

    private static ConstraintTypeOperator findConstraintTypeForExpression(String expression) {
        if (expression.contains("==")) {
            return ConstraintTypeOperator.EQUAL;
        }
        if (expression.contains("!=")) {
            return ConstraintTypeOperator.NOT_EQUAL;
        }
        if (expression.contains(">")) {
            return ConstraintTypeOperator.GREATER_THAN;
        }
        if (expression.contains(">=")) {
            return ConstraintTypeOperator.GREATER_OR_EQUAL;
        }
        if (expression.contains("<")) {
            return ConstraintTypeOperator.LESS_THAN;
        }
        if (expression.contains("<=")) {
            return ConstraintTypeOperator.LESS_OR_EQUAL;
        }
        return ConstraintTypeOperator.UNKNOWN;
    }
}
