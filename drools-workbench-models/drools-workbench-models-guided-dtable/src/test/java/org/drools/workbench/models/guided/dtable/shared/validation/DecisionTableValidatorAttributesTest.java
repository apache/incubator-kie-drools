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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class DecisionTableValidatorAttributesTest {

    private final String attributeName;
    private GuidedDecisionTable52 table;

    public DecisionTableValidatorAttributesTest( final String attributeName ) {
        this.attributeName = attributeName;
    }

    @Parameterized.Parameters
    public static Collection<String> attributes() {
        return Stream.of(Attribute.values()).map(Attribute::getAttributeName).collect(Collectors.toList());
    }

    @Test(expected = DuplicateAttributeException.class)
    public void addAttributeColumn() throws
                                     Exception {
        final DecisionTableValidator validator = getDecisionTableValidator();

        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute(attributeName);

        try {
            validator.isValidToAdd( attributeCol52 );
        } catch ( final Exception e ) {
            fail( "First addition should be valid." );
        }

        addAttributeCol( attributeCol52 );

        validator.isValidToAdd( attributeCol52 );
    }

    private DecisionTableValidator getDecisionTableValidator() {
        table = mock( GuidedDecisionTable52.class );
        when( table.getHitPolicy() ).thenReturn( GuidedDecisionTable52.HitPolicy.NONE );
        return new DecisionTableValidator( table );
    }

    private void addAttributeCol( final AttributeCol52 attributeCol52 ) {
        final ArrayList<AttributeCol52> attributeCol52s = new ArrayList<>();
        when( table.getAttributeCols() ).thenReturn( attributeCol52s );
        attributeCol52s.add( attributeCol52 );
    }
}