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

package org.kie.dmn.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.DMNInputRuntimeTest;
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.core.imports.ImportsTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.v1_3.DMN13specificTest;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

public class ValidatorTest extends AbstractValidatorTest {

    @Test
    public void testDryRun() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", DMNInputRuntimeTest.class );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );
        
        DMNValidatorFactory.newValidator().validate(definitions);
    }

    @Test
    public void testMACDInputDefinitions() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "MACD-enhanced_iteration.dmn", DMNInputRuntimeTest.class );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_6cfe7d88-6741-45d1-968c-b61a597d0964", "MACD-enhanced iteration" );
        assertThat( dmnModel, notNullValue() );

        Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);

        assertThat( messages.toString(), messages.size(), is( 0 ) );
    }

    @Test
    public void testMACDInputReader() throws IOException {
        try (final Reader reader = new InputStreamReader(getClass().getResourceAsStream("/org/kie/dmn/core/MACD-enhanced_iteration.dmn") )) {
            List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(reader, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat( messages.toString(), messages.size(), is( 0 ) );
        }
    }

    private Definitions utilDefinitions(String filename, String modelName) {
//        List<DMNMessage> validateXML;
//        try {
//            validateXML = validator.validate( new File(this.getClass().getResource(filename).toURI()), DMNValidator.Validation.VALIDATE_SCHEMA );
//            assertThat( "using unit test method utilDefinitions must received a XML valid DMN file", validateXML, IsEmptyCollection.empty() );
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            fail("Unable for the test suite to locate the file for XML validation.");
//        }
        
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        try( InputStreamReader isr = new InputStreamReader( getClass().getResourceAsStream( filename ) ) ) {
            Definitions definitions = marshaller.unmarshal( isr );
            assertThat( definitions, notNullValue() );
            return definitions;
        } catch ( IOException e ) {
            e.printStackTrace();
            fail("Unable for the test suite to locate the file for validation.");
        }
        return null;
    }
        
    @Test
    public void testInvalidXml() throws URISyntaxException {
        List<DMNMessage> validateXML = validator.validate( new File(this.getClass().getResource( "invalidXml.dmn" ).toURI()), DMNValidator.Validation.VALIDATE_SCHEMA);
        assertThat( ValidatorUtil.formatMessages( validateXML ), validateXML.size(), is( 1 ) );
        assertThat( validateXML.get( 0 ).toString(), validateXML.get( 0 ).getMessageType(), is( DMNMessageType.FAILED_XML_VALIDATION ) );
    }

    @Test
    public void testINVOCATION_MISSING_EXPR() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), greaterThan(0));
        assertThat( validate.get( 0 ).toString(), validate.get( 0 ).getMessageType(), is( DMNMessageType.MISSING_EXPRESSION ) );
    }

    @Test
    public void testNAME_IS_VALID() {
        List<DMNMessage> validate = validator.validate( getReader( "NAME_IS_VALID.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 0 ) );
    }

    @Test
    public void testNAME_INVALID_empty_name() {
        List<DMNMessage> validate = validator.validate( getReader( "DROOLS-1447.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 4 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.FAILED_XML_VALIDATION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.VARIABLE_NAME_MISMATCH ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) && p.getSourceId().equals( "_5e43b55c-888e-443c-b1b9-80e4aa6746bd" ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) && p.getSourceId().equals( "_b1e4588e-9ce1-4474-8e4e-48dbcdb7524b" ) ) );
    }
    
    @Test
    public void testDRGELEM_NOT_UNIQUE() {
        List<DMNMessage> validate = validator.validate( getReader( "DRGELEM_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATE_NAME ) ) );
    }
    
    @Test
    public void testFORMAL_PARAM_DUPLICATED() {
        List<DMNMessage> validate = validator.validate( getReader( "FORMAL_PARAM_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 3 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_PARAM ) ) );
    }
    
    @Test
    public void testINVOCATION_INCONSISTENT_PARAM_NAMES() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_INCONSISTENT_PARAM_NAMES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), greaterThan(0));
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.PARAMETER_MISMATCH ) ) );
    }
    
    @Test @Ignore( "Needs to be improved as invocations can be used to invoke functions node defined in BKMs. E.g., FEEL built in functions, etc.")
    public void testINVOCATION_MISSING_TARGET() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGET.dmn", "INVOCATION_MISSING_TARGET" );
        List<DMNMessage> validate = validator.validate(definitions);
        
//        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVOCATION_MISSING_TARGET ) ) );
    }
    
    @Ignore("known current limitation")
    @Test
    public void testINVOCATION_MISSING_TARGETRbis() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGETbis.dmn", "INVOCATION_MISSING_TARGETbis" );
        List<DMNMessage> validate = validator.validate(definitions);
        
//        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVOCATION_MISSING_TARGET ) ) );
    }
    
    @Test
    public void testINVOCATION_WRONG_PARAM_COUNT() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_WRONG_PARAM_COUNT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), greaterThan(0));
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.PARAMETER_MISMATCH ) ) );
    }
    
    @Test
    public void testITEMCOMP_DUPLICATED() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMCOMP_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_ITEM_DEF ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_ITEM_DEF ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE_DROOLS_1450() {
        // DROOLS-1450
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE_DROOLS-1450.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 0 ) );
    }
    
    @Test
    public void testRELATION_DUP_COLUMN() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_DUP_COLUMN.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_RELATION_COLUMN ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELL_NOTLITERAL() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELL_NOTLITERAL.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.RELATION_CELL_NOT_LITERAL ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELLCOUNTMISMATCH() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELLCOUNTMISMATCH.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.RELATION_CELL_COUNT_MISMATCH ) ) );
    }

    @Test
    public void testMortgageRecommender() {
        // This file has a gazillion errors. The goal of this test is simply check that the validator itself is not blowing up
        // and raising an exception. The errors in the file itself are irrelevant.
        List<DMNMessage> validate = validator.validate( getReader( "MortgageRecommender.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.isEmpty(), is( false ) );
    }

    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCEbis() {
        // DROOLS-1435
        List<DMNMessage> validate = validator.validate( getReader( "REQAUTH_NOT_KNOWLEDGESOURCEbis.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 1 ) );
    }

    @Test
    public void testVARIABLE_LEADING_TRAILING_SPACES() {
        List<DMNMessage> validate = validator.validate( getReader( "VARIABLE_LEADING_TRAILING_SPACES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
        assertThat( validate.get(0).getSourceId(), is("_dd662d27-7896-42cb-9d14-bd74203bdbec") );
    }

    @Test
    public void testUNKNOWN_VARIABLE() {
        List<DMNMessage> validate = validator.validate( getReader( "UNKNOWN_VARIABLE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ERR_COMPILING_FEEL ) ) );
    }

    @Test
    public void testUNKNOWN_OPERATOR() {
        List<DMNMessage> validate = validator.validate( getReader( "UNKNOWN_OPERATOR.dmn" ),
                                                        VALIDATE_SCHEMA,
                                                        VALIDATE_MODEL,
                                                        VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), greaterThan( 0 ) );
    }

    @Test
    public void testVALIDATION() {
        List<DMNMessage> validate = validator.validate( getReader( "validation.dmn" ), VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( ValidatorUtil.formatMessages( validate ), validate.size(), is( 5 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ERR_COMPILING_FEEL ) ) );
        // on node DTI the `Loan Payment` is of type `tLoanPayment` hence the property is `monthlyAmount`, NOT `amount` as reported in the model FEEL expression: (Loan Payment.amount+...
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ERR_COMPILING_FEEL ) ) );
    }

    @Test
    public void testUsingSemanticNamespacePrefix() {
        // DROOLS-2419
        List<DMNMessage> validate = validator.validate(getReader("UsingSemanticNS.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testUsingSemanticNamespacePrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = validator.validate(getReader("Hello_World_semantic_namespace_with_extensions.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testNoPrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = validator.validate(getReader("Hello_World_no_prefix_with_extensions.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testRelationwithemptycell() {
        // DROOLS-2439
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("relation_with_empty_cell.dmn", DMNRuntimeTest.class);
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_99a00903-2943-47df-bab1-a32f276617ea", "Relation with empty cell");
        assertThat(dmnModel, notNullValue());

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(definitions, notNullValue());

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(messages.toString(), messages.size(), is(0));
    }

    @Test
    public void testRelationwithemptycellJustValidator() {
        // DROOLS-2439
        List<DMNMessage> validate = validator.validate(getReader("relation_with_empty_cell.dmn", DMNRuntimeTest.class), VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testBoxedInvocationMissingExpression() {
        // DROOLS-2813 DMN boxed invocation missing expression NPE and Validator issue
        List<DMNMessage> validate = validator.validate(getReader("DROOLS-2813-NPE-BoxedInvocationMissingExpression.dmn", DMNRuntimeTest.class), VALIDATE_MODEL);
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) && p.getSourceId().equals("_a111c4df-c5b5-4d84-81e7-3ec735b50d06")));
    }

    @Test
    public void testDMNv1_2_ch11Modified() {
        // DROOLS-2832
        List<DMNMessage> validate = validator.validate(getReader("v1_2/ch11MODIFIED.dmn", DMNRuntimeTest.class),
                                                       VALIDATE_SCHEMA, 
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testDMNv1_2_ch11() {
        // DROOLS-2832
        List<DMNMessage> validate = validator.validate(getReader("DMNv12_ch11.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL);

        // DMN v1.2 CH11 example for Adjudication does not define decision logic nor typeRef:
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF)));
        assertTrue(validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                                                   p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                                                   p.getSourceId().equals("d_Adjudication")));
    }

    @Test
    public void testDecisionServiceCompiler20180830() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = validator.validate(getReader("DecisionServiceABC.dmn", DMNDecisionServicesTest.class),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testDecisionServiceCompiler20180830DMNV12() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = validator.validate(getReader("DecisionServiceABC_DMN12.dmn", org.kie.dmn.core.v1_2.DMNDecisionServicesTest.class),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);

        // DMNMessage{ severity=WARN, type=MISSING_TYPE_REF, message='Variable named 'Decision Service ABC' is missing its type reference on node 'Decision Service ABC'', sourceId='_63d05cff-8e3b-4dad-a355-fd88f8bcd613', exception='', feelEvent=''}
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF) && p.getSourceId().equals("_63d05cff-8e3b-4dad-a355-fd88f8bcd613")));
    }

    @Test
    public void testDecisionService20181008() {
        // DROOLS-3087 DMN Validation of DecisionService referencing a missing import
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("DSWithImport20181008-ModelA.dmn"),
                                                          getReader("DSWithImport20181008-ModelB.dmn"));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));

        List<DMNMessage> missingDMNImport = validator.validateUsing(VALIDATE_MODEL)
                                                     .theseModels(getReader("DSWithImport20181008-ModelA.dmn"),
                                                                  getReader("DSWithImport20181008-ModelB-missingDMNImport.dmn"));
        assertThat(missingDMNImport.stream().filter(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)).count(), is(2L)); // on Decision and Decision Service missing to locate the dependency given Import is omitted.
    }

    @Test
    public void testInvalidFunctionNameInvocation() {
        List<DMNMessage> validate = validator.validate(getReader("invalidFunctionNameInvocation.dmn"),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING && p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND)).count(), is(1L));
    }

    @Test
    public void testDecisionNoExpr() {
        // DROOLS-4765 DMN validation rule alignment for missing expression
        List<DMNMessage> validate = validator.validate(getReader("noExpr.dmn", DMNRuntimeTest.class),
                                                       VALIDATE_MODEL); // this test ensures the WARN for missing expr on the Decision node also applies when using static model validation rules (before compilation)
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING &&
                                                 p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                                                 p.getSourceId().equals("_cdd03786-d1ab-47b5-ba05-df830458dc62")).count(),
                   is(1L));
    }

    @Test
    public void testValidateSchemaAndModels() {
        // DROOLS-4773 DMN Validator fluent builder schema & analysis using reader
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL)
                                             .theseModels(getReader("base join.dmn", ImportsTest.class),
                                                          getReader("use join.dmn", ImportsTest.class));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testDMNv1_3_simple() {
        List<DMNMessage> validate = validator.validate(getReader("simple.dmn", DMN13specificTest.class),
                                                       VALIDATE_SCHEMA,
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void testDMNv1_3_ch11example1() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("Financial.dmn", DMN13specificTest.class),
                                                          getReader("Chapter 11 Example.dmn", DMN13specificTest.class));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                                                   p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                                                   p.getSourceId().equals("_4bd33d4a-741b-444a-968b-64e1841211e7")));
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().anyMatch(p -> p.getLevel() == Level.ERROR &&
                                                   p.getMessageType().equals(DMNMessageType.INVALID_NAME) &&
                                                   p.getSourceId().equals("_96b30012-a6e7-4545-89d3-068ec722469c")));
    }

    @Test
    public void testSomethingInBetweenOC() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("somethingInBetween.dmn"));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().anyMatch(p -> p.getLevel() == Level.ERROR &&
                                                   p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL) &&
                                                   p.getSourceId().equals("_841ed91c-db69-401e-890b-08a5bf44222d")));
    }

    @Test
    public void testDMNv1_3_ch11example2() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("Recommended Loan Products.dmn", DMN13specificTest.class),
                                                          getReader("Loan info.dmn", DMN13specificTest.class));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(0));
    }

    @Test
    public void test_dttyperef() {
        List<DMNMessage> validate = validator.validate(getReader("wrongxml/dttyperef.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(1));
        DMNMessage v0 = validate.get(0);
        Assertions.assertThat(v0.getLevel()).isEqualTo(Level.ERROR);
        Assertions.assertThat(v0.getMessageType()).isEqualTo(DMNMessageType.MISSING_TYPE_REF);
        Assertions.assertThat(v0.getSourceId()).isEqualTo("_99FC159F-0D94-45C3-A9BD-F1388017A5D4");
    }

    @Test
    public void testBkmAndBindingWarnLevel() {
        // DROOLS-4875 DMN validation message alignment to DMN XSD constraint
        List<DMNMessage> validate = validator.validate(getReader("bkmAndBinding.dmn"),
                                                       VALIDATE_SCHEMA,
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().allMatch(p -> p.getLevel() == Level.WARNING));
        assertThat(ValidatorUtil.formatMessages(validate), validate.size(), is(2));
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                                                   p.getSourceId() != null &&
                                                   p.getSourceId().equals("_3ce3c41a-450a-40d1-9e9c-09180cd29879")));
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                                                   ((DMNElement) ((DMNModelInstrumentedBase) p.getSourceReference()).getParent()).getId().equals("_d8b0c243-3fb6-40ec-a29c-28f8bdb92e13")));
    }

    @Test
    public void testInformationItemMissingTypeRef_SC() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_COMPILATION);
    }

    @Test
    public void testInformationItemMissingTypeRef_SM() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_MODEL);
    }

    @Test
    public void testInformationItemMissingTypeRef_SMC() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
    }

    private void checkInformationItemMissingTypeRef(DMNValidator.Validation... options) {
        List<DMNMessage> validate = validator.validate(getReader("variableMissingTypeRef.dmn"),
                                                       options);
        assertTrue(ValidatorUtil.formatMessages(validate),
                   validate.stream().allMatch(p -> p.getLevel() == Level.WARNING &&
                                                   p.getSourceId().equals("_FE47213A-2042-49DE-9A44-65831DA6AD11")));
    }

}
