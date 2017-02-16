/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.Arrays;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BRLRuleModelTest extends BaseBRLTest {

    private BRLRuleModel rm;

    @Before
    public void setup() {
        super.setup();
        this.rm = new BRLRuleModel(dtable);
    }

    @Test
    public void getLHSBoundFactsWithNoDefinition() {
        assertThereAreNoBindings();
    }

    @Test
    public void getLHSBoundFactsWithPattern() {
        whenThereIsAPattern("Applicant",
                            "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactsWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant",
                                   "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactsWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                "$a");
        assertThereIsLHSBindingFor("$a");
    }

    @Test
    public void getLHSBoundFactWithPattern() {
        whenThereIsAPattern("Applicant",
                            "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBoundFactWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant",
                                   "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBoundFactWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                "$a");
        assertThereIsABoundFactFor("$a");
    }

    @Test
    public void getLHSBindingTypeWithPattern() {
        whenThereIsAPattern("Applicant",
                            "$a");
        assertLHSBindingTypeFor("Applicant",
                                "$a");
    }

    @Test
    public void getLHSBindingTypeWithFactPattern() {
        whenThereIsABRLFactPattern("Applicant",
                                   "$a");
        assertLHSBindingTypeFor("Applicant",
                                "$a");
    }

    @Test
    public void getLHSBindingTypeWithFromCompositeFactPattern() {
        whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                "$a");
        assertLHSBindingTypeFor("Applicant",
                                "$a");
    }

    @Test
    public void getLHSBoundFieldWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant",
                                                "$a");
        whenPatternHasAField(p,
                             "field1",
                             DataType.TYPE_STRING,
                             "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBoundFieldWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant",
                                                                  "$a");
        whenBRLFactPatternHasAField(brl,
                                    "field1",
                                    DataType.TYPE_STRING,
                                    "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBoundFieldWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                                               "$a");
        whenBRLFromCompositeFactPatternHasAField(brl,
                                                 "field1",
                                                 DataType.TYPE_STRING,
                                                 "$f");
        assertThereIsAFieldBindingFor("$f");
    }

    @Test
    public void getLHSBindingTypeWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant",
                                                "$a");
        whenPatternHasAField(p,
                             "field1",
                             DataType.TYPE_STRING,
                             "$f");
        assertLHSBindingTypeFor(DataType.TYPE_STRING,
                                "$f");
    }

    @Test
    public void getLHSBindingTypeWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant",
                                                                  "$a");
        whenBRLFactPatternHasAField(brl,
                                    "field1",
                                    DataType.TYPE_STRING,
                                    "$f");
        assertLHSBindingTypeFor(DataType.TYPE_STRING,
                                "$f");
    }

    @Test
    public void getLHSBindingTypeWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                                               "$a");
        whenBRLFromCompositeFactPatternHasAField(brl,
                                                 "field1",
                                                 DataType.TYPE_STRING,
                                                 "$f");
        assertLHSBindingTypeFor(DataType.TYPE_STRING,
                                "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithPatternField() {
        final Pattern52 p = whenThereIsAPattern("Applicant",
                                                "$a");
        whenPatternHasAField(p,
                             "field1",
                             DataType.TYPE_STRING,
                             "$f");
        assertLHSParentFactPatternFor("$a",
                                      "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFactPattern("Applicant",
                                                                  "$a");
        whenBRLFactPatternHasAField(brl,
                                    "field1",
                                    DataType.TYPE_STRING,
                                    "$f");
        assertLHSParentFactPatternFor("$a",
                                      "$f");
    }

    @Test
    public void getLHSParentFactPatternForBindingWithFromCompositeFactPatternField() {
        final BRLConditionColumn brl = whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                                               "$a");
        whenBRLFromCompositeFactPatternHasAField(brl,
                                                 "field1",
                                                 DataType.TYPE_STRING,
                                                 "$f");
        assertLHSParentFactPatternFor("$a",
                                      "$f");
    }

    @Test
    public void getAllLHSVariables() {
        final Pattern52 p = whenThereIsAPattern("Applicant",
                                                "$a1");
        whenPatternHasAField(p,
                             "field1",
                             DataType.TYPE_STRING,
                             "$f1");

        final BRLConditionColumn brl1 = whenThereIsABRLFactPattern("Applicant",
                                                                   "$a2");
        whenBRLFactPatternHasAField(brl1,
                                    "field1",
                                    DataType.TYPE_STRING,
                                    "$f2");

        final BRLConditionColumn brl2 = whenThereIsABRLFromCompositeFactPattern("Applicant",
                                                                                "$a3");
        whenBRLFromCompositeFactPatternHasAField(brl2,
                                                 "field1",
                                                 DataType.TYPE_STRING,
                                                 "$f3");

        assertLHSBindings("$a1",
                          "$a2",
                          "$a3",
                          "$f1",
                          "$f2",
                          "$f3");
    }

    private ConditionCol52 whenPatternHasAField(final Pattern52 p,
                                                final String fieldName,
                                                final String fieldType,
                                                final String fieldBinding) {
        final ConditionCol52 c = new ConditionCol52();
        c.setFactField(fieldName);
        c.setFieldType(fieldType);
        c.setBinding(fieldBinding);
        p.getChildColumns().add(c);
        return c;
    }

    private void assertThereAreNoBindings() {
        final List<String> result = rm.getLHSBoundFacts();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void assertThereIsLHSBindingFor(final String binding) {
        final List<String> result = rm.getLHSBoundFacts();
        assertNotNull(result);
        assertEquals(1,
                     result.size());
        assertEquals(binding,
                     result.get(0));
    }

    private void assertThereIsABoundFactFor(final String binding) {
        final FactPattern result = rm.getLHSBoundFact(binding);
        assertNotNull(result);
        assertEquals(binding,
                     result.getBoundName());
    }

    private void assertThereIsAFieldBindingFor(final String fieldBinding) {
        final SingleFieldConstraint sfc = rm.getLHSBoundField(fieldBinding);
        assertNotNull(sfc);
    }

    private void assertLHSBindingTypeFor(final String expectedType,
                                         final String binding) {
        final String actualType = rm.getLHSBindingType(binding);
        assertEquals(expectedType,
                     actualType);
    }

    private void assertLHSParentFactPatternFor(final String patternBinding,
                                               final String fieldBinding) {
        final FactPattern boundPattern1 = rm.getLHSParentFactPatternForBinding(patternBinding);
        assertNotNull(boundPattern1);
        assertEquals(patternBinding,
                     boundPattern1.getBoundName());

        final FactPattern boundPattern2 = rm.getLHSParentFactPatternForBinding(fieldBinding);
        assertNotNull(boundPattern2);
        assertEquals(patternBinding,
                     boundPattern2.getBoundName());
    }

    private void assertLHSBindings(final String... expectedBindings) {
        final List<String> actualBindings = rm.getAllLHSVariables();
        assertNotNull(actualBindings);
        assertEquals(expectedBindings.length,
                     actualBindings.size());

        Arrays.asList(expectedBindings).stream().forEach(actualBindings::contains);
    }
}
