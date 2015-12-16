/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.index.IndexUtil;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;

import java.util.ArrayList;

public class MvelConstraintTestUtil extends MvelConstraint {

    static {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;
    }

    public MvelConstraintTestUtil(String expression, FieldValue fieldValue, InternalReadAccessor extractor) {
        super(null, expression, null, findConstraintTypeForExpression(expression), fieldValue, extractor, null);
    }

    public MvelConstraintTestUtil(String expression, Declaration declaration, InternalReadAccessor extractor) {
        super(new ArrayList<String>(), expression, new Declaration[] { declaration }, null, null, findConstraintTypeForExpression(expression), declaration, extractor, expression.contains(":="));
    }

    public MvelConstraintTestUtil(String expression, String operator, Declaration declaration, InternalReadAccessor extractor) {
        this(expression, IndexUtil.ConstraintType.decode(operator), declaration, extractor);
    }

    public MvelConstraintTestUtil(String expression, IndexUtil.ConstraintType constraintType, Declaration declaration, InternalReadAccessor extractor) {
        super(new ArrayList<String>(), expression, new Declaration[] { declaration }, null, null, constraintType, declaration, extractor, expression.contains(":="));
    }

    @Override
    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.addImport(Cheese.class);
        return parserConfiguration;
    }

    private static IndexUtil.ConstraintType findConstraintTypeForExpression(String expression) {
        if (expression.contains("==")) {
            return IndexUtil.ConstraintType.EQUAL;
        }
        if (expression.contains("!=")) {
            return IndexUtil.ConstraintType.NOT_EQUAL;
        }
        if (expression.contains(">")) {
            return IndexUtil.ConstraintType.GREATER_THAN;
        }
        if (expression.contains(">=")) {
            return IndexUtil.ConstraintType.GREATER_OR_EQUAL;
        }
        if (expression.contains("<")) {
            return IndexUtil.ConstraintType.LESS_THAN;
        }
        if (expression.contains("<=")) {
            return IndexUtil.ConstraintType.LESS_OR_EQUAL;
        }
        return IndexUtil.ConstraintType.UNKNOWN;
    }
}
