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
package org.kie.pmml.compiler.commons.codegenfactories;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Field;
import org.dmg.pmml.MiningField;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLIntervalFactory.getIntervalVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

public class KiePMMLMiningFieldFactory {

    private KiePMMLMiningFieldFactory() {
    }

    static final String KIE_PMML_MININGFIELD_TEMPLATE_JAVA = "KiePMMLMiningFieldTemplate.tmpl";
    static final String KIE_PMML_MININGFIELD_TEMPLATE = "KiePMMLMiningFieldTemplate";
    static final String GETKIEPMMLMININGFIELD = "getKiePMMLMiningField";
    static final String MININGFIELD = "miningField";
    static final ClassOrInterfaceDeclaration MININGFIELD_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_MININGFIELD_TEMPLATE_JAVA);
        MININGFIELD_TEMPLATE = cloneCU.getClassByName(KIE_PMML_MININGFIELD_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_MININGFIELD_TEMPLATE));
        MININGFIELD_TEMPLATE.getMethodsByName(GETKIEPMMLMININGFIELD).get(0).clone();
    }

    static BlockStmt getMiningFieldVariableDeclaration(final String variableName, final MiningField miningField,
                                                       final List<Field<?>> fields) {
        final MethodDeclaration methodDeclaration =
                MININGFIELD_TEMPLATE.getMethodsByName(GETKIEPMMLMININGFIELD).get(0).clone();
        final BlockStmt miningFieldBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(miningFieldBody, MININGFIELD).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, MININGFIELD, miningFieldBody)));

        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      MININGFIELD, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(miningField.getName());
        Expression fieldUsageTypeExpr;
        if (miningField.getUsageType() != null) {
            final FIELD_USAGE_TYPE fieldUsageType = FIELD_USAGE_TYPE.byName(miningField.getUsageType().value());
            fieldUsageTypeExpr = new NameExpr(FIELD_USAGE_TYPE.class.getName() + "." + fieldUsageType.name());
        } else {
            fieldUsageTypeExpr = new NullLiteralExpr();
        }
        Expression opTypeExpr;
        if (miningField.getOpType() != null) {
            final OP_TYPE opType = OP_TYPE.byName(miningField.getOpType().value());
            opTypeExpr = new NameExpr(OP_TYPE.class.getName() + "." + opType.name());
        } else {
            opTypeExpr = new NullLiteralExpr();
        }
        final List<Field<?>> mappedFields = getMappedFields(fields,miningField.getName());
        final DataType dataType = getDataType(mappedFields,miningField.getName());
        final DATA_TYPE data_TYPE = DATA_TYPE.byName(dataType.value());
        Expression dataTypeExpr = new NameExpr(DATA_TYPE.class.getName() + "." + data_TYPE.name());
        Expression missingValueTreatmentMethodExpr;
        if (miningField.getMissingValueTreatment() != null) {
            final MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod =
                    MISSING_VALUE_TREATMENT_METHOD.byName(miningField.getMissingValueTreatment().value());
            missingValueTreatmentMethodExpr =
                    new NameExpr(MISSING_VALUE_TREATMENT_METHOD.class.getName() + "." + missingValueTreatmentMethod.name());
        } else {
            missingValueTreatmentMethodExpr = new NullLiteralExpr();
        }
        Expression invalidValueTreatmentMethodExpr;
        if (miningField.getInvalidValueTreatment() != null) {
            final INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod =
                    INVALID_VALUE_TREATMENT_METHOD.byName(miningField.getInvalidValueTreatment().value());
            invalidValueTreatmentMethodExpr =
                    new NameExpr(INVALID_VALUE_TREATMENT_METHOD.class.getName() + "." + invalidValueTreatmentMethod.name());
        } else {
            invalidValueTreatmentMethodExpr = new NullLiteralExpr();
        }
        Expression missingValueReplacementExpr;
        if (miningField.getMissingValueReplacement() != null) {
            final String missingValueReplacement = miningField.getMissingValueReplacement().toString();
            missingValueReplacementExpr = new StringLiteralExpr(missingValueReplacement);
        } else {
            missingValueReplacementExpr = new NullLiteralExpr();
        }
        Expression invalidValueReplacementExpr;
        if (miningField.getInvalidValueReplacement() != null) {
            final String invalidValueReplacement = miningField.getInvalidValueReplacement().toString();
            invalidValueReplacementExpr = new StringLiteralExpr(invalidValueReplacement);
        } else {
            invalidValueReplacementExpr = new NullLiteralExpr();
        }
        DataField dataField = getMappedDataField(mappedFields);
        NodeList<Expression> allowedValuesExpressions = dataField != null ? getAllowedValuesExpressions(dataField) :
                new NodeList<>();
        NodeList<Expression> intervalsExpressions = dataField != null ? getIntervalsExpressions(dataField) :
                new NodeList<>();
        builder.setArgument(0, nameExpr);
        getChainedMethodCallExprFrom("withFieldUsageType", initializer).setArgument(0, fieldUsageTypeExpr);
        getChainedMethodCallExprFrom("withOpType", initializer).setArgument(0, opTypeExpr);
        getChainedMethodCallExprFrom("withDataType", initializer).setArgument(0, dataTypeExpr);
        getChainedMethodCallExprFrom("withMissingValueTreatmentMethod", initializer).setArgument(0,
                                                                                                 missingValueTreatmentMethodExpr);
        getChainedMethodCallExprFrom("withInvalidValueTreatmentMethod", initializer).setArgument(0,
                                                                                                 invalidValueTreatmentMethodExpr);
        getChainedMethodCallExprFrom("withMissingValueReplacement", initializer).setArgument(0,
                                                                                             missingValueReplacementExpr);
        getChainedMethodCallExprFrom("withInvalidValueReplacement", initializer).setArgument(0,
                                                                                             invalidValueReplacementExpr);
        getChainedMethodCallExprFrom("withAllowedValues", initializer).getArgument(0).asMethodCallExpr().setArguments(allowedValuesExpressions);
        getChainedMethodCallExprFrom("withIntervals", initializer).getArgument(0).asMethodCallExpr().setArguments(intervalsExpressions);

        miningFieldBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }

    /**
     * Returns a list of <code>Field</code>s filtered by field name
     * @param fields
     * @param fieldName
     * @return
     */
    private static List<Field<?>> getMappedFields(final List<Field<?>> fields,
                                                  final String fieldName) {
        return fields.stream()
                .filter(fld -> Objects.equals(fieldName,fld.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a DataField
     * Expect a list of <code>Field</code>s filtered by field name
     * @param fields List of <code>Field</code>s filtered by field name
     * @return
     */
    private static DataField getMappedDataField(final List<Field<?>> fields) {
        return fields.stream()
                .filter(DataField.class::isInstance)
                .map(DataField.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Expect a list of <code>Field</code>s filtered by field name
     * @param fields List of <code>Field</code>s filtered by field name
     * @param fieldName
     * @return
     */
    private static DataType getDataType(final List<Field<?>> fields,
                                        final String fieldName) {
        return fields.stream()
                .map(Field::getDataType)
                .findFirst()
                .orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to find DataType for " +
                                                                                      "field %s",
                                                                              fieldName)));
    }

    private static NodeList<Expression> getAllowedValuesExpressions(final DataField dataField) {
        final NodeList<Expression> toReturn = new NodeList<>();
        if (dataField.hasValues()) {
            dataField.getValues().forEach(value -> toReturn.add(new StringLiteralExpr(value.getValue().toString())));
        }
        return toReturn;
    }

    private static NodeList<Expression> getIntervalsExpressions(final DataField dataField) {
        final NodeList<Expression> toReturn = new NodeList<>();
        if (dataField.hasIntervals()) {
            dataField.getIntervals().forEach(interval -> {
                BlockStmt intervalStmt = getIntervalVariableDeclaration("name", interval);
                Expression toAdd = intervalStmt.getStatement(0)
                        .asExpressionStmt()
                        .getExpression()
                        .asVariableDeclarationExpr()
                        .getVariable(0)
                        .getInitializer()
                        .orElseThrow(() -> new KiePMMLInternalException(String.format("Failed to create initializer " +
                                                                                              "for " +
                                                                                              "Interval %s",
                                                                                      interval)));
                toReturn.add(toAdd);
            });
        }
        return toReturn;
    }
}
