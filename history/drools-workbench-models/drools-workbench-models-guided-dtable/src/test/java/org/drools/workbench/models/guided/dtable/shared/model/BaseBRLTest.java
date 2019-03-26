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

import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.junit.Before;

import static org.junit.Assert.*;

public abstract class BaseBRLTest {

    protected GuidedDecisionTable52 dtable;

    @Before
    public void setup() {
        this.dtable = new GuidedDecisionTable52();
    }

    protected Pattern52 whenThereIsAPattern(final String factType,
                                            final String binding) {
        final Pattern52 p = new Pattern52();
        p.setFactType(factType);
        p.setBoundName(binding);
        dtable.getConditions().add(p);
        return p;
    }

    protected BRLConditionColumn whenThereIsABRLFactPattern(final String factType,
                                                            final String binding) {
        final FactPattern fp = new FactPattern(factType);
        fp.setBoundName(binding);
        final BRLConditionColumn brl = new BRLConditionColumn();
        brl.getDefinition().add(fp);
        dtable.getConditions().add(brl);
        return brl;
    }

    protected BRLConditionColumn whenThereIsABRLFromCompositeFactPattern(final String factType,
                                                                         final String binding) {
        final FromCompositeFactPattern fcfp = new FromCompositeFactPattern();
        final FactPattern fp = new FactPattern(factType);
        fp.setBoundName(binding);
        fcfp.setFactPattern(fp);
        final BRLConditionColumn brl = new BRLConditionColumn();
        brl.getDefinition().add(fcfp);
        dtable.getConditions().add(brl);
        return brl;
    }

    protected SingleFieldConstraint whenBRLFactPatternHasAField(final BRLConditionColumn brl,
                                                                final String fieldName,
                                                                final String fieldType,
                                                                final String fieldBinding) {
        assertFalse("BRLConditionColumn has not been initialised. Was 'whenThereIsABRLFactPattern' called?",
                    brl.getDefinition().isEmpty());
        assertEquals("BRLConditionColumn has not been initialised correctly. Was 'whenThereIsABRLFactPattern' called?",
                     1,
                     brl.getDefinition().size());
        assertTrue("BRLConditionColumn has not been initialised correctly. Was 'whenThereIsABRLFactPattern' called?",
                   brl.getDefinition().get(0) instanceof FactPattern);

        final FactPattern fp = (FactPattern) brl.getDefinition().get(0);
        final SingleFieldConstraint sfc = new SingleFieldConstraint();
        sfc.setFactType(fp.getFactType());
        sfc.setFieldName(fieldName);
        sfc.setFieldType(fieldType);
        sfc.setFieldBinding(fieldBinding);
        fp.addConstraint(sfc);
        brl.getDefinition().add(fp);
        return sfc;
    }

    protected SingleFieldConstraint whenBRLFromCompositeFactPatternHasAField(final BRLConditionColumn brl,
                                                                             final String fieldName,
                                                                             final String fieldType,
                                                                             final String fieldBinding) {
        assertFalse("BRLConditionColumn has not been initialised. Was 'whenThereIsABRLFactPattern' called?",
                    brl.getDefinition().isEmpty());
        assertEquals("BRLConditionColumn has not been initialised correctly. Was 'whenThereIsABRLFactPattern' called?",
                     1,
                     brl.getDefinition().size());
        assertTrue("BRLConditionColumn has not been initialised correctly. Was 'whenThereIsABRLFactPattern' called?",
                   brl.getDefinition().get(0) instanceof FromCompositeFactPattern);

        final FromCompositeFactPattern fcfp = (FromCompositeFactPattern) brl.getDefinition().get(0);
        final FactPattern fp = fcfp.getFactPattern();
        final SingleFieldConstraint sfc = new SingleFieldConstraint();
        sfc.setFactType(fp.getFactType());
        sfc.setFieldName(fieldName);
        sfc.setFieldType(fieldType);
        sfc.setFieldBinding(fieldBinding);
        fp.addConstraint(sfc);
        brl.getDefinition().add(fp);
        return sfc;
    }

    protected void assertThereIsNoPatternFor(final String binding) {
        final Pattern52 p = dtable.getConditionPattern(binding);
        assertNull(p);
    }

    protected void assertThereIsAPatternFor(final String factType,
                                            final String binding) {
        final Pattern52 result = dtable.getConditionPattern(binding);
        assertNotNull(result);
        assertEquals(factType,
                     result.getFactType());
        assertEquals(binding,
                     result.getBoundName());
    }
}
