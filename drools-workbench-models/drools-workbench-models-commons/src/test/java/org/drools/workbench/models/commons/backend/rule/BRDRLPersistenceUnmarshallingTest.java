/*
 * Copyright 2013 JBoss Inc
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

package org.drools.workbench.models.commons.backend.rule;

import org.drools.workbench.models.commons.shared.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.commons.shared.rule.CompositeFieldConstraint;
import org.drools.workbench.models.commons.shared.rule.FactPattern;
import org.drools.workbench.models.commons.shared.rule.IPattern;
import org.drools.workbench.models.commons.shared.rule.RuleModel;
import org.drools.workbench.models.commons.shared.rule.SingleFieldConstraint;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.*;

public class BRDRLPersistenceUnmarshallingTest {

    @Test
    public void testFactPattern() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant()\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );
    }

    @Test
    public void testFactPatternWithBinding() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$a : Applicant()\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );
        assertEquals( "$a",
                      fp.getBoundName() );

        assertEquals( 0,
                      fp.getNumberOfConstraints() );
    }

    @Test
    public void testSingleFieldConstraint() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age < 55 )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "<",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintWithBinding() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( $a : age < 55 )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "<",
                      sfp.getOperator() );
        assertEquals( "55",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
        assertEquals( "$a",
                      sfp.getFieldBinding() );
    }

    @Test
    public void testCompositeFieldConstraint() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age < 55 || age > 70 )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof CompositeFieldConstraint );

        CompositeFieldConstraint cfp = (CompositeFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "||",
                      cfp.getCompositeJunctionType() );
        assertEquals( 2,
                      cfp.getNumberOfConstraints() );
        assertTrue( cfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        assertTrue( cfp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) cfp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp1.getFactType() );
        assertEquals( "age",
                      sfp1.getFieldName() );
        assertEquals( "<",
                      sfp1.getOperator() );
        assertEquals( "55",
                      sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp1.getConstraintValueType() );

        SingleFieldConstraint sfp2 = (SingleFieldConstraint) cfp.getConstraint( 1 );
        assertEquals( "Applicant",
                      sfp2.getFactType() );
        assertEquals( "age",
                      sfp2.getFieldName() );
        assertEquals( ">",
                      sfp2.getOperator() );
        assertEquals( "70",
                      sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp2.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintIsNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age == null )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "== null",
                      sfp.getOperator() );
        assertNull( sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintIsNotNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age != null )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "age",
                      sfp.getFieldName() );
        assertEquals( "!= null",
                      sfp.getOperator() );
        assertNull( sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testCompositeFieldConstraintWithNotNullOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( age != null && age > 70 )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getConstraintList().getConstraints().length );
        assertTrue( fp.getConstraint( 0 ) instanceof CompositeFieldConstraint );

        CompositeFieldConstraint cfp = (CompositeFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "&&",
                      cfp.getCompositeJunctionType() );
        assertEquals( 2,
                      cfp.getNumberOfConstraints() );
        assertTrue( cfp.getConstraint( 0 ) instanceof SingleFieldConstraint );
        assertTrue( cfp.getConstraint( 1 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp1 = (SingleFieldConstraint) cfp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp1.getFactType() );
        assertEquals( "age",
                      sfp1.getFieldName() );
        assertEquals( "!= null",
                      sfp1.getOperator() );
        assertNull( sfp1.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_UNDEFINED,
                      sfp1.getConstraintValueType() );

        SingleFieldConstraint sfp2 = (SingleFieldConstraint) cfp.getConstraint( 1 );
        assertEquals( "Applicant",
                      sfp2.getFactType() );
        assertEquals( "age",
                      sfp2.getFieldName() );
        assertEquals( ">",
                      sfp2.getOperator() );
        assertEquals( "70",
                      sfp2.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp2.getConstraintValueType() );
    }

    @Test
    @Ignore("Unmarshalling of CEP is broken")
    public void testSingleFieldConstraintCEPOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "Applicant( dob after \"26-Jun-2013\" )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 1,
                      m.lhs.length );
        IPattern p = m.lhs[ 0 ];
        assertTrue( p instanceof FactPattern );

        FactPattern fp = (FactPattern) p;
        assertEquals( "Applicant",
                      fp.getFactType() );

        assertEquals( 1,
                      fp.getNumberOfConstraints() );
        assertTrue( fp.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "dob",
                      sfp.getFieldName() );
        assertEquals( "after",
                      sfp.getOperator() );
        assertEquals( "26-Jun-2013",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      sfp.getConstraintValueType() );
    }

    @Test
    public void testSingleFieldConstraintContainsOperator() {
        String drl = "rule \"rule1\"\n"
                + "when\n"
                + "$is : IncomeSource( )\n"
                + "Applicant( incomes contains $is )\n"
                + "then\n"
                + "end";

        RuleModel m = BRDRLPersistence.getInstance().unmarshal( drl );

        assertNotNull( m );
        assertEquals( "rule1",
                      m.name );

        assertEquals( 2,
                      m.lhs.length );
        IPattern p0 = m.lhs[ 0 ];
        assertTrue( p0 instanceof FactPattern );

        FactPattern fp0 = (FactPattern) p0;
        assertEquals( "IncomeSource",
                      fp0.getFactType() );
        assertEquals( "$is",
                      fp0.getBoundName() );

        IPattern p1 = m.lhs[ 1 ];
        assertTrue( p1 instanceof FactPattern );

        FactPattern fp1 = (FactPattern) p1;
        assertEquals( "Applicant",
                      fp1.getFactType() );

        assertEquals( 0,
                      fp0.getNumberOfConstraints() );

        assertEquals( 1,
                      fp1.getNumberOfConstraints() );
        assertTrue( fp1.getConstraint( 0 ) instanceof SingleFieldConstraint );

        SingleFieldConstraint sfp = (SingleFieldConstraint) fp1.getConstraint( 0 );
        assertEquals( "Applicant",
                      sfp.getFactType() );
        assertEquals( "incomes",
                      sfp.getFieldName() );
        assertEquals( "contains",
                      sfp.getOperator() );
        assertEquals( "$is",
                      sfp.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_VARIABLE,
                      sfp.getConstraintValueType() );
    }

}
