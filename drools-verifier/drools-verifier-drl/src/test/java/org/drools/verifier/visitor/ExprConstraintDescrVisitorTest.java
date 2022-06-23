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

package org.drools.verifier.visitor;

import java.util.Collection;

import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.base.evaluators.Operator;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.Eval;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.NumberRestriction;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.StringRestriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ExprConstraintDescrVisitorTest extends TestBase {

    @Test
    public void testVisitPerson() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr1.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        assertThat(allRestrictions.size()).isEqualTo(3);
        assertThat(allFields.size()).isEqualTo(3);

        for (Field field : allFields) {
            assertThat(field.getFieldType()).isNotNull();
        }

        assertContainsField("name");
        assertContainsField("lastName");
        assertContainsField("age");

        assertContainsStringRestriction(Operator.EQUAL, "toni");
        assertContainsStringRestriction(Operator.NOT_EQUAL, "Lake");
        assertContainsNumberRestriction(Operator.GREATER, 20);
        assertContainsEval("eval( true )");
    }

    @Test
    public void testVisitAnd() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr2.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        assertThat(allRestrictions.size()).isEqualTo(2);
        assertContainsFields(1);


        assertContainsField("age");

        assertContainsNumberRestriction(Operator.GREATER, 0);
        assertContainsNumberRestriction(Operator.LESS, 100);
    }

    @Test
    public void testVisitVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("ExprConstraintDescr3.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<StringRestriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        assertThat(allRestrictions.size()).isEqualTo(1);
        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test 1", "var");

        assertContainsVariableRestriction(Operator.EQUAL, "var");
    }

    private void assertContainsEval(String eval) {
        Collection<VerifierComponent> allEvals = verifierData.getAll(VerifierComponentType.PREDICATE);

        for (VerifierComponent component : allEvals) {
            Eval evalObject = (Eval) component;
            if (eval.equals(evalObject.getContent())) {
                return;
            }
        }

        fail(String.format("Could not find Eval : %s ", eval));
    }

    private void assertContainsVariableRestriction(Operator operator, String variableName) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof VariableRestriction) {
                VariableRestriction variableRestriction = (VariableRestriction) restriction;
                if (variableName.equals(variableRestriction.getVariable().getName()) && operator.equals(variableRestriction.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find VariableRestriction: Operator : %s Variable name: %s", operator, variableName));
    }

    private void assertContainsStringRestriction(Operator operator, String value) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof StringRestriction) {
                StringRestriction stringLiteral = (StringRestriction) restriction;
                if (value.equals(stringLiteral.getValueAsString()) && operator.equals(stringLiteral.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find StringRestriction: Operator : %s Value: %s", operator, value));
    }

    private void assertContainsNumberRestriction(Operator operator, Number value) {
        Collection<Restriction> allRestrictions = verifierData.getAll(VerifierComponentType.RESTRICTION);

        for (Restriction restriction : allRestrictions) {
            if (restriction instanceof NumberRestriction) {
                NumberRestriction numberRestriction = (NumberRestriction) restriction;
                if (value.equals(numberRestriction.getValue()) && operator.equals(numberRestriction.getOperator())) {
                    return;
                }
            }
        }

        fail(String.format("Could not find NumberRestriction: Operator : %s Value: %s", operator, value));
    }

}
