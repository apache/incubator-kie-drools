/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.rule;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RuleModelTest {

    private RuleModel model;

    @Before
    public void setup() {
        this.model = new RuleModel();

        final FactPattern fp1 = new FactPattern();
        fp1.setBoundName("$p1");
        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding("$sfc1");
        fp1.addConstraint(sfc1);

        final SingleFieldConstraintEBLeftSide sfc2 = new SingleFieldConstraintEBLeftSide();
        sfc2.setFieldBinding("$sfc2");
        fp1.addConstraint(sfc2);

        final FromCompositeFactPattern fcfp = new FromCompositeFactPattern();
        final FactPattern fp2 = new FactPattern();
        fp2.setBoundName("$p2");
        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFieldBinding("$sfc3");
        fp2.addConstraint(sfc3);
        fcfp.setFactPattern(fp2);

        model.addLhsItem(fp1);
        model.addLhsItem(fcfp);
    }

    @Test
    public void checkGetAllLHSVariables() {
        final List<String> allLHSVariables = model.getAllLHSVariables();
        assertEquals(5,
                     allLHSVariables.size());
        assertTrue(allLHSVariables.contains("$p1"));
        assertTrue(allLHSVariables.contains("$p2"));
        assertTrue(allLHSVariables.contains("$sfc1"));
        assertTrue(allLHSVariables.contains("$sfc2"));
        assertTrue(allLHSVariables.contains("$sfc3"));
    }

    @Test
    public void checkGetLHSPatternVariables() {
        final List<String> allLHSVariables = model.getLHSPatternVariables();
        assertEquals(2,
                     allLHSVariables.size());
        assertTrue(allLHSVariables.contains("$p1"));
        assertTrue(allLHSVariables.contains("$p2"));
    }

    @Test
    public void checkGetLHSFieldVariables() {
        final List<String> allLHSVariables = model.getLHSVariables(false,
                                                                   true);
        assertEquals(3,
                     allLHSVariables.size());
        assertTrue(allLHSVariables.contains("$sfc1"));
        assertTrue(allLHSVariables.contains("$sfc2"));
        assertTrue(allLHSVariables.contains("$sfc3"));
    }

    @Test
    public void testEmptyCompositeFactPatternIsIgnored_getLHSBoundField() {
        model.addLhsItem(new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_NOT));

        final SingleFieldConstraint lhsBoundField = model.getLHSBoundField("$sfc1");
        assertNotNull(lhsBoundField);
    }

    @Test
    public void testEmptyCompositeFactPatternIsIgnored_getBoundVariablesInScope() {
        model.addLhsItem(new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_NOT));

        final List<String> boundVariablesInScope = model.getBoundVariablesInScope(new BaseSingleFieldConstraint());
        assertNotNull(boundVariablesInScope);
    }

    @Test
    public void testEmptyCompositeFactPatternIsIgnored_getLHSBindingType() {
        final FactPattern fp3 = new FactPattern();
        fp3.setBoundName("$p3");
        final SingleFieldConstraint sfc4 = new SingleFieldConstraint();
        sfc4.setFieldBinding("$sfc4");
        fp3.addConstraint(sfc4);

        model.addLhsItem(new CompositeFactPattern(CompositeFactPattern.COMPOSITE_TYPE_NOT));
        model.addLhsItem(fp3);

        final String type = model.getLHSBindingType("$sfc4");
        assertNotNull(type);
    }
}
