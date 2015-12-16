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

package org.drools.workbench.models.commons.backend.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionExecuteWorkItem;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionGlobalCollectionAdd;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.ActionWorkItemFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.models.datamodel.workitems.PortableBooleanParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableIntegerParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleModelDRLPersistenceTest {

    private RuleModelPersistence ruleModelPersistence;

    @Before
    public void setUp() throws Exception {
        ruleModelPersistence = RuleModelDRLPersistenceImpl.getInstance();
    }

    @Test
    public void testGenerateEmptyDRL() {
        String expected = "rule \"null\"\n\tdialect \"mvel\"\n\twhen\n\tthen\nend\n";

        checkMarshalling( expected,
                          new RuleModel() );
    }

    private void checkMarshalling( String expected,
                                   RuleModel m ) {
        String drl = ruleModelPersistence.marshal( m );
        assertNotNull( drl );
        if ( expected != null ) {
            assertEqualsIgnoreWhitespace( expected,
                                          drl );
        }
    }

    private void checkMarshallingUsingDsl( String expected,
                                           RuleModel m ) {
        String drl = ruleModelPersistence.marshal( m );
        assertNotNull( drl );
        if ( expected != null ) {
            assertEqualsIgnoreWhitespace( expected,
                                          drl );
        }
    }

    @Test
    public void testFreeForm() {
        RuleModel m = new RuleModel();
        m.name = "with composite";
        m.lhs = new IPattern[ 1 ];
        m.rhs = new IAction[ 1 ];

        FreeFormLine fl = new FreeFormLine();
        fl.setText( "Person()" );
        m.lhs[ 0 ] = fl;

        FreeFormLine fr = new FreeFormLine();
        fr.setText( "fun()" );
        m.rhs[ 0 ] = fr;

        String drl = ruleModelPersistence.marshal( m );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "Person()" ) > 0 );
        assertTrue( drl.indexOf( "fun()" ) > drl.indexOf( "Person()" ) );
    }

    @Test
    public void testBasics() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                + "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\nend\n";
        RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testInsertLogical() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                + "\t\tAccident( )\n\tthen\n\t\tinsertLogical( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertLogicalFact( "Report" ) );

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testAttr() {
        RuleModel m = new RuleModel();
        m.attributes = new RuleAttribute[ 1 ];
        m.attributes[ 0 ] = new RuleAttribute( "enabled",
                                               "true" );
        final String drl = ruleModelPersistence.marshal( m );

        assertTrue( drl.indexOf( "enabled true" ) > 0 );

    }

    @Test
    public void testCalendars() {
        //BZ1059232 - Guided rule editor: calendars attribute is broken when a list of calendars is used
        RuleModel m = new RuleModel();
        m.attributes = new RuleAttribute[ 1 ];
        m.attributes[ 0 ] = new RuleAttribute( "calendars",
                                               "a, b" );
        final String drl = ruleModelPersistence.marshal( m );

        assertTrue( drl.indexOf( "calendars \"a\", \"b\"" ) > 0 );
    }

    @Test
    public void testCalendarsWithQuotes() {
        //BZ1059232 - Guided rule editor: calendars attribute is broken when a list of calendars is used
        RuleModel m = new RuleModel();
        m.attributes = new RuleAttribute[ 1 ];
        m.attributes[ 0 ] = new RuleAttribute( "calendars",
                                               "\"a\", \"b\"" );
        final String drl = ruleModelPersistence.marshal( m );

        assertTrue( drl.indexOf( "calendars \"a\", \"b\"" ) > 0 );
    }

    @Test
    public void testCalendarsWithQuotesAroundWholeValue() {
        //BZ1059232 - Guided rule editor: calendars attribute is broken when a list of calendars is used
        RuleModel m = new RuleModel();
        m.attributes = new RuleAttribute[ 1 ];
        m.attributes[ 0 ] = new RuleAttribute( "calendars",
                                               "\"a, b\"" );
        final String drl = ruleModelPersistence.marshal( m );

        assertTrue( drl.indexOf( "calendars \"a\", \"b\"" ) > 0 );
    }

    @Test
    public void testEnumNoType() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == \"CheeseType.CHEDDAR\" )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "CheeseType.CHEDDAR" );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeString() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == \"CHEDDAR\" )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "CHEDDAR" );
        con.setFieldType( DataType.TYPE_STRING );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testExtends() {
        String expected = "rule \"my rule\" extends \"secondRule\"\n\tdialect \"mvel\"\n\twhen\n"
                + "\tthen\n\nend\n";
        final RuleModel m = new RuleModel();
        m.parentName = "secondRule";

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testSumAsGivenValue() {
        // BZ-1013682
        String expected = "" +
                "rule \"my rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    m:Message()\n" +
                "  then\n" +
                "    modify( m ) {\n" +
                "      setText( \"Hello \" + \"world\" )\n" +
                "    }\n" +
                "end\n";
        final RuleModel m = new RuleModel();

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "Message" );
        factPattern.setBoundName( "m" );
        m.lhs = new IPattern[]{ factPattern };

        ActionUpdateField actionUpdateField = new ActionUpdateField();
        actionUpdateField.setVariable( "m" );
        ActionFieldValue actionFieldValue = new ActionFieldValue();
        actionFieldValue.setField( "text" );
        actionFieldValue.setType( "String" );
        actionFieldValue.setNature( FieldNatureType.TYPE_FORMULA );
        actionFieldValue.setValue( "\"Hello \" + \"world\"" );
        actionUpdateField.setFieldValues( new ActionFieldValue[]{ actionFieldValue } );
        m.rhs = new IAction[]{ actionUpdateField };

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testNotNull() {
        String expected = "" +
                "rule \"my rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Customer( contact != null , contact.tel1 > 15 )\n" +
                "  then\n" +
                "end\n";

        final RuleModel m = new RuleModel();

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "Customer" );
        m.lhs = new IPattern[]{ factPattern };

        SingleFieldConstraint constraint1 = new SingleFieldConstraint( "Customer", "contact", "Contact", null );
        constraint1.setOperator( "!= null" );
        factPattern.addConstraint( constraint1 );

        SingleFieldConstraint constraint2 = new SingleFieldConstraint( "Customer", "contact", "Contact", null );
        factPattern.addConstraint( constraint2 );

        SingleFieldConstraint constraint3 = new SingleFieldConstraint( "Contact", "tel1", "Integer", constraint2 );
        constraint3.setOperator( ">" );
        constraint3.setValue( "15" );
        factPattern.addConstraint( constraint3 );

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCallFunction() throws Exception {
        String expected = "" +
                "package org.mortgages;\n" +
                "import org.mortgages.LoanApplication;\n" +
                "\n" +
                "rule \"my rule\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    a : LoanApplication( )\n" +
                "  then\n" +
                "    keke.clear(  );\n" +
                "end\n";

        final RuleModel m = new RuleModel();
        m.setPackageName( "org.mortgages" );
        m.getImports().addImport( new Import( "org.mortgages.LoanApplication" ) );
        m.name = "my rule";

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "LoanApplication" );
        factPattern.setBoundName( "a" );
        m.lhs = new IPattern[]{ factPattern };

        ActionCallMethod actionCallMethod = new ActionCallMethod();
        actionCallMethod.setState( 1 );
        actionCallMethod.setMethodName( "clear" );
        actionCallMethod.setVariable( "keke" );
        m.rhs = new IAction[]{ actionCallMethod };

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeNumeric() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( age == 100 )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "==" );
        con.setValue( "100" );
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeStringInOperator() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n"
                + "\tdialect \"mvel\"\n"
                + "\twhen\n"
                + "\t\tCheese( type in ( \"CHEDDAR\", \"STILTON\" ) )\n"
                + "\tthen\n"
                + "\t\tinsert( new Report() );\n"
                + "end\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "in" );
        con.setValue( "( \"CHEDDAR\",\"STILTON\" )" );
        con.setFieldType( DataType.TYPE_STRING );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeBoolean() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( smelly == true )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "smelly" );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setFieldType( DataType.TYPE_BOOLEAN );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeDate() {
        //A legacy "Guvnor" enums (i.e pick-list of underlying field data-type)
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( dateMade == \"31-Jan-2010\" )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "dateMade" );
        con.setOperator( "==" );
        con.setValue( "31-Jan-2010" );
        con.setFieldType( DataType.TYPE_DATE );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEnumTypeComparable() {
        //Java 1.5+ "true" enums are of type Comparable
        String expected = "rule \"my rule\"\n\tdialect \"mvel\"\n\twhen\n\t\tCheese( type == Cheese.CHEDDAR )\n"
                + "\tthen\n\t\tinsert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        final FactPattern pat = new FactPattern( "Cheese" );

        m.addLhsItem( pat );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "type" );
        con.setOperator( "==" );
        con.setValue( "Cheese.CHEDDAR" );
        con.setFieldType( DataType.TYPE_COMPARABLE );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_ENUM );
        pat.addConstraint( con );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testMoreComplexRendering() {
        final RuleModel m = getComplexModel( false );
        String expected = "rule \"Complex Rule\"\n" +
                "no-loop true\n" +
                "salience -10\n" +
                "agenda-group \"aGroup\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  p1 : Person( f1 : age < 42 )\n" +
                "  not (Cancel( )) \n" +
                "then\n" +
                "  modify( p1 ) {\n" +
                "    setStatus( \"rejected\" ),\n" +
                "    setName( \"Fred\" )\n" +
                "  }\n" +
                "  retract( p1 );\n" +
                "end\n";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testMoreComplexRenderingWithDsl() {
        final RuleModel m = getComplexModel( true );
        String expected = "rule \"Complex Rule\"\n" +
                "no-loop true\n" +
                "salience -10\n" +
                "agenda-group \"aGroup\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  >p1 : Person( f1 : age < 42 )\n" +
                "  >not (Cancel( )) \n" +
                "then\n" +
                "  >modify( p1 ) {\n" +
                "    >setStatus( \"rejected\" ),\n" +
                "    >setName( \"Fred\" )\n" +
                "  >}\n" +
                "  >retract( p1 );\n" +
                "Send an email to administrator\n" +
                "end\n";

        checkMarshallingUsingDsl( expected,
                                  m );

        String drl = ruleModelPersistence.marshal( m );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );

        String dslFile = "[then]Send an email to {administrator}=sendMailTo({administrator});";

        RuleModel unmarshalledModel = ruleModelPersistence.unmarshalUsingDSL( drl,
                                                                              null,
                                                                              mock( PackageDataModelOracle.class ),
                                                                              dslFile );

        IAction[] actions = unmarshalledModel.rhs;
        DSLSentence dslSentence = (DSLSentence) actions[ actions.length - 1 ];
        assertEquals( "Send an email to {administrator}", dslSentence.getDefinition() );

        checkMarshallingUsingDsl( expected,
                                  unmarshalledModel );
    }

    @Test
    public void testDSLExpansion() {
        String expected =
                "rule \"Rule With DSL\"\n" +
                        "\tdialect \"mvel\"\n" +
                        "\twhen\n" +
                        "\t\tThe credit rating is AA\n" +
                        "\tthen\n" +
                        "end\n";
        final String dslDefinition = "The credit rating is {rating:ENUM:Applicant.creditRating}";

        final DSLSentence dsl = new DSLSentence();
        dsl.setDefinition( dslDefinition );

        //Check values are correctly parsed
        final List<DSLVariableValue> values = dsl.getValues();
        assertEquals( 1,
                      values.size() );
        assertTrue( values.get( 0 ) instanceof DSLComplexVariableValue );
        assertEquals( "rating",
                      values.get( 0 ).getValue() );
        assertEquals( "ENUM:Applicant.creditRating",
                      ( (DSLComplexVariableValue) values.get( 0 ) ).getId() );

        //The following line is normally performed by the UI when the user sets values
        dsl.getValues().get( 0 ).setValue( "AA" );

        //Check interpolation
        final String expansion = dsl.interpolate();

        assertEquals( "The credit rating is AA",
                      expansion );
        assertEquals( dsl.getDefinition(),
                      dslDefinition );

        final RuleModel m = new RuleModel();
        m.name = "Rule With DSL";
        m.addLhsItem( dsl );

        String drl = ruleModelPersistence.marshal( m );
        assertEqualsIgnoreWhitespace( expected, drl );

        String dslFile = "[when]" + dslDefinition + "=Credit( rating == {rating} )";

        RuleModel unmarshalledModel = ruleModelPersistence.unmarshalUsingDSL( drl,
                                                                              null, null,
                                                                              dslFile );

        DSLSentence dslSentence = (DSLSentence) unmarshalledModel.lhs[ 0 ];
        assertEquals( dslDefinition,
                      dslSentence.getDefinition() );
        assertEquals( 1,
                      dslSentence.getValues().size() );
        assertTrue( dslSentence.getValues().get( 0 ) instanceof DSLComplexVariableValue );
        DSLComplexVariableValue dslComplexVariableValue = (DSLComplexVariableValue) dslSentence.getValues().get( 0 );
        assertEquals( "AA",
                      dslComplexVariableValue.getValue() );
        assertEquals( "ENUM:Applicant.creditRating",
                      dslComplexVariableValue.getId() );

        assertEqualsIgnoreWhitespace( expected,
                                      ruleModelPersistence.marshal( unmarshalledModel ) );
    }

    @Test
    public void testDSLExpansionContainingRegex() {
        String expected =
                "rule \"RegexDslRule\"\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "When the ages is less than  57\n" +
                        "then\n" +
                        "end\n";
        final String dslDefinition = "When the ages is less than {num:1?[0-9]?[0-9]}";

        final DSLSentence dsl = new DSLSentence();
        dsl.setDefinition( dslDefinition );

        //Check values are correctly parsed
        final List<DSLVariableValue> values = dsl.getValues();
        assertEquals( 1,
                      values.size() );
        assertTrue( values.get( 0 ) instanceof DSLComplexVariableValue );
        assertEquals( "num",
                      values.get( 0 ).getValue() );
        assertEquals( "1?[0-9]?[0-9]",
                      ( (DSLComplexVariableValue) values.get( 0 ) ).getId() );

        //The following line is normally performed by the UI when the user sets values
        dsl.getValues().get( 0 ).setValue( "57" );

        //Check interpolation
        final String expansion = dsl.interpolate();

        assertEquals( "When the ages is less than 57",
                      expansion );
        assertEquals( dsl.getDefinition(),
                      dslDefinition );

        final RuleModel m = new RuleModel();
        m.name = "RegexDslRule";
        m.addLhsItem( dsl );

        String drl = ruleModelPersistence.marshal( m );
        assertEqualsIgnoreWhitespace( expected,
                                      drl );

        String dslFile = "[when]" + dslDefinition + "=applicant:Applicant(age<{num})";

        RuleModel model = ruleModelPersistence.unmarshalUsingDSL( drl,
                                                                  null, null,
                                                                  dslFile );

        DSLSentence dslSentence = (DSLSentence) model.lhs[ 0 ];
        assertEquals( dslDefinition,
                      dslSentence.getDefinition() );
        assertEquals( 1,
                      dslSentence.getValues().size() );
        assertTrue( dslSentence.getValues().get( 0 ) instanceof DSLComplexVariableValue );
        DSLComplexVariableValue dslComplexVariableValue = (DSLComplexVariableValue) dslSentence.getValues().get( 0 );
        assertEquals( "57",
                      dslComplexVariableValue.getValue() );
        assertEquals( "1?[0-9]?[0-9]",
                      dslComplexVariableValue.getId() );

        assertEqualsIgnoreWhitespace( drl,
                                      ruleModelPersistence.marshal( model ) );
    }

    @Test
    public void testDSLExpansionLHS() {
        final String dslDefinition = "The credit rating is {rating:ENUM:Applicant.creditRating}";
        final String drlExpected =
                "rule \"r1\"\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "The credit rating is AA\n" +
                        "then\n" +
                        "end";

        final DSLSentence dsl = new DSLSentence();
        dsl.setDefinition( dslDefinition );
        //The following line is normally performed by the UI when the user sets values
        dsl.getValues().get( 0 ).setValue( "AA" );

        //Append DSL to RuleModel to check marshalling
        final RuleModel m = new RuleModel();
        m.name = "r1";
        m.addLhsItem( dsl );

        final String drlActual = ruleModelPersistence.marshal( m );
        assertEqualsIgnoreWhitespace( drlExpected,
                                      drlActual );
    }

    private RuleModel getComplexModel( boolean useDsl ) {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "salience",
                                           "-10" ) );
        m.addAttribute( new RuleAttribute( "agenda-group",
                                           "aGroup" ) );

        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        con.setOperator( "<" );
        con.setValue( "42" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        final CompositeFactPattern comp = new CompositeFactPattern( "not" );
        comp.addFactPattern( new FactPattern( "Cancel" ) );
        m.addLhsItem( comp );

        final ActionUpdateField upd1 = new ActionUpdateField();
        upd1.setVariable( "p1" );
        upd1.addFieldValue( new ActionFieldValue( "status",
                                                  "rejected",
                                                  DataType.TYPE_STRING ) );
        upd1.addFieldValue( new ActionFieldValue( "name",
                                                  "Fred",
                                                  DataType.TYPE_STRING ) );
        m.addRhsItem( upd1 );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        if ( useDsl ) {
            final DSLSentence sen = new DSLSentence();
            sen.setDefinition( "Send an email to {administrator}" );
            m.addRhsItem( sen );
        }

        return m;
    }

    @Test
    public void testFieldBindingWithNoConstraints() {
        // to satisfy JBRULES-850
        RuleModel m = getModelWithNoConstraints();
        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        // System.out.println(s);
        assertTrue( s.contains( "Person( f1 : age)" ) );

        checkMarshalling( s,
                          m );
    }

    @Test
    public void textIsNullOperator() {
        final RuleModel m = new RuleModel();
        m.name = "IsNullOperator";
        final FactPattern pat = new FactPattern( "Person" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "== null" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( s.indexOf( "Person( age == null )" ) != -1 );
        checkMarshalling( s,
                          m );
    }

    @Test
    public void textIsNotNullOperator() {
        final RuleModel m = new RuleModel();
        m.name = "IsNotNullOperator";
        final FactPattern pat = new FactPattern( "Person" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "age" );
        con.setOperator( "!= null" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( s.indexOf( "Person( age != null )" ) != -1 );
        checkMarshalling( s,
                          m );
    }

    private RuleModel getModelWithNoConstraints() {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";
        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        // con.operator = "<";
        // con.value = "42";
        pat.addConstraint( con );

        m.addLhsItem( pat );

        return m;
    }

    @Test
    public void testOrComposite() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern(
                CompositeFactPattern.COMPOSITE_TYPE_OR );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        FactPattern p2 = new FactPattern( "Person" );
        SingleFieldConstraint sf2 = new SingleFieldConstraint( "age" );
        sf2.setOperator( "==" );
        sf2.setValue( "43" );
        p2.addConstraint( sf2 );

        cp.addFactPattern( p2 );

        m.addLhsItem( cp );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( result.indexOf( "( Person( age == 42 ) or Person( age == 43 ) )" ) > 0 );

        checkMarshalling( result,
                          m );
    }

    @Test
    public void testExistsMultiPatterns() throws Exception {
        RuleModel m = getCompositeFOL( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( result.indexOf( "exists (Person( age == 42 ) and Person( age == 43 ))" ) > 0 );
        checkMarshalling( result,
                          m );
    }

    @Test
    public void testNotMultiPatterns() throws Exception {
        RuleModel m = getCompositeFOL( CompositeFactPattern.COMPOSITE_TYPE_NOT );
        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( result.indexOf( "not (Person( age == 42 ) and Person( age == 43 ))" ) > 0 );
        checkMarshalling( result,
                          m );
    }

    @Test
    public void testSingleExists() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_EXISTS );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        m.addLhsItem( cp );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        System.out.println( result );

        assertTrue( result.indexOf( "exists (Person( age == 42 )) " ) > 0 );
        checkMarshalling( result,
                          m );
    }

    private RuleModel getCompositeFOL( String type ) {
        RuleModel m = new RuleModel();
        m.name = "or";
        CompositeFactPattern cp = new CompositeFactPattern( type );
        FactPattern p1 = new FactPattern( "Person" );
        SingleFieldConstraint sf1 = new SingleFieldConstraint( "age" );
        sf1.setOperator( "==" );
        sf1.setValue( "42" );
        p1.addConstraint( sf1 );

        cp.addFactPattern( p1 );

        FactPattern p2 = new FactPattern( "Person" );
        SingleFieldConstraint sf2 = new SingleFieldConstraint( "age" );
        sf2.setOperator( "==" );
        sf2.setValue( "43" );
        p2.addConstraint( sf2 );

        cp.addFactPattern( p2 );

        m.addLhsItem( cp );

        return m;
    }

    @Test
    public void testCompositeConstraints() {
        RuleModel m = new RuleModel();
        m.name = "with composite";

        FactPattern p1 = new FactPattern( "Person" );
        p1.setBoundName( "p1" );
        m.addLhsItem( p1 );

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "goo" );
        X.setFieldType( DataType.TYPE_STRING );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        X.setValue( "foo" );
        X.setOperator( "==" );
        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = new ConnectiveConstraint();
        X.getConnectives()[ 0 ].setConstraintValueType( ConnectiveConstraint.TYPE_LITERAL );
        X.getConnectives()[ 0 ].setFieldType( DataType.TYPE_STRING );
        X.getConnectives()[ 0 ].setOperator( "|| ==" );
        X.getConnectives()[ 0 ].setValue( "bar" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "goo2" );
        Y.setFieldType( DataType.TYPE_STRING );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Y.setValue( "foo" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldType( DataType.TYPE_STRING );
        Q1.setFieldName( "goo" );
        Q1.setOperator( "==" );
        Q1.setValue( "whee" );
        Q1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q1 );

        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldType( DataType.TYPE_STRING );
        Q2.setFieldName( "gabba" );
        Q2.setOperator( "==" );
        Q2.setValue( "whee" );
        Q2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q2 );

        // now nest it
        comp.addConstraint( comp2 );

        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName( "goo3" );
        Z.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Z.setFieldType( DataType.TYPE_STRING );
        Z.setValue( "foo" );
        Z.setOperator( "==" );

        p.addConstraint( Z );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        String expected = "rule \"with composite\"\n" +
                "\tdialect \"mvel\"\n" +
                "\twhen\n" +
                "\t\tp1 : Person( )\n" +
                "\t\tGoober( goo == \"foo\"  || == \"bar\" || goo2 == \"foo\" || ( goo == \"whee\" && gabba == \"whee\" ), goo3 == \"foo\" )\n" +
                "\tthen\n" +
                "\t\tinsert( new Whee() );\n" +
                "end\n";
        assertEqualsIgnoreWhitespace( expected,
                                      actual );

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testFieldsDeclaredButNoConstraints() {
        RuleModel m = new RuleModel();
        m.name = "boo";

        FactPattern p = new FactPattern( "Person" );

        // this isn't an effective constraint, so it should be ignored.
        p.addConstraint( new SingleFieldConstraint( "field1" ) );

        m.addLhsItem( p );

        String actual = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        String expected = "rule \"boo\" \tdialect \"mvel\"\n when Person() then end";

        checkMarshalling( expected, m );

        SingleFieldConstraint con = (SingleFieldConstraint) p.getConstraintList().getConstraint( 0 );
        con.setFieldBinding( "q" );

        // now it should appear, as we are binding a var to it
        expected = "rule \"boo\" dialect \"mvel\" when Person(q : field1) then end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralStrings() {

        RuleModel m = new RuleModel();
        m.name = "test literal strings";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test literal strings\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1 == \"goo\", field2 == variableHere)"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionString1() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsString1";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionText( "field1" ) );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsString1\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == \"goo\" )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionString2() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsString2";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.String",
                                                                     DataType.TYPE_STRING ) );
        con.setOperator( "==" );
        con.setValue( "Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        String expected = "rule \"test expressionsString2\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == \"Cheddar\" )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionJavaEnum() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsJavaEnum";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "CHEESE",
                                                                     DataType.TYPE_COMPARABLE ) );
        con.setOperator( "==" );
        con.setValue( "CHEESE.Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsJavaEnum\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == CHEESE.Cheddar )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNumber() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNumber";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.Integer",
                                                                     DataType.TYPE_NUMERIC_INTEGER ) );
        con.setOperator( "==" );
        con.setValue( "55" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNumber\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == 55 )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionDate() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsDate";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.util.Date",
                                                                     DataType.TYPE_DATE ) );
        con.setOperator( "==" );
        con.setValue( "27-Jun-2011" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsDate\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == \"27-Jun-2011\" )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionBoolean() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsBoolean";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "field1",
                                                                     "java.lang.Boolean",
                                                                     DataType.TYPE_BOOLEAN ) );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsBoolean\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( field1 == true )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNestedString() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedString";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     DataType.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "name",
                                                                     "java.lang.String",
                                                                     DataType.TYPE_STRING ) );
        con.setOperator( "==" );
        con.setValue( "Cheedar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNestedString\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( favouriteCheese.name == \"Cheedar\" )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNestedNumber() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedNumber";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     DataType.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "age",
                                                                     "java.lang.Integer",
                                                                     DataType.TYPE_NUMERIC_INTEGER ) );
        con.setOperator( "==" );
        con.setValue( "55" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNestedNumber\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( favouriteCheese.age == 55 )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNestedDate() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedDate";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     DataType.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "dateBrought",
                                                                     "java.util.Date",
                                                                     DataType.TYPE_DATE ) );
        con.setOperator( "==" );
        con.setValue( "27-Jun-2011" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNestedDate\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( favouriteCheese.dateBrought == \"27-Jun-2011\" )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNestedJavaEnum() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedJavaEnum";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     DataType.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "genericName",
                                                                     "CHEESE",
                                                                     DataType.TYPE_COMPARABLE ) );
        con.setOperator( "==" );
        con.setValue( "CHEESE.Cheddar" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNestedJavaEnum\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( favouriteCheese.genericName == CHEESE.Cheddar )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSExpressionNestedBoolean() {

        RuleModel m = new RuleModel();
        m.name = "test expressionsNestedBoolean";
        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraintEBLeftSide con = new SingleFieldConstraintEBLeftSide();
        con.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "favouriteCheese",
                                                                     "Cheese",
                                                                     DataType.TYPE_OBJECT ) );
        con.getExpressionLeftSide().appendPart( new ExpressionField( "smelly",
                                                                     "java.lang.Boolean",
                                                                     DataType.TYPE_BOOLEAN ) );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"test expressionsNestedBoolean\""
                + "\tdialect \"mvel\"\n when "
                + "     Person( favouriteCheese.smelly == true )"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralNumerics() {

        RuleModel m = new RuleModel();
        m.name = "test literal numerics";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test literal numerics\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1 == 44, field2 == variableHere)"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralBigDecimalMvel() {

        RuleModel m = new RuleModel();
        m.name = "test literal bigdecimal";

        m.addAttribute( new RuleAttribute( "dialect",
                                           "mvel" ) );

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_BIGDECIMAL );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionInsertFact ai = new ActionInsertFact( "Person" );
        ai.addFieldValue( new ActionFieldValue( "field1",
                                                "55",
                                                DataType.TYPE_NUMERIC_BIGDECIMAL ) );
        m.addRhsItem( ai );

        String expected = "rule \"test literal bigdecimal\" \n"
                + "\tdialect \"mvel\"\n when \n"
                + "     Person(field1 == 44B) \n"
                + " then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( 55B ); \n"
                + "insert( fact0 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralBigIntegerMvel() {

        RuleModel m = new RuleModel();
        m.name = "test literal biginteger";

        m.addAttribute( new RuleAttribute( "dialect",
                                           "mvel" ) );

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_BIGINTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionInsertFact ai = new ActionInsertFact( "Person" );
        ai.addFieldValue( new ActionFieldValue( "field1",
                                                "55",
                                                DataType.TYPE_NUMERIC_BIGINTEGER ) );
        m.addRhsItem( ai );

        String expected = "rule \"test literal biginteger\" \n"
                + "\tdialect \"mvel\"\n when \n"
                + "     Person(field1 == 44I ) \n"
                + " then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( 55I ); \n"
                + "insert( fact0 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralBigDecimalJava() {

        RuleModel m = new RuleModel();
        m.name = "test literal bigdecimal";

        m.addAttribute( new RuleAttribute( "dialect",
                                           "java" ) );

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_BIGDECIMAL );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionInsertFact ai = new ActionInsertFact( "Person" );
        ai.addFieldValue( new ActionFieldValue( "field1",
                                                "55",
                                                DataType.TYPE_NUMERIC_BIGDECIMAL ) );
        m.addRhsItem( ai );

        String expected = "rule \"test literal bigdecimal\" \n"
                + "\tdialect \"java\"\n when \n"
                + "     Person(field1 == 44B) \n"
                + " then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( new java.math.BigDecimal( \"55\" ) ); \n"
                + "insert( fact0 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralBigIntegerJava() {

        RuleModel m = new RuleModel();
        m.name = "test literal biginteger";

        m.addAttribute( new RuleAttribute( "dialect",
                                           "java" ) );

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_BIGINTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionInsertFact ai = new ActionInsertFact( "Person" );
        ai.addFieldValue( new ActionFieldValue( "field1",
                                                "55",
                                                DataType.TYPE_NUMERIC_BIGINTEGER ) );
        m.addRhsItem( ai );

        String expected = "rule \"test literal biginteger\" \n"
                + "\tdialect \"java\"\n when \n"
                + "     Person(field1 == 44I ) \n"
                + " then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( new java.math.BigInteger( \"55\" ) ); \n"
                + "insert( fact0 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralBooleans() {

        RuleModel m = new RuleModel();
        m.name = "test literal booleans";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_BOOLEAN );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "true" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test literal booleans\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1 == true, field2 == variableHere)"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralDates() {

        RuleModel m = new RuleModel();
        m.name = "test literal dates";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_DATE );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "31-Jan-2010" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test literal dates\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1 == \"31-Jan-2010\", field2 == variableHere)"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLiteralNoType() {

        RuleModel m = new RuleModel();
        m.name = "test literal no type";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "bananna" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test literal no type\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1 == \"bananna\", field2 == variableHere)"
                + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testInOperatorString() {

        RuleModel m = new RuleModel();
        m.name = "in";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "in" );
        con.setValue( "value1, value2" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"in\" \n"
                + "dialect \"mvel\" \n"
                + "when \n"
                + "     Person(field1 in ( \"value1\", \"value2\" ) ) \n"
                + " then \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testInOperatorNumber() {

        RuleModel m = new RuleModel();
        m.name = "in";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "in" );
        con.setValue( "55, 66" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"in\" \n"
                + "dialect \"mvel\" \n"
                + "when \n"
                + "     Person(field1 in ( 55, 66 ) ) \n"
                + " then \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testNotInOperatorString() {

        RuleModel m = new RuleModel();
        m.name = "not in";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "not in" );
        con.setValue( "value1, value2" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"not in\" \n"
                + "dialect \"mvel\" \n"
                + "when \n"
                + "     Person(field1 not in ( \"value1\", \"value2\" ) ) \n"
                + " then \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testNotInOperatorNumber() {

        RuleModel m = new RuleModel();
        m.name = "not in";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "not in" );
        con.setValue( "55, 66" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        String expected = "rule \"not in\" \n"
                + "dialect \"mvel\" \n"
                + "when \n"
                + "     Person(field1 not in ( 55, 66 ) ) \n"
                + " then \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSDateInsertAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( DataType.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionInsertFact ai = new ActionInsertFact( "Birthday" );
            ai.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    DataType.TYPE_DATE ) );
            m.addRhsItem( ai );

            String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "fact0.setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );

            checkMarshalling( null,
                              m );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
    }

    @Test
    public void testRHSDateModifyAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( DataType.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionUpdateField am = new ActionUpdateField( "$p" );
            am.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    DataType.TYPE_DATE ) );
            m.addRhsItem( am );

            String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );
            assertTrue( result.indexOf( "modify( $p ) {" ) != -1 );

            checkMarshalling( null,
                              m );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }

    }

    @Test
    public void testRHSDateUpdateAction() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS Date";

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            SingleFieldConstraint con = new SingleFieldConstraint();
            con.setFieldType( DataType.TYPE_DATE );
            con.setFieldName( "dateOfBirth" );
            con.setOperator( "==" );
            con.setValue( "31-Jan-2000" );
            con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
            p.addConstraint( con );

            m.addLhsItem( p );

            ActionSetField au = new ActionSetField( "$p" );
            au.addFieldValue( new ActionFieldValue( "dob",
                                                    "31-Jan-2000",
                                                    DataType.TYPE_DATE ) );
            m.addRhsItem( au );

            String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "$p.setDob( sdf.parse(\"31-Jan-2000\"" ) != -1 );
            assertTrue( result.indexOf( "update( $p );" ) == -1 );

            checkMarshalling( null,
                              m );
        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }
    }

    @Test
    public void testRHSExecuteWorkItem1() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern p = new FactPattern( "Person" );
        p.setBoundName( "$p" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "name" );
        con.setOperator( "==" );
        con.setValue( "Michael" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        m.addRhsItem( awi );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", Boolean.TRUE );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", 123.456f );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", 123 );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", \"hello\" );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    public void testRHSExecuteWorkItem2() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern p = new FactPattern( "Person" );
        p.setBoundName( "$p" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "name" );
        con.setOperator( "==" );
        con.setValue( "Michael" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "" );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        p2.setBinding( "" );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        p3.setBinding( "" );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        p4.setBinding( "" );
        pwd.addParameter( p4 );

        m.addRhsItem( awi );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", Boolean.TRUE );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", 123.456f );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", 123 );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", \"hello\" );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    //Test that WorkItem Parameters whose values are bound are created and
    //populated in the RHS if the Pattern is bound to the same variable
    public void testRHSExecuteWorkItemWithBindings() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern fp1 = new FactPattern( "Person" );
        fp1.setBoundName( "$p" );
        SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setFieldType( DataType.TYPE_STRING );
        con1.setFieldName( "name" );
        con1.setOperator( "==" );
        con1.setValue( "Michael" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( con1 );
        m.addLhsItem( fp1 );

        FactPattern fp2 = new FactPattern( "Boolean" );
        fp2.setBoundName( "$b" );
        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldType( DataType.TYPE_BOOLEAN );
        con2.setFieldName( "this" );
        con2.setOperator( "==" );
        con2.setValue( "true" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp2.addConstraint( con2 );
        m.addLhsItem( fp2 );

        FactPattern fp3 = new FactPattern( "Float" );
        fp3.setBoundName( "$f" );
        SingleFieldConstraint con3 = new SingleFieldConstraint();
        con3.setFieldType( DataType.TYPE_NUMERIC_FLOAT );
        con3.setFieldName( "this" );
        con3.setOperator( "==" );
        con3.setValue( "123.456f" );
        con3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp3.addConstraint( con3 );
        m.addLhsItem( fp3 );

        FactPattern fp4 = new FactPattern( "Integer" );
        fp4.setBoundName( "$i" );
        SingleFieldConstraint con4 = new SingleFieldConstraint();
        con4.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con4.setFieldName( "this" );
        con4.setOperator( "==" );
        con4.setValue( "123" );
        con4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp4.addConstraint( con4 );
        m.addLhsItem( fp4 );

        FactPattern fp5 = new FactPattern( "String" );
        fp5.setBoundName( "$s" );
        SingleFieldConstraint con5 = new SingleFieldConstraint();
        con5.setFieldType( DataType.TYPE_STRING );
        con5.setFieldName( "this" );
        con5.setOperator( "==" );
        con5.setValue( "hello" );
        con5.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp5.addConstraint( con5 );
        m.addLhsItem( fp5 );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setBinding( "$b" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setBinding( "$f" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setBinding( "$i" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setBinding( "$s" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        m.addRhsItem( awi );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", $b );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", $f );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", $i );" ) != -1 );
        assertTrue( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", $s );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    //Test that WorkItem Parameters whose values are bound are *NOT* created or
    //populated in the RHS if the Pattern is *NOT* bound to the same variable
    public void testRHSExecuteWorkItemWithMissingBindings1() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern fp1 = new FactPattern( "Person" );
        fp1.setBoundName( "$p" );
        SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setFieldType( DataType.TYPE_STRING );
        con1.setFieldName( "name" );
        con1.setOperator( "==" );
        con1.setValue( "Michael" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( con1 );
        m.addLhsItem( fp1 );

        FactPattern fp2 = new FactPattern( "Boolean" );
        fp2.setBoundName( "$b1" );
        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldType( DataType.TYPE_BOOLEAN );
        con2.setFieldName( "this" );
        con2.setOperator( "==" );
        con2.setValue( "true" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp2.addConstraint( con2 );
        m.addLhsItem( fp2 );

        FactPattern fp3 = new FactPattern( "Float" );
        fp3.setBoundName( "$f1" );
        SingleFieldConstraint con3 = new SingleFieldConstraint();
        con3.setFieldType( DataType.TYPE_NUMERIC_FLOAT );
        con3.setFieldName( "this" );
        con3.setOperator( "==" );
        con3.setValue( "123.456f" );
        con3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp3.addConstraint( con3 );
        m.addLhsItem( fp3 );

        FactPattern fp4 = new FactPattern( "Integer" );
        fp4.setBoundName( "$i1" );
        SingleFieldConstraint con4 = new SingleFieldConstraint();
        con4.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con4.setFieldName( "this" );
        con4.setOperator( "==" );
        con4.setValue( "123" );
        con4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp4.addConstraint( con4 );
        m.addLhsItem( fp4 );

        FactPattern fp5 = new FactPattern( "String" );
        fp5.setBoundName( "$s1" );
        SingleFieldConstraint con5 = new SingleFieldConstraint();
        con5.setFieldType( DataType.TYPE_STRING );
        con5.setFieldName( "this" );
        con5.setOperator( "==" );
        con5.setValue( "hello" );
        con5.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp5.addConstraint( con5 );
        m.addLhsItem( fp5 );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setBinding( "$b" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setBinding( "$f" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setBinding( "$i" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setBinding( "$s" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        m.addRhsItem( awi );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", $b1 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", $f1 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", $i1 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", $s1 );" ) != -1 );

        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", $b2 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", $f2 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", $i2 );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", $s2 );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    //Test that WorkItem Parameters whose values are bound are *NOT* created or
    //populated in the RHS if the Pattern is *NOT* bound to the same variable
    public void testRHSExecuteWorkItemWithMissingBindings2() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern fp1 = new FactPattern( "Person" );
        fp1.setBoundName( "$p" );
        SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setFieldType( DataType.TYPE_STRING );
        con1.setFieldName( "name" );
        con1.setOperator( "==" );
        con1.setValue( "Michael" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        fp1.addConstraint( con1 );
        m.addLhsItem( fp1 );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setBinding( "$b2" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setBinding( "$f2" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setBinding( "$i2" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setBinding( "$s2" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        m.addRhsItem( awi );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"BooleanParameter\", $b );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"FloatParameter\", $f );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"IntegerParameter\", $i );" ) != -1 );
        assertFalse( result.indexOf( "wiWorkItem.getParameters().put( \"StringParameter\", $s );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    //Test that WorkItem Parameters can be used to set fields on existing Facts
    public void testRHSActionWorkItemSetFields() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        FactPattern fp1 = new FactPattern( "Results" );
        fp1.setBoundName( "$r" );
        m.addLhsItem( fp1 );

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        m.addRhsItem( awi );

        ActionSetField asf = new ActionSetField();
        asf.setVariable( "$r" );
        ActionWorkItemFieldValue fv1 = new ActionWorkItemFieldValue( "ResultsBooleanResult",
                                                                     DataType.TYPE_BOOLEAN,
                                                                     "WorkItem",
                                                                     "BooleanResult",
                                                                     Boolean.class.getName() );
        asf.addFieldValue( fv1 );
        ActionWorkItemFieldValue fv2 = new ActionWorkItemFieldValue( "ResultsFloatResult",
                                                                     DataType.TYPE_NUMERIC_FLOAT,
                                                                     "WorkItem",
                                                                     "FloatResult",
                                                                     Float.class.getName() );
        asf.addFieldValue( fv2 );
        ActionWorkItemFieldValue fv3 = new ActionWorkItemFieldValue( "ResultsIntegerResult",
                                                                     DataType.TYPE_NUMERIC_INTEGER,
                                                                     "WorkItem",
                                                                     "IntegerResult",
                                                                     Integer.class.getName() );
        asf.addFieldValue( fv3 );
        ActionWorkItemFieldValue fv4 = new ActionWorkItemFieldValue( "ResultsStringResult",
                                                                     DataType.TYPE_STRING,
                                                                     "WorkItem",
                                                                     "StringResult",
                                                                     String.class.getName() );
        asf.addFieldValue( fv4 );

        m.addRhsItem( asf );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertTrue( result.indexOf( "$r.setResultsBooleanResult( (java.lang.Boolean) wiWorkItem.getResult( \"BooleanResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsFloatResult( (java.lang.Float) wiWorkItem.getResult( \"FloatResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsIntegerResult( (java.lang.Integer) wiWorkItem.getResult( \"IntegerResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsStringResult( (java.lang.String) wiWorkItem.getResult( \"StringResult\" ) );" ) != -1 );

        assertTrue( result.indexOf( "wim.internalExecuteWorkItem( wiWorkItem );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    //Test that WorkItem Parameters can be used to set fields on new Fact
    public void testRHSActionWorkItemInsertFacts() {

        RuleModel m = new RuleModel();
        m.name = "WorkItem";

        ActionExecuteWorkItem awi = new ActionExecuteWorkItem();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        m.addRhsItem( awi );

        ActionInsertFact aif = new ActionInsertFact();
        aif.setBoundName( "$r" );
        aif.setFactType( "Results" );
        ActionWorkItemFieldValue fv1 = new ActionWorkItemFieldValue( "ResultsBooleanResult",
                                                                     DataType.TYPE_BOOLEAN,
                                                                     "WorkItem",
                                                                     "BooleanResult",
                                                                     Boolean.class.getName() );
        aif.addFieldValue( fv1 );
        ActionWorkItemFieldValue fv2 = new ActionWorkItemFieldValue( "ResultsFloatResult",
                                                                     DataType.TYPE_NUMERIC_FLOAT,
                                                                     "WorkItem",
                                                                     "FloatResult",
                                                                     Float.class.getName() );
        aif.addFieldValue( fv2 );
        ActionWorkItemFieldValue fv3 = new ActionWorkItemFieldValue( "ResultsIntegerResult",
                                                                     DataType.TYPE_NUMERIC_INTEGER,
                                                                     "WorkItem",
                                                                     "IntegerResult",
                                                                     Integer.class.getName() );
        aif.addFieldValue( fv3 );
        ActionWorkItemFieldValue fv4 = new ActionWorkItemFieldValue( "ResultsStringResult",
                                                                     DataType.TYPE_STRING,
                                                                     "WorkItem",
                                                                     "StringResult",
                                                                     String.class.getName() );
        aif.addFieldValue( fv4 );

        m.addRhsItem( aif );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( result.indexOf( "org.drools.core.process.instance.WorkItemManager wim = (org.drools.core.process.instance.WorkItemManager) drools.getWorkingMemory().getWorkItemManager();" ) != -1 );
        assertTrue( result.indexOf( "org.drools.core.process.instance.impl.WorkItemImpl wiWorkItem = new org.drools.core.process.instance.impl.WorkItemImpl();" ) != -1 );

        assertTrue( result.indexOf( "Results $r = new Results();" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsBooleanResult( (java.lang.Boolean) wiWorkItem.getResult( \"BooleanResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsFloatResult( (java.lang.Float) wiWorkItem.getResult( \"FloatResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsIntegerResult( (java.lang.Integer) wiWorkItem.getResult( \"IntegerResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "$r.setResultsStringResult( (java.lang.String) wiWorkItem.getResult( \"StringResult\" ) );" ) != -1 );
        assertTrue( result.indexOf( "insert( $r );" ) != -1 );

        checkMarshalling( null,
                          m );
    }

    @Test
    public void testSubConstraints() {

        RuleModel m = new RuleModel();
        m.name = "test sub constraints";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "field1" );
        p.addConstraint( con );

        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.setFieldName( "field2" );
        con2.setOperator( "==" );
        con2.setValue( "variableHere" );
        con2.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        con2.setParent( con );
        p.addConstraint( con2 );

        m.addLhsItem( p );

        String expected = "rule \"test sub constraints\""
                + "\tdialect \"mvel\"\n when "
                + "     Person(field1.field2 == variableHere)" + " then "
                + "end";

        checkMarshalling( expected,
                          m );
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

    @Test
    public void testReturnValueConstraint() {
        RuleModel m = new RuleModel();
        m.name = "yeah";

        FactPattern p = new FactPattern( "Goober" );

        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_RET_VALUE );
        con.setValue( "someFunc(x)" );
        con.setOperator( "==" );
        con.setFieldName( "goo" );

        p.addConstraint( con );
        m.addLhsItem( p );

        String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                + "Goober( goo == ( someFunc(x) ) )" + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testPredicateConstraint() {
        RuleModel m = new RuleModel();
        m.name = "yeah";

        FactPattern p = new FactPattern( "Goober" );

        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setConstraintValueType( SingleFieldConstraint.TYPE_PREDICATE );
        con.setValue( "field soundslike \"poo\"" );

        p.addConstraint( con );
        m.addLhsItem( p );

        String expected = "rule \"yeah\" " + "\tdialect \"mvel\"\n when "
                + "Goober( eval( field soundslike \"poo\" ) )" + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testConnective() {

        RuleModel m = new RuleModel();
        m.name = "test literal strings";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_STRING );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "goo" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
        p.addConstraint( con );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        connective.setFieldType( DataType.TYPE_STRING );
        connective.setOperator( "|| ==" );
        connective.setValue( "blah" );

        con.setConnectives( new ConnectiveConstraint[ 1 ] );
        con.getConnectives()[ 0 ] = connective;

        m.addLhsItem( p );

        String expected = "rule \"test literal strings\" "
                + "\tdialect \"mvel\"\n when "
                + "Person( field1 == goo  || == \"blah\" )" + " then " + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testInvalidComposite() throws Exception {
        RuleModel m = new RuleModel();
        CompositeFactPattern com = new CompositeFactPattern( "not" );
        m.addLhsItem( com );

        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertNotNull( s );

        m.addLhsItem( new CompositeFactPattern( "or" ) );
        m.addLhsItem( new CompositeFactPattern( "exists" ) );
        s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertNotNull( s );
    }

    @Test
    public void testAssertWithDSL() throws Exception {
        RuleModel m = new RuleModel();
        DSLSentence sen = new DSLSentence();
        sen.setDefinition( "I CAN HAS DSL" );
        m.addRhsItem( sen );
        ActionInsertFact ins = new ActionInsertFact( "Shizzle" );
        ActionFieldValue val = new ActionFieldValue( "goo",
                                                     "42",
                                                     "Numeric" );
        ins.setFieldValues( new ActionFieldValue[ 1 ] );
        ins.getFieldValues()[ 0 ] = val;
        m.addRhsItem( ins );

        ActionInsertLogicalFact insL = new ActionInsertLogicalFact( "Shizzle" );
        ActionFieldValue valL = new ActionFieldValue( "goo",
                                                      "42",
                                                      "Numeric" );
        insL.setFieldValues( new ActionFieldValue[ 1 ] );
        insL.getFieldValues()[ 0 ] = valL;
        m.addRhsItem( insL );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( result.indexOf( ">insert" ) > -1 );

        assertTrue( result.indexOf( ">insertLogical" ) > -1 );
    }

    @Test
    public void testDefaultMVEL() {
        RuleModel m = new RuleModel();

        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertTrue( s.indexOf( "mvel" ) > -1 );

        m.addAttribute( new RuleAttribute( "dialect",
                                           "goober" ) );
        s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertFalse( s.indexOf( "mvel" ) > -1 );
        assertTrue( s.indexOf( "goober" ) > -1 );
    }

    @Test
    public void testLockOnActive() {
        RuleModel m = new RuleModel();

        m.addAttribute( new RuleAttribute( "lock-on-active",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "auto-focus",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "duration",
                                           "42" ) );

        String s = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        assertTrue( s.indexOf( "lock-on-active true" ) > -1 );
        assertTrue( s.indexOf( "auto-focus true" ) > -1 );
        assertTrue( s.indexOf( "duration 42" ) > -1 );

        checkMarshalling( s,
                          m );
    }

    @Test
    public void testAddGlobal() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\tdialect \"mvel\"\n\twhen\n\t\tPerson( )\n"
                + "\t\tAccident( )\n\tthen\n\t\tinsert( new Report() );\n\t\tresults.add(f);\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        ActionGlobalCollectionAdd add = new ActionGlobalCollectionAdd();
        add.setGlobalName( "results" );
        add.setFactName( "f" );
        m.addRhsItem( add );
        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCompositeOrConstraints() {
        RuleModel m = new RuleModel();
        m.name = "or composite";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFactType( "Goober" );
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFactType( "Goober" );
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( DataType.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFactType( "Bar" );
        sfc3.setFieldName( "barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( DataType.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        String expected = "rule \"or composite\""
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Goober( gooField == \"gooValue\" || fooField != null || fooField.barField == \"barValue\" )\n"
                + "then\n"
                + "insert( new Whee() );\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCompositeOrConstraintsComplex() {
        RuleModel m = new RuleModel();
        m.name = "or composite complex";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFactType( "Goober" );
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFactType( "Goober" );
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( DataType.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFactType( "Bar" );
        sfc3.setFieldName( "barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( DataType.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        final SingleFieldConstraint sfc4 = new SingleFieldConstraint();
        sfc4.setFactType( "Goober" );
        sfc4.setFieldName( "zooField" );
        sfc4.setFieldType( DataType.TYPE_STRING );
        sfc4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc4.setValue( "zooValue" );
        sfc4.setOperator( "==" );
        p.addConstraint( sfc4 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String expected = "rule \"or composite complex\""
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Goober( gooField == \"gooValue\" || fooField != null || fooField.barField == \"barValue\", zooField == \"zooValue\" )\n"
                + "then\n"
                + "insert( new Whee() );\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCompositeAndConstraints() {
        RuleModel m = new RuleModel();
        m.name = "and composite";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFactType( "Goober" );
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFactType( "Goober" );
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( DataType.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc3.setFactType( "Bar" );
        sfc3.setFieldName( "barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( DataType.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String expected = "rule \"and composite\""
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Goober( gooField == \"gooValue\" && fooField != null && fooField.barField == \"barValue\" )\n"
                + "then\n"
                + "insert( new Whee() );\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCompositeAndConstraintsComplex() {
        RuleModel m = new RuleModel();
        m.name = "and composite complex";

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        p.addConstraint( comp );

        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFactType( "Goober" );
        sfc1.setFieldName( "gooField" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc1.setValue( "gooValue" );
        sfc1.setOperator( "==" );
        comp.addConstraint( sfc1 );

        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFactType( "Goober" );
        sfc2.setFieldName( "fooField" );
        sfc2.setFieldType( DataType.TYPE_OBJECT );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc2.setOperator( "!= null" );
        comp.addConstraint( sfc2 );

        final SingleFieldConstraint sfc3 = new SingleFieldConstraint();
        sfc1.setFactType( "Bar" );
        sfc3.setFieldName( "barField" );
        sfc3.setParent( sfc2 );
        sfc3.setFieldType( DataType.TYPE_STRING );
        sfc3.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc3.setValue( "barValue" );
        sfc3.setOperator( "==" );
        comp.addConstraint( sfc3 );

        final SingleFieldConstraint sfc4 = new SingleFieldConstraint();
        sfc4.setFactType( "Goober" );
        sfc4.setFieldName( "zooField" );
        sfc4.setFieldType( DataType.TYPE_STRING );
        sfc4.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        sfc4.setValue( "zooValue" );
        sfc4.setOperator( "==" );
        p.addConstraint( sfc4 );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String actual = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        String expected = "rule \"and composite complex\""
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Goober( gooField == \"gooValue\" && fooField != null && fooField.barField == \"barValue\", zooField == \"zooValue\" )\n"
                + "then\n"
                + "insert( new Whee() );\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSSetMethodCallsMethodMVEL() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS SetMethodCallsMethod";
            m.addAttribute( new RuleAttribute( "dialect",
                                               "mvel" ) );

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            m.addLhsItem( p );

            ActionCallMethod acm = new ActionCallMethod();
            acm.setMethodName( "method" );
            acm.setVariable( "$p" );
            acm.addFieldValue( new ActionFieldFunction( "f1",
                                                        "String",
                                                        DataType.TYPE_STRING ) );
            acm.addFieldValue( new ActionFieldFunction( "f2",
                                                        "true",
                                                        DataType.TYPE_BOOLEAN ) );
            acm.addFieldValue( new ActionFieldFunction( "f3",
                                                        "31-Jan-2012",
                                                        DataType.TYPE_DATE ) );
            acm.addFieldValue( new ActionFieldFunction( "f4",
                                                        "100",
                                                        DataType.TYPE_NUMERIC_INTEGER ) );
            acm.addFieldValue( new ActionFieldFunction( "f5",
                                                        "100",
                                                        DataType.TYPE_NUMERIC_BIGDECIMAL ) );

            m.addRhsItem( acm );

            String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "$p.method( \"String\", true, sdf.parse(\"31-Jan-2012\"), 100, 100B );" ) != -1 );

            checkMarshalling( null,
                              m );

        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }

    }

    @Test
    public void testRHSSetMethodCallsMethodJava() {

        String oldValue = System.getProperty( "drools.dateformat" );
        try {

            System.setProperty( "drools.dateformat",
                                "dd-MMM-yyyy" );

            RuleModel m = new RuleModel();
            m.name = "RHS SetMethodCallsMethod";
            m.addAttribute( new RuleAttribute( "dialect",
                                               "java" ) );

            FactPattern p = new FactPattern( "Person" );
            p.setBoundName( "$p" );
            m.addLhsItem( p );

            ActionCallMethod acm = new ActionCallMethod();
            acm.setMethodName( "method" );
            acm.setVariable( "$p" );
            acm.addFieldValue( new ActionFieldFunction( "f1",
                                                        "String",
                                                        DataType.TYPE_STRING ) );
            acm.addFieldValue( new ActionFieldFunction( "f2",
                                                        "true",
                                                        DataType.TYPE_BOOLEAN ) );
            acm.addFieldValue( new ActionFieldFunction( "f3",
                                                        "31-Jan-2012",
                                                        DataType.TYPE_DATE ) );
            acm.addFieldValue( new ActionFieldFunction( "f4",
                                                        "100",
                                                        DataType.TYPE_NUMERIC_INTEGER ) );
            acm.addFieldValue( new ActionFieldFunction( "f5",
                                                        "100",
                                                        DataType.TYPE_NUMERIC_BIGDECIMAL ) );

            m.addRhsItem( acm );

            String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

            assertTrue( result.indexOf( "java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"dd-MMM-yyyy\");" ) != -1 );
            assertTrue( result.indexOf( "$p.method( \"String\", true, sdf.parse(\"31-Jan-2012\"), 100, new java.math.BigDecimal(\"100\") );" ) != -1 );

            checkMarshalling( null,
                              m );

        } finally {
            if ( oldValue == null ) {
                System.clearProperty( "drools.dateformat" );
            } else {
                System.setProperty( "drools.dateformat",
                                    oldValue );
            }
        }

    }

    @Test
    public void testFromAccumulateWithEmbeddedFromEntryPoint() {
        RuleModel m = new RuleModel();
        m.name = "r1";

        SingleFieldConstraint sfc = new SingleFieldConstraint( "bar" );
        sfc.setFactType( DataType.TYPE_NUMERIC_INTEGER );
        sfc.setFieldBinding( "$a" );
        sfc.setOperator( "==" );
        sfc.setValue( "777" );

        FactPattern fp = new FactPattern( "Foo" );
        fp.addConstraint( sfc );

        FromEntryPointFactPattern fep = new FromEntryPointFactPattern();
        fep.setEntryPointName( "ep" );
        fep.setFactPattern( fp );
        FromAccumulateCompositeFactPattern fac = new FromAccumulateCompositeFactPattern();
        fac.setSourcePattern( fep );
        fac.setFactPattern( new FactPattern( "java.util.List" ) );
        fac.setFunction( "max($a)" );
        m.addLhsItem( fac );

        String expected = "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( ) from accumulate ( Foo( $a : bar == 777 ) from entry-point \"ep\", \n"
                + "max($a))\n"
                + "then\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testFromCollectWithEmbeddedFromEntryPoint() {
        RuleModel m = new RuleModel();
        m.name = "r1";

        SingleFieldConstraint sfc = new SingleFieldConstraint( "bar" );
        sfc.setFactType( DataType.TYPE_NUMERIC_INTEGER );
        sfc.setFieldBinding( "$a" );
        sfc.setOperator( "==" );
        sfc.setValue( "777" );

        FactPattern fp = new FactPattern( "Foo" );
        fp.addConstraint( sfc );

        FromEntryPointFactPattern fep = new FromEntryPointFactPattern();
        fep.setEntryPointName( "ep" );
        fep.setFactPattern( fp );
        FromCollectCompositeFactPattern fac = new FromCollectCompositeFactPattern();
        fac.setRightPattern( fep );
        fac.setFactPattern( new FactPattern( "java.util.List" ) );
        m.addLhsItem( fac );

        String expected = "rule \"r1\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "java.util.List( ) from collect ( Foo( $a : bar == 777 ) from entry-point \"ep\" ) \n"
                + "then\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testCompositeFactPatternWithFromWithDSL() {
        final RuleModel m = new RuleModel();
        m.name = "model";

        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "A DSL phrase" );
        m.addLhsItem( sen );

        final FactPattern fp1 = new FactPattern( "Data" );
        fp1.setBoundName( "$d" );
        m.addLhsItem( fp1 );

        final CompositeFactPattern cp = new CompositeFactPattern( CompositeFactPattern.COMPOSITE_TYPE_NOT );

        final FactPattern fp2 = new FactPattern( "Person" );
        final FromCompositeFactPattern ffp1 = new FromCompositeFactPattern();
        ffp1.setExpression( new ExpressionFormLine( new ExpressionVariable( fp1.getBoundName(),
                                                                            fp1.getFactType() ) ) );
        ffp1.setFactPattern( fp2 );
        cp.addFactPattern( ffp1 );
        m.addLhsItem( cp );

        final String actual = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        final String expected = "rule \"model\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "A DSL phrase\n" +
                ">$d : Data( )\n" +
                ">not ( Person( ) from $d\n" +
                ")\n" +
                "then\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected,
                                      actual );
    }

    @Test
    public void testImports() {
        final String drl = "import java.util.ArrayList;\n" +
                "rule \"r0\"\n" +
                "dialect \"mvel\"" +
                "when\n" +
                "then\n" +
                "end\n";

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Collections.EMPTY_LIST,
                                                                                 dmo );
        assertNotNull( m );

        assertEquals( 1,
                      m.getImports().getImports().size() );
        assertEquals( "java.util.ArrayList",
                      m.getImports().getImports().get( 0 ).getType() );
    }

    @Test
    public void testActionSetFieldValue() {
        final String drl = "rule \"r0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$a : Applicant( )\n" +
                "then\n" +
                "$a.setName( \"Michael\" );\n" +
                "end\n";

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Collections.EMPTY_LIST,
                                                                                 dmo );
        assertNotNull( m );

        //LHS
        assertEquals( 1,
                      m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );

        final FactPattern p = (FactPattern) m.lhs[ 0 ];
        assertEquals( "$a",
                      p.getBoundName() );
        assertEquals( "Applicant",
                      p.getFactType() );

        //RHS
        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionSetField );

        final ActionSetField a = (ActionSetField) m.rhs[ 0 ];
        assertEquals( "$a",
                      a.getVariable() );
        assertEquals( 1,
                      a.getFieldValues().length );

        final ActionFieldValue fv = a.getFieldValues()[ 0 ];
        assertEquals( "name",
                      fv.getField() );
        assertEquals( "Michael",
                      fv.getValue() );
    }

    @Test
    public void testActionCallMethod() {
        final String drl = "rule \"r0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$a : Applicant( )\n" +
                "then\n" +
                "$a.addName( \"Michael\" );\n" +
                "end\n";

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Collections.EMPTY_LIST,
                                                                                 dmo );
        assertNotNull( m );

        //LHS
        assertEquals( 1,
                      m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );

        final FactPattern p = (FactPattern) m.lhs[ 0 ];
        assertEquals( "$a",
                      p.getBoundName() );
        assertEquals( "Applicant",
                      p.getFactType() );

        //RHS
        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionCallMethod );

        final ActionCallMethod a = (ActionCallMethod) m.rhs[ 0 ];
        assertEquals( "$a",
                      a.getVariable() );
        assertEquals( "addName",
                      a.getMethodName() );
        assertEquals( 1,
                      a.getFieldValues().length );

        final ActionFieldValue fv = a.getFieldValue( 0 );
        assertEquals( "Michael",
                      fv.getValue() );
    }

    @Test
    public void testAddToGlobalCollection() {
        String global = "global java.util.ArrayList list";
        String drl =
                "rule \"r0\"\n" +
                        "dialect \"mvel\"\n" +
                        "when\n" +
                        "$a : Applicant( )\n" +
                        "then\n" +
                        "list.add( $a );\n" +
                        "end\n";

        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Arrays.asList( global ),
                                                                                 mock( PackageDataModelOracle.class ) );

        assertNotNull( m );
        assertEqualsIgnoreWhitespace( drl, RuleModelDRLPersistenceImpl.getInstance().marshal( m ) );

        //LHS
        assertEquals( 1,
                      m.lhs.length );
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );

        final FactPattern p = (FactPattern) m.lhs[ 0 ];
        assertEquals( "$a",
                      p.getBoundName() );
        assertEquals( "Applicant",
                      p.getFactType() );

        //RHS
        assertEquals( 1,
                      m.rhs.length );
        assertTrue( m.rhs[ 0 ] instanceof ActionGlobalCollectionAdd );

        final ActionGlobalCollectionAdd a = (ActionGlobalCollectionAdd) m.rhs[ 0 ];
        assertEquals( "list",
                      a.getGlobalName() );
        assertEquals( "$a",
                      a.getFactName() );
    }

    @Test
    public void testFreeFormatDRLCondition() {
        final String drl = "rule \"r0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$a : Applicant( )\n" +
                "Here's something typed by the user as free-format DRL\n" +
                "$b : Bananna( )\n" +
                "then\n" +
                "end\n";

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Collections.EMPTY_LIST,
                                                                                 dmo );
        assertNotNull( m );

        //LHS
        assertEquals( 3,
                      m.lhs.length );

        //Condition line 1
        assertTrue( m.lhs[ 0 ] instanceof FactPattern );
        final FactPattern fp1 = (FactPattern) m.lhs[ 0 ];
        assertEquals( "$a",
                      fp1.getBoundName() );
        assertEquals( "Applicant",
                      fp1.getFactType() );

        //Condition line 2
        assertTrue( m.lhs[ 1 ] instanceof FreeFormLine );
        final FreeFormLine ffl = (FreeFormLine) m.lhs[ 1 ];
        assertEquals( "Here's something typed by the user as free-format DRL",
                      ffl.getText() );

        //Condition line 3
        assertTrue( m.lhs[ 2 ] instanceof FactPattern );
        final FactPattern fp2 = (FactPattern) m.lhs[ 2 ];
        assertEquals( "$b",
                      fp2.getBoundName() );
        assertEquals( "Bananna",
                      fp2.getFactType() );
    }

    @Test
    public void testFreeFormatDRLAction() {
        final String drl = "rule \"r0\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "$a : Applicant( )\n" +
                "then\n" +
                "$a.setName( \"Michael\" );\n" +
                "Here's something typed by the user as free-format DRL\n" +
                "$a.setAge( 40 );\n" +
                "end\n";

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                                 Collections.EMPTY_LIST,
                                                                                 dmo );
        assertNotNull( m );

        //LHS
        assertEquals( 1,
                      m.lhs.length );

        assertTrue( m.lhs[ 0 ] instanceof FreeFormLine );
        final FreeFormLine ffl1 = (FreeFormLine) m.lhs[ 0 ];
        assertEquals( "$a : Applicant( )",
                      ffl1.getText() );

        //RHS
        assertEquals( 1,
                      m.rhs.length );

        assertTrue( m.rhs[ 0 ] instanceof FreeFormLine );
        final FreeFormLine ffl2 = (FreeFormLine) m.rhs[ 0 ];
        assertEquals( "$a.setName( \"Michael\" );\n" +
                              "Here's something typed by the user as free-format DRL\n" +
                              "$a.setAge( 40 );",
                      ffl2.getText() );
    }

    @Test
    public void testGenerateEmptyXML() {
        final RuleModelPersistence p = RuleModelDRLPersistenceImpl.getInstance();
        final String drl = p.marshal( new RuleModel() );
        assertNotNull( drl );
        assertFalse( drl.equals( "" ) );
    }

    @Test
    public void testBasics2() {
        final RuleModelPersistence p = RuleModelDRLPersistenceImpl.getInstance();
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionInsertFact( "Report" ) );
        ActionGlobalCollectionAdd ag = new ActionGlobalCollectionAdd();
        ag.setFactName( "x" );
        ag.setGlobalName( "g" );
        m.addRhsItem( ag );
        m.name = "my rule";

        final String drl = p.marshal( m );
        assertTrue( drl.indexOf( "Person( )" ) > -1 );
        assertTrue( drl.indexOf( "Accident( )" ) > -1 );
        assertTrue( drl.indexOf( "no-loop true" ) > -1 );
        assertTrue( drl.indexOf( "org.kie" ) == -1 );
        assertTrue( drl.indexOf( "g.add( x );" ) > -1 );

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        RuleModel rm_ = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                             Collections.EMPTY_LIST,
                                                                             dmo );
        assertEquals( 2,
                      rm_.rhs.length );

    }

    @Test
    public void testMoreComplexRendering2() {
        final RuleModelPersistence p = RuleModelDRLPersistenceImpl.getInstance();
        final RuleModel m = getComplexModel();
        final String drl = p.marshal( m );

        assertTrue( drl.indexOf( "org.kie" ) == -1 );

    }

    @Test
    public void testRoundTrip() {
        final RuleModel m = getComplexModel();
        final String drl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        final RuleModel m2 = RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                          Collections.EMPTY_LIST,
                                                                                          dmo );
        assertNotNull( m2 );
        assertEquals( m.name,
                      m2.name );
        assertEquals( m.lhs.length,
                      m2.lhs.length );
        assertEquals( m.rhs.length,
                      m2.rhs.length );
        assertEquals( 1,
                      m.attributes.length );

        final RuleAttribute at = m.attributes[ 0 ];
        assertEquals( "no-loop",
                      at.getAttributeName() );
        assertEquals( "true",
                      at.getValue() );

        final String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( m2 );
        assertEquals( drl,
                      drl2 );

    }

    @Test
    public void testCompositeConstraintsRoundTrip() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "with composite";

        FactPattern p1 = new FactPattern( "Person" );
        p1.setBoundName( "p1" );
        m.addLhsItem( p1 );

        FactPattern p = new FactPattern( "Goober" );
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_OR );
        p.addConstraint( comp );

        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.setFieldName( "goo" );
        X.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        X.setValue( "foo" );
        X.setOperator( "==" );
        X.setConnectives( new ConnectiveConstraint[ 1 ] );
        X.getConnectives()[ 0 ] = new ConnectiveConstraint();
        X.getConnectives()[ 0 ].setConstraintValueType( ConnectiveConstraint.TYPE_LITERAL );
        X.getConnectives()[ 0 ].setOperator( "|| ==" );
        X.getConnectives()[ 0 ].setValue( "bar" );
        comp.addConstraint( X );

        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.setFieldName( "goo2" );
        Y.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Y.setValue( "foo" );
        Y.setOperator( "==" );
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.setCompositeJunctionType( CompositeFieldConstraint.COMPOSITE_TYPE_AND );
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.setFieldName( "goo" );
        Q1.setOperator( "==" );
        Q1.setValue( "whee" );
        Q1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q1 );

        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.setFieldName( "gabba" );
        Q2.setOperator( "==" );
        Q2.setValue( "whee" );
        Q2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );

        comp2.addConstraint( Q2 );

        //now nest it
        comp.addConstraint( comp2 );

        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.setFieldName( "goo3" );
        Z.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        Z.setValue( "foo" );
        Z.setOperator( "==" );

        p.addConstraint( Z );

        ActionInsertFact ass = new ActionInsertFact( "Whee" );
        m.addRhsItem( ass );

        String drl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        RuleModel m2 = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                            Collections.EMPTY_LIST,
                                                                            dmo );
        assertNotNull( m2 );
        assertEquals( "with composite",
                      m2.name );

        assertEquals( m2.lhs.length,
                      m.lhs.length );
        assertEquals( m2.rhs.length,
                      m.rhs.length );

    }

    @Test
    public void testFreeFormLine() {
        RuleModel m = new RuleModel();
        m.name = "with composite";
        m.lhs = new IPattern[ 1 ];
        m.rhs = new IAction[ 1 ];

        FreeFormLine fl = new FreeFormLine();
        fl.setText( "Person()" );
        m.lhs[ 0 ] = fl;

        FreeFormLine fr = new FreeFormLine();
        fr.setText( "fun()" );
        m.rhs[ 0 ] = fr;

        String drl = RuleModelDRLPersistenceImpl.getInstance().marshal( m );
        assertNotNull( drl );

        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        RuleModel m_ = RuleModelDRLPersistenceImpl.getInstance().unmarshal( drl,
                                                                            Collections.EMPTY_LIST,
                                                                            dmo );
        assertEquals( 1,
                      m_.lhs.length );
        assertEquals( 1,
                      m_.rhs.length );

        assertEquals( "Person",
                      ( (FactPattern) m_.lhs[ 0 ] ).getFactType() );
        assertEquals( "fun()",
                      ( (FreeFormLine) m_.rhs[ 0 ] ).getText() );

    }

    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();
        m.name = "complex";

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        final FactPattern pat = new FactPattern( "Person" );
        pat.setBoundName( "p1" );
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFactType( "Person" );
        con.setFieldBinding( "f1" );
        con.setFieldName( "age" );
        con.setOperator( "<" );
        con.setValue( "42" );
        pat.addConstraint( con );

        m.addLhsItem( pat );

        final CompositeFactPattern comp = new CompositeFactPattern( "not" );
        comp.addFactPattern( new FactPattern( "Cancel" ) );
        m.addLhsItem( comp );

        final ActionUpdateField set = new ActionUpdateField();
        set.setVariable( "p1" );
        set.addFieldValue( new ActionFieldValue( "status",
                                                 "rejected",
                                                 DataType.TYPE_STRING ) );
        m.addRhsItem( set );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        final DSLSentence sen = new DSLSentence();
        sen.setDefinition( "Send an email to {administrator}" );

        m.addRhsItem( sen );
        return m;
    }

    @Test
    public void testLoadEmpty() {
        PackageDataModelOracle dmo = mock( PackageDataModelOracle.class );
        RuleModel m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( "",
                                                                           Collections.EMPTY_LIST,
                                                                           dmo );
        assertNotNull( m );

        m = RuleModelDRLPersistenceImpl.getInstance().unmarshal( "",
                                                                 Collections.EMPTY_LIST,
                                                                 dmo );
        assertNotNull( m );
    }

    @Test
    public void testIncompleteFieldConstraintStringWithNull() {
        String expected = "" +
                "rule \"rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Message( text == \"\" )\n" +
                "  then\n" +
                "end\n";

        final RuleModel m = new RuleModel();
        m.name = "rule";

        final FactPattern pat = new FactPattern( "Message" );
        m.addLhsItem( pat );

        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "text" );
        con.setOperator( "==" );
        con.setValue( null );
        con.setFieldType( DataType.TYPE_STRING );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        pat.addConstraint( con );

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testIncompleteFieldConstraintStringWithNonNull() {
        String expected = "" +
                "rule \"rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Message( text == \"\" )\n" +
                "  then\n" +
                "end\n";

        final RuleModel m = new RuleModel();
        m.name = "rule";

        final FactPattern pat = new FactPattern( "Message" );
        m.addLhsItem( pat );

        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "text" );
        con.setOperator( "==" );
        con.setValue( "" );
        con.setFieldType( DataType.TYPE_STRING );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        pat.addConstraint( con );

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testIncompleteFieldConstraintNonStringWithNull() {
        String expected = "" +
                "rule \"rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Message()\n" +
                "  then\n" +
                "end\n";

        final RuleModel m = new RuleModel();
        m.name = "rule";

        final FactPattern pat = new FactPattern( "Message" );
        m.addLhsItem( pat );

        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "number" );
        con.setOperator( "==" );
        con.setValue( null );
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        pat.addConstraint( con );

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testIncompleteFieldConstraintNonStringWithNonNull() {
        String expected = "" +
                "rule \"rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    Message()\n" +
                "  then\n" +
                "end\n";

        final RuleModel m = new RuleModel();
        m.name = "rule";

        final FactPattern pat = new FactPattern( "Message" );
        m.addLhsItem( pat );

        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldName( "number" );
        con.setOperator( "==" );
        con.setValue( "" );
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        pat.addConstraint( con );

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSFactBindingZeroBound() {

        RuleModel m = new RuleModel();
        m.addAttribute( new RuleAttribute( "dialect",
                                           "mvel" ) );
        m.name = "test";

        ActionInsertFact ai0 = new ActionInsertFact( "Person" );
        ai0.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        ActionInsertFact ai1 = new ActionInsertFact( "Person" );
        ai1.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        m.addRhsItem( ai0 );
        m.addRhsItem( ai1 );

        String expected = "rule \"test\" \n"
                + "dialect \"mvel\"\n"
                + "when"
                + "then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( 55 ); \n"
                + "insert( fact0 ); \n"
                + "Person fact1 = new Person(); \n"
                + "fact1.setField1( 55 ); \n"
                + "insert( fact1 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSFactBindingFirstBound() {

        RuleModel m = new RuleModel();
        m.name = "test";

        ActionInsertFact ai0 = new ActionInsertFact( "Person" );
        ai0.setBoundName( "fact0" );
        ai0.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        ActionInsertFact ai1 = new ActionInsertFact( "Person" );
        ai1.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        m.addRhsItem( ai0 );
        m.addRhsItem( ai1 );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        String expected = "rule \"test\" \n"
                + "dialect \"mvel\"\n"
                + "when"
                + "then \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( 55 ); \n"
                + "insert( fact0 ); \n"
                + "Person fact1 = new Person(); \n"
                + "fact1.setField1( 55 ); \n"
                + "insert( fact1 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSFactBindingLastBound() {

        RuleModel m = new RuleModel();
        m.name = "test";

        ActionInsertFact ai0 = new ActionInsertFact( "Person" );
        ai0.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        ActionInsertFact ai1 = new ActionInsertFact( "Person" );
        ai1.setBoundName( "fact0" );
        ai1.addFieldValue( new ActionFieldValue( "field1",
                                                 "55",
                                                 DataType.TYPE_NUMERIC_LONG ) );
        m.addRhsItem( ai0 );
        m.addRhsItem( ai1 );

        String result = RuleModelDRLPersistenceImpl.getInstance().marshal( m );

        String expected = "rule \"test\" \n"
                + "dialect \"mvel\"\n"
                + "when"
                + "then \n"
                + "Person fact1 = new Person(); \n"
                + "fact1.setField1( 55 ); \n"
                + "insert( fact1 ); \n"
                + "Person fact0 = new Person(); \n"
                + "fact0.setField1( 55 ); \n"
                + "insert( fact0 ); \n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testEmptyFreeForm() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1058247
        RuleModel m = new RuleModel();
        m.name = "Empty FreeFormLine";
        m.lhs = new IPattern[ 1 ];
        m.rhs = new IAction[ 1 ];

        FreeFormLine fl = new FreeFormLine();
        m.lhs[ 0 ] = fl;

        FreeFormLine fr = new FreeFormLine();
        m.rhs[ 0 ] = fr;

        String drl = ruleModelPersistence.marshal( m );
        assertNotNull( drl );
    }

    @Test
    public void testRHSSetFieldWithVariable() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1077212
        RuleModel m = new RuleModel();
        m.name = "variable";

        FactPattern p = new FactPattern( "Person" );
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con.setFieldName( "field1" );
        con.setOperator( "==" );
        con.setValue( "44" );
        con.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        con.setFieldBinding( "$f" );
        p.addConstraint( con );

        m.addLhsItem( p );

        ActionInsertFact ai = new ActionInsertFact( "Person" );
        ActionFieldValue acv = new ActionFieldValue( "field1",
                                                     "=$f",
                                                     DataType.TYPE_OBJECT );
        acv.setNature( FieldNatureType.TYPE_VARIABLE );
        ai.addFieldValue( acv );
        m.addRhsItem( ai );

        String expected = "rule \"variable\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Person( $f : field1 == 44 )\n"
                + "then\n"
                + "Person fact0 = new Person();\n"
                + "fact0.setField1( $f );\n"
                + "insert( fact0 );\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSFormula() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1087690
        RuleModel m = new RuleModel();
        m.name = "test";

        FactPattern p = new FactPattern( "Number" );
        m.addLhsItem( p );

        SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setValue( "true" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_PREDICATE );
        p.addConstraint( con1 );

        SingleFieldConstraintEBLeftSide con2 = new SingleFieldConstraintEBLeftSide();
        con2.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con2.getExpressionLeftSide().appendPart( new ExpressionMethod( "intValue",
                                                                       "int",
                                                                       DataType.TYPE_NUMERIC_INTEGER ) );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setOperator( "==" );
        con2.setValue( "0" );
        p.addConstraint( con2 );

        String expected = "rule \"test\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Number( eval( true ), intValue() == 0 )\n"
                + "then\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testLHSReturnType() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1087690
        RuleModel m = new RuleModel();
        m.name = "test";

        FactPattern p = new FactPattern( "Number" );
        m.addLhsItem( p );

        SingleFieldConstraint con1 = new SingleFieldConstraint();
        con1.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        con1.setFieldName( "this" );
        con1.setOperator( "!= null" );
        con1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        p.addConstraint( con1 );

        SingleFieldConstraintEBLeftSide con2 = new SingleFieldConstraintEBLeftSide();
        con2.getExpressionLeftSide().appendPart( new ExpressionUnboundFact( p.getFactType() ) );
        con2.getExpressionLeftSide().appendPart( new ExpressionMethod( "intValue",
                                                                       "int",
                                                                       DataType.TYPE_NUMERIC_INTEGER ) );
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setOperator( "==" );
        con2.setValue( "0" );
        p.addConstraint( con2 );

        String expected = "rule \"test\"\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "Number( this != null, intValue() == 0 )\n"
                + "then\n"
                + "end";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSChangeMultipleFieldsModifyBoth() {
        String expected = "" +
                "rule \"my rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person()\n" +
                "  then\n" +
                "    modify( $p ) {\n" +
                "      setName( \"Fred\" ),\n" +
                "      setAge( 55 )\n" +
                "    }\n" +
                "end\n";
        final RuleModel m = new RuleModel();

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "Person" );
        factPattern.setBoundName( "$p" );
        m.lhs = new IPattern[]{ factPattern };

        ActionUpdateField auf = new ActionUpdateField();
        auf.setVariable( "$p" );
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setField( "name" );
        afv1.setType( DataType.TYPE_STRING );
        afv1.setNature( FieldNatureType.TYPE_LITERAL );
        afv1.setValue( "Fred" );
        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setField( "age" );
        afv2.setType( DataType.TYPE_NUMERIC_INTEGER );
        afv2.setNature( FieldNatureType.TYPE_LITERAL );
        afv2.setValue( "55" );

        auf.setFieldValues( new ActionFieldValue[]{ afv1, afv2 } );
        m.rhs = new IAction[]{ auf };

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSChangeMultipleFieldsModifyOneUpdateOther() {
        String expected = "" +
                "rule \"my rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person()\n" +
                "  then\n" +
                "    modify( $p ) {\n" +
                "      setName( \"Fred\" )\n" +
                "    }\n" +
                "  $p.setAge( 55 );\n" +
                "end\n";
        final RuleModel m = new RuleModel();

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "Person" );
        factPattern.setBoundName( "$p" );
        m.lhs = new IPattern[]{ factPattern };

        ActionUpdateField auf = new ActionUpdateField();
        auf.setVariable( "$p" );
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setField( "name" );
        afv1.setType( DataType.TYPE_STRING );
        afv1.setNature( FieldNatureType.TYPE_LITERAL );
        afv1.setValue( "Fred" );

        auf.setFieldValues( new ActionFieldValue[]{ afv1 } );

        ActionSetField asf = new ActionSetField();
        asf.setVariable( "$p" );
        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setField( "age" );
        afv2.setType( DataType.TYPE_NUMERIC_INTEGER );
        afv2.setNature( FieldNatureType.TYPE_LITERAL );
        afv2.setValue( "55" );

        asf.setFieldValues( new ActionFieldValue[]{ afv2 } );

        m.rhs = new IAction[]{ auf, asf };

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

    @Test
    public void testRHSChangeMultipleFieldsBlockModify() {
        String expected = "" +
                "rule \"my rule\" \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $p : Person()\n" +
                "  then\n" +
                "    modify( $p ) {\n" +
                "      setName( \"Fred\" ),\n" +
                "      setAge( 55 )\n" +
                "    }\n" +
                "    $p.setGender( \"X\" );" +
                "end\n";
        final RuleModel m = new RuleModel();

        FactPattern factPattern = new FactPattern();
        factPattern.setFactType( "Person" );
        factPattern.setBoundName( "$p" );
        m.lhs = new IPattern[]{ factPattern };

        ActionUpdateField auf1 = new ActionUpdateField();
        auf1.setVariable( "$p" );
        ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setField( "name" );
        afv1.setType( DataType.TYPE_STRING );
        afv1.setNature( FieldNatureType.TYPE_LITERAL );
        afv1.setValue( "Fred" );

        auf1.setFieldValues( new ActionFieldValue[]{ afv1 } );

        ActionSetField asf = new ActionSetField();
        asf.setVariable( "$p" );
        ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setField( "gender" );
        afv2.setType( DataType.TYPE_STRING );
        afv2.setNature( FieldNatureType.TYPE_LITERAL );
        afv2.setValue( "X" );

        asf.setFieldValues( new ActionFieldValue[]{ afv2 } );

        ActionUpdateField auf2 = new ActionUpdateField();
        auf2.setVariable( "$p" );
        ActionFieldValue afv3 = new ActionFieldValue();
        afv3.setField( "age" );
        afv3.setType( DataType.TYPE_NUMERIC_INTEGER );
        afv3.setNature( FieldNatureType.TYPE_LITERAL );
        afv3.setValue( "55" );

        auf2.setFieldValues( new ActionFieldValue[]{ afv3 } );

        m.rhs = new IAction[]{ auf1, asf, auf2 };

        m.name = "my rule";

        checkMarshalling( expected,
                          m );
    }

}
