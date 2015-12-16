/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend.verifiers;

import org.drools.core.base.TypeResolver;
import org.drools.core.util.MVELSafeHelper;
import org.drools.workbench.models.testscenarios.backend.util.DateObjectFactory;
import org.drools.workbench.models.testscenarios.backend.util.FieldTypeResolver;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FactFieldValueVerifier {

    private final Map<String, Object> populatedData;
    private final String factName;
    private final Object factObject;

    private VerifyField currentField;
    final TypeResolver resolver;

    private final ParserConfiguration pconf;
    private final ParserContext pctx;

    public FactFieldValueVerifier(Map<String, Object> populatedData,
                                  String factName,
                                  Object factObject,
                                  final TypeResolver resolver) {
        this.populatedData = populatedData;
        this.factName = factName;
        this.factObject = factObject;
        this.resolver = resolver;

        this.pconf = new ParserConfiguration();
        pconf.setClassLoader(resolver.getClassLoader());
        this.pctx = new ParserContext(pconf);
        pctx.setStrongTyping(true);
    }

    public void checkFields(List<VerifyField> fieldValues) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Iterator<VerifyField> fields = fieldValues.iterator();
        while (fields.hasNext()) {
            this.currentField = fields.next();

            if (currentField.getExpected() != null) {
                ResultVerifier resultVerifier = new ResultVerifier(factObject);

                resultVerifier.setExpected(getExpectedResult());

                currentField.setSuccessResult(resultVerifier.isSuccess(currentField));

                if (!currentField.getSuccessResult()) {
                    currentField.setActualResult(resultVerifier.getActual(currentField));

                    currentField.setExplanation(getFailingExplanation());
                } else {
                    currentField.setExplanation(getSuccessfulExplanation());
                }
            }

        }

    }

    private Object getExpectedResult() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object expectedResult = currentField.getExpected().trim();
        if (currentField.getExpected().startsWith("=")) {
            expectedResult = MVELSafeHelper.getEvaluator().eval(currentField.getExpected().substring(1),
                    this.populatedData);
        } else if (currentField.getNature() == VerifyField.TYPE_ENUM) {
            try {
                // The string representation of enum value is using a
                // format like CheeseType.CHEDDAR
                String classNameOfEnum = currentField.getExpected().substring(0,
                        currentField.getExpected().indexOf("."));
                String valueOfEnum = currentField.getExpected().substring(currentField.getExpected().indexOf(".") + 1);
                String fullName = resolver.getFullTypeName(classNameOfEnum);
                if (fullName != null && !"".equals(fullName)) {
                    valueOfEnum = fullName + "." + valueOfEnum;
                }

                Serializable compiled = MVEL.compileExpression(valueOfEnum,
                        pctx);
                expectedResult = MVELSafeHelper.getEvaluator().executeExpression(compiled);

            } catch (ClassNotFoundException e) {
                //Do nothing.
            }
        } else if (isFieldDate()) {
            return DateObjectFactory.createTimeObject(FieldTypeResolver.getFieldType(currentField.getFieldName(), factObject), currentField.getExpected());
        }
        return expectedResult;
    }

    private boolean isFieldDate() {
        return FieldTypeResolver.isDate(currentField.getFieldName(), factObject);
    }

    private String getSuccessfulExplanation() {
        if (currentField.getOperator().equals("==")) {
            return "[" + factName + "] field [" + currentField.getFieldName() + "] was [" + currentField.getExpected() + "].";
        } else if (currentField.getOperator().equals("!=")) {
            return "[" + factName + "] field [" + currentField.getFieldName() + "] was not [" + currentField.getExpected() + "].";
        }

        return "";
    }

    private String getFailingExplanation() {
        if (currentField.getOperator().equals("==")) {
            return "[" + factName + "] field [" + currentField.getFieldName() + "] was [" + currentField.getActualResult() + "] expected [" + currentField.getExpected() + "].";
        } else {
            return "[" + factName + "] field [" + currentField.getFieldName() + "] was not expected to be [" + currentField.getActualResult() + "].";
        }
    }
}

class ResultVerifier {

    private final Map<String, Object> variables = new HashMap<String, Object>();
    private ParserContext parserContext = new ParserContext();

    protected ResultVerifier(Object factObject) {
        addVariable("__fact__",
                factObject);
    }

    protected void setExpected(Object expected) {
        addVariable("__expected__",
                expected);
    }

    private void addVariable(String name,
                             Object object) {
        variables.put(name,
                object);

        parserContext.addInput(name,
                object.getClass());
    }

    protected Boolean isSuccess(VerifyField currentField) {
        String s = "__fact__." + currentField.getFieldName() + " " + currentField.getOperator() + " __expected__";
        CompiledExpression expression = new ExpressionCompiler(s, parserContext).compile();

        return (Boolean) MVELSafeHelper.getEvaluator().executeExpression(expression,
                variables);
    }

    protected String getActual(VerifyField currentField) {
        Object actualValue = MVELSafeHelper.getEvaluator().executeExpression(new ExpressionCompiler("__fact__." + currentField.getFieldName(), parserContext).compile(),
                variables);

        return (actualValue != null) ? actualValue.toString() : "";

    }
}
