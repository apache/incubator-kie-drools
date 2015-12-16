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

package org.drools.verifier.visitor;

import org.drools.core.base.evaluators.Operator;
import org.drools.compiler.lang.descr.FieldConstraintDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.compiler.lang.descr.RestrictionConnectiveDescr;
import org.drools.compiler.lang.descr.RestrictionDescr;
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.lang.descr.VariableRestrictionDescr;
import org.drools.verifier.components.EnumField;
import org.drools.verifier.components.EnumRestriction;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternVariable;
import org.drools.verifier.components.QualifiedIdentifierRestriction;
import org.drools.verifier.components.ReturnValueRestriction;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

import java.util.List;

public class FieldConstraintDescrVisitor {

    private final VerifierData data;

    private final ObjectType objectType;
    private final Pattern pattern;
    private Field field;

    private final int orderNumber;

    private final Solvers solvers;

    public FieldConstraintDescrVisitor(VerifierData data,
                                       Pattern pattern,
                                       Solvers solvers,
                                       int orderNumber) {
        this.data = data;
        this.pattern = pattern;
        this.solvers = solvers;
        this.orderNumber = orderNumber;
        this.objectType = data.getVerifierObject(VerifierComponentType.OBJECT_TYPE,
                pattern.getObjectTypePath());
    }

    public void visitFieldConstraintDescr(FieldConstraintDescr descr) throws UnknownDescriptionException {

        field = data.getFieldByObjectTypeAndFieldName(objectType.getFullName(),
                descr.getFieldName());
        if (field == null) {
            field = ObjectTypeFactory.createField(descr, descr.getFieldName(),
                    objectType);
            data.add(field);
        }

        visit(descr.getRestriction());
    }

    public void visit(RestrictionDescr restrictionDescr) throws UnknownDescriptionException {
        if (restrictionDescr instanceof LiteralRestrictionDescr) {
            visit((LiteralRestrictionDescr) restrictionDescr);
        } else if (restrictionDescr instanceof QualifiedIdentifierRestrictionDescr) {
            visit((QualifiedIdentifierRestrictionDescr) restrictionDescr);
        } else if (restrictionDescr instanceof ReturnValueRestrictionDescr) {
            visit((ReturnValueRestrictionDescr) restrictionDescr);
        } else if (restrictionDescr instanceof VariableRestrictionDescr) {
            visit((VariableRestrictionDescr) restrictionDescr);
        } else if (restrictionDescr instanceof PredicateDescr) {
            visit((PredicateDescr) restrictionDescr);
        } else if (restrictionDescr instanceof RestrictionConnectiveDescr) {
            visit((RestrictionConnectiveDescr) restrictionDescr);
        } else {
            throw new UnknownDescriptionException(restrictionDescr);
        }
    }

    private void visit(List<RestrictionDescr> restrictions) throws UnknownDescriptionException {
        for (RestrictionDescr restrictionDescr : restrictions) {
            visit(restrictionDescr);
        }
    }

    private void visit(RestrictionConnectiveDescr descr) throws UnknownDescriptionException {

        if (descr.getConnective() == RestrictionConnectiveDescr.AND) {

            solvers.startOperator(OperatorDescrType.AND);
            visit(descr.getRestrictions());
            solvers.endOperator();

        } else if (descr.getConnective() == RestrictionConnectiveDescr.OR) {

            solvers.startOperator(OperatorDescrType.OR);
            visit(descr.getRestrictions());
            solvers.endOperator();

        } else {
            throw new UnknownDescriptionException(descr);
        }
    }

    /**
     * End
     *
     * @param descr
     */
    private void visit(LiteralRestrictionDescr descr) {

        LiteralRestriction restriction = LiteralRestriction.createRestriction(pattern,
                descr.getText());

        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setFieldPath(field.getPath());
        restriction.setOperator(Operator.determineOperator(descr.getEvaluator(),
                descr.isNegated()));
        restriction.setOrderNumber(orderNumber);
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());

        // Set field value, if it is unset.
        field.setFieldType(restriction.getValueType());

        data.add(restriction);
        solvers.addPatternComponent(restriction);
    }

    /**
     * End
     *
     * @param descr
     */
    private void visit(QualifiedIdentifierRestrictionDescr descr) {

        String text = descr.getText();

        String base = text.substring(0,
                text.indexOf("."));
        String fieldName = text.substring(text.indexOf("."));

        Variable patternVariable = data.getVariableByRuleAndVariableName(pattern.getRuleName(),
                base);

        if (patternVariable != null) {

            QualifiedIdentifierRestriction restriction = new QualifiedIdentifierRestriction(pattern);

            restriction.setPatternIsNot(pattern.isPatternNot());
            restriction.setFieldPath(field.getPath());
            restriction.setOperator(Operator.determineOperator(descr.getEvaluator(),
                    descr.isNegated()));
            restriction.setVariablePath(patternVariable.getPath());
            restriction.setVariableName(base);
            restriction.setVariablePath(fieldName);
            restriction.setOrderNumber(orderNumber);
            restriction.setParentPath(pattern.getPath());
            restriction.setParentType(pattern.getVerifierComponentType());

            // Set field value, if it is not set.
            field.setFieldType(Field.VARIABLE);

            data.add(restriction);
            solvers.addPatternComponent(restriction);
        } else {

            EnumField enumField = (EnumField) data.getFieldByObjectTypeAndFieldName(base,
                    fieldName);
            if (enumField == null) {

                ObjectType objectType = data.getObjectTypeByFullName(base);

                if (objectType == null) {
                    Import objectImport = data.getImportByName(base);

                    if (objectImport != null) {
                        objectType = ObjectTypeFactory.createObjectType(descr,objectImport);
                    } else {
                        objectType = ObjectTypeFactory.createObjectType(descr,base);
                    }

                    data.add(objectType);
                }

                enumField = new EnumField(descr);

                enumField.setObjectTypePath(objectType.getPath());
                enumField.setObjectTypeName(objectType.getName());
                enumField.setName(fieldName);

                objectType.getFields().add(enumField);

                data.add(enumField);
            }

            EnumRestriction restriction = new EnumRestriction(pattern);

            restriction.setPatternIsNot(pattern.isPatternNot());
            restriction.setFieldPath(field.getPath());
            restriction.setOperator(Operator.determineOperator(descr.getEvaluator(),
                    descr.isNegated()));
            restriction.setEnumBasePath(enumField.getPath());
            restriction.setEnumBase(base);
            restriction.setEnumName(fieldName);
            restriction.setOrderNumber(orderNumber);
            restriction.setParentPath(pattern.getPath());
            restriction.setParentType(pattern.getVerifierComponentType());

            // Set field value, if it is not set.
            field.setFieldType(Field.ENUM);

            data.add(restriction);
            solvers.addPatternComponent(restriction);
        }
    }

    /**
     * End
     * <p/>
     * Foo( bar == $bar )<br>
     * $bar is a VariableRestrictionDescr
     *
     * @param descr
     */
    private void visit(VariableRestrictionDescr descr) {

        PatternVariable patternVariable = (PatternVariable) data.getVariableByRuleAndVariableName(pattern.getRuleName(),
                descr.getIdentifier());
        VariableRestriction restriction = new VariableRestriction(pattern);

        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setFieldPath(field.getPath());
        restriction.setOperator(Operator.determineOperator(descr.getEvaluator(),
                descr.isNegated()));
        restriction.setVariable(patternVariable);
        restriction.setOrderNumber(orderNumber);
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());

        // Set field value, if it is unset.
        field.setFieldType(Field.VARIABLE);

        data.add(restriction);
        solvers.addPatternComponent(restriction);
    }

    /**
     * End
     *
     * @param descr
     */
    private void visit(ReturnValueRestrictionDescr descr) {

        ReturnValueRestriction restriction = new ReturnValueRestriction(pattern);

        restriction.setPatternIsNot(pattern.isPatternNot());
        restriction.setFieldPath(field.getPath());
        restriction.setOperator(Operator.determineOperator(descr.getEvaluator(),
                descr.isNegated()));
        restriction.setClassMethodName(descr.getClassMethodName());
        restriction.setContent(descr.getContent());
        restriction.setDeclarations(descr.getDeclarations());
        restriction.setOrderNumber(orderNumber);
        restriction.setParentPath(pattern.getPath());
        restriction.setParentType(pattern.getVerifierComponentType());

        data.add(restriction);
        solvers.addPatternComponent(restriction);

    }

}
