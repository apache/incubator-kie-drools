/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.rule;

import org.junit.Test;

import static org.junit.Assert.*;

public class SingleFieldConstraintEBLeftSideTests {

    @Test
    public void testHashCode() {
        final FactPattern fp = new FactPattern();
        fp.setFactType( "Applicant" );
        fp.setBoundName( "$a" );
        final SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_UNDEFINED );
        fp.addConstraint( con );
        con.setExpressionLeftSide( new ExpressionFormLine( new ExpressionUnboundFact( fp.getFactType() ) ) );
        assertNotEquals( 0,
                         con.hashCode() );
    }

    @Test
    public void testEquals() {
        final FactPattern fp = new FactPattern();
        fp.setFactType( "Applicant" );
        fp.setBoundName( "$a" );
        final SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_UNDEFINED );
        fp.addConstraint( con );
        con.setExpressionLeftSide( new ExpressionFormLine( new ExpressionUnboundFact( fp.getFactType() ) ) );
        assertEquals( con,
                      con );
    }

}
