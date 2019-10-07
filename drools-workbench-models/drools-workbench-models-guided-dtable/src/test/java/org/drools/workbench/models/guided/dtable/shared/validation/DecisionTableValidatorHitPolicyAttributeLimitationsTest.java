/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.validation;

import java.util.Arrays;
import java.util.Collection;

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.drools.workbench.models.datamodel.rule.Attribute.ACTIVATION_GROUP;
import static org.drools.workbench.models.datamodel.rule.Attribute.SALIENCE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class DecisionTableValidatorHitPolicyAttributeLimitationsTest {

    private final GuidedDecisionTable52.HitPolicy hitPolicy;
    private final String attribute;
    private final boolean isAllowed;

    public DecisionTableValidatorHitPolicyAttributeLimitationsTest( final GuidedDecisionTable52.HitPolicy hitPolicy,
                                                                    final String attribute,
                                                                    final boolean isAllowed ) {
        this.hitPolicy = hitPolicy;
        this.attribute = attribute;
        this.isAllowed = isAllowed;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> attributes() {
        return Arrays.asList( new Object[][]{
                {GuidedDecisionTable52.HitPolicy.NONE, SALIENCE.getAttributeName(), true},
                {GuidedDecisionTable52.HitPolicy.FIRST_HIT, SALIENCE.getAttributeName(), false},
                {GuidedDecisionTable52.HitPolicy.RULE_ORDER, SALIENCE.getAttributeName(), false},
                {GuidedDecisionTable52.HitPolicy.UNIQUE_HIT, SALIENCE.getAttributeName(), true},
                {GuidedDecisionTable52.HitPolicy.NONE, ACTIVATION_GROUP.getAttributeName(), true},
                {GuidedDecisionTable52.HitPolicy.FIRST_HIT, ACTIVATION_GROUP.getAttributeName(), false},
                {GuidedDecisionTable52.HitPolicy.RULE_ORDER, ACTIVATION_GROUP.getAttributeName(), true},
                {GuidedDecisionTable52.HitPolicy.UNIQUE_HIT, ACTIVATION_GROUP.getAttributeName(), false}
        } );
    }

    @Test
    public void addAttributeColumn() throws
                                     Exception {
        final GuidedDecisionTable52 table = mock( GuidedDecisionTable52.class );
        when( table.getHitPolicy() ).thenReturn( hitPolicy );
        final DecisionTableValidator validator = new DecisionTableValidator( table );

        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute( attribute );

        boolean wasAllowed = true;
        try {
            validator.isValidToAdd( attributeCol52 );
        } catch ( final Exception e ) {
            wasAllowed = false;
        }

        assertEquals( wasAllowed,
                      isAllowed );

    }
}