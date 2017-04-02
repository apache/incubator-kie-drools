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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.Location;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.DMNInputRuntimeTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.model.v1_1.Context;
import org.kie.dmn.model.v1_1.ContextEntry;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.Definitions;

public class ValidatorTest {
    
    private static DMNValidator validator;
    @BeforeClass
    public static void init() {
        validator = DMNValidatorFactory.newValidator();
    }
    @AfterClass
    public static void dispose() {
        validator.dispose();
    }

    @Test
    public void testDryRun() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", DMNInputRuntimeTest.class );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );
        
        DMNValidatorFactory.newValidator().validate(definitions);
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

    private Reader getReader( String filename ) {
        return new InputStreamReader( getClass().getResourceAsStream( filename ) );
    }
        
    @Test
    public void testInvalidXml() throws URISyntaxException {
        List<DMNMessage> validateXML = validator.validate( new File(this.getClass().getResource( "invalidXml.dmn" ).toURI()), DMNValidator.Validation.VALIDATE_SCHEMA);
        assertThat( formatMessages( validateXML ), validateXML.size(), is( 1 ) );
        assertThat( validateXML.get( 0 ).toString(), validateXML.get( 0 ).getMessageType(), is( DMNMessageType.FAILED_XML_VALIDATION ) );
    }

    @Test
    public void testBKM_MISSING_EXPR() {
        List<DMNMessage> validate = validator.validate( getReader( "BKM_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertThat( validate.get( 0 ).toString(), validate.get( 0 ).getMessageType(), is( DMNMessageType.MISSING_EXPRESSION ) );
    }

    @Test
    public void testDECISION_MISSING_EXPR() {
        List<DMNMessage> validate = validator.validate( getReader( "DECISION_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertThat( validate.get( 0 ).toString(), validate.get( 0 ).getMessageType(), is( DMNMessageType.MISSING_EXPRESSION ) );
    }

    @Test
    public void testINVOCATION_MISSING_EXPR() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertThat( validate.get( 0 ).toString(), validate.get( 0 ).getMessageType(), is( DMNMessageType.MISSING_EXPRESSION ) );
    }

    @Test
    public void testCONTEXT_MISSING_EXPR() {
        List<DMNMessage> validate = validator.validate( getReader( "CONTEXT_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.FAILED_XML_VALIDATION ) ) ); // this is schema validation
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
    }

    @Test
    public void testCONTEXT_MISSING_ENTRIES() {
        List<DMNMessage> validate = validator.validate( getReader( "CONTEXT_MISSING_ENTRIES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
    }

    @Test
    public void testCONTEXT_ENTRY_MISSING_VARIABLE() {
        List<DMNMessage> validate = validator.validate( getReader( "CONTEXT_ENTRY_MISSING_VARIABLE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_VARIABLE ) ) );
        // check that it reports and error for the second context entry, but not for the last one
        ContextEntry ce = (ContextEntry) validate.get( 0 ).getSourceReference();
        assertThat( ((Context)ce.getParent()).getContextEntry().indexOf( ce ), is( 1 ) );
    }

    @Test
    public void testBKM_MISSING_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "BKM_MISSING_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_VARIABLE ) ) );
    }

    @Test
    public void testDECISION_MISSING_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "DECISION_MISSING_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_VARIABLE ) ) );
    }

    @Test
    public void testDECISION_MISSING_VARbis() {
        List<DMNMessage> validate = validator.validate( getReader( "DECISION_MISSING_VARbis.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_VARIABLE ) ) );
    }

    @Test
    public void testINPUT_MISSING_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "INPUTDATA_MISSING_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_VARIABLE ) ) );
    }

    @Test
    public void testBKM_MISMATCH_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "BKM_MISMATCH_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.VARIABLE_NAME_MISMATCH ) ) );
    }

    @Test
    public void testDECISION_MISMATCH_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "DECISION_MISMATCH_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.VARIABLE_NAME_MISMATCH ) ) );
    }

    @Test
    public void testINPUT_MISMATCH_VAR() {
        List<DMNMessage> validate = validator.validate( getReader( "INPUTDATA_MISMATCH_VAR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.VARIABLE_NAME_MISMATCH ) ) );
    }

    @Test
    public void testTYPEREF_NO_NS() throws URISyntaxException {
        List<DMNMessage> validate = validator.validate( getReader( "TYPEREF_NO_NS.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.FAILED_XML_VALIDATION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.TYPE_DEF_NOT_FOUND ) ) );
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF() {
        List<DMNMessage> validate = validator.validate( getReader( "TYPEREF_NOT_FEEL_NOT_DEF.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.TYPE_DEF_NOT_FOUND ) ) );
    }

    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any DMNMessageTypeId.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        List<DMNMessage> validate = validator.validate( getReader( "TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 0 ) );
    }

    @Test
    public void testNAME_INVALID() {
        List<DMNMessage> validate = validator.validate( getReader( "NAME_INVALID.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }

    @Test
    public void testNAME_INVALID_bis() {
        /* in the file NAME_INVALID_bis.dmn there are 3 invalid "names" but only the one for the Decision node should be reported.
         * <definitions id="NAME_INVALID" name="code in list of codes" ...
            <decision name="code in list of codes" id="d_GreetingMessage">
             <variable name="code in list of codes" typeRef="feel:string"/>
         */
        List<DMNMessage> validate = validator.validate( getReader( "NAME_INVALID_bis.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }

    @Test
    public void testNAME_INVALID_empty_name() {
        List<DMNMessage> validate = validator.validate( getReader( "DROOLS-1447.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 4 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.FAILED_XML_VALIDATION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.VARIABLE_NAME_MISMATCH ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) && p.getSourceId().equals( "_5e43b55c-888e-443c-b1b9-80e4aa6746bd" ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) && p.getSourceId().equals( "_b1e4588e-9ce1-4474-8e4e-48dbcdb7524b" ) ) );
    }

    @Test
    public void testCONTEXT_DUP_ENTRY() {
        List<DMNMessage> validate = validator.validate( getReader( "CONTEXT_DUP_ENTRY.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATE_NAME ) ) );
    }
    
    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF() {
        List<DMNMessage> validate = validator.validate( getReader( "CONTEXT_ENTRY_NOTYPEREF.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
    }
    
    @Test
    public void testDRGELEM_NOT_UNIQUE() {
        List<DMNMessage> validate = validator.validate( getReader( "DRGELEM_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATE_NAME ) ) );
    }
    
    @Test
    public void testDTABLE_MULTIPLEOUT_NAME() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 5 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_TYPE_REF ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
    }
    
    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_OUTPUT_VALUES ) ) );
    }
    
    @Test
    public void testDTABLE_SINGLEOUT_NONAME() {
        List<DMNMessage> validate = validator.validate( getReader( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_NAME ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ILLEGAL_USE_OF_TYPEREF ) ) );
    }
    
    @Test
    public void testELEMREF_MISSING_TARGET() {
        List<DMNMessage> validate = validator.validate( getReader( "ELEMREF_MISSING_TARGET.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.REQ_NOT_FOUND ) ) );
    }
    
    @Test
    public void testELEMREF_NOHASH() {
        List<DMNMessage> validate = validator.validate( getReader( "ELEMREF_NOHASH.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        System.out.println( formatMessages( validate ) );
        assertThat( formatMessages( validate ), validate.size(), is( 3 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_HREF_SYNTAX ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.REQ_NOT_FOUND ) ) );
    }
    
    @Test
    public void testFORMAL_PARAM_DUPLICATED() {
        List<DMNMessage> validate = validator.validate( getReader( "FORMAL_PARAM_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 3 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_PARAM ) ) );
    }
    
    @Test
    public void testINVOCATION_INCONSISTENT_PARAM_NAMES() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_INCONSISTENT_PARAM_NAMES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
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
        assertThat( formatMessages( validate ), validate.size(), is( 3 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.PARAMETER_MISMATCH ) ) );
    }
    
    @Test
    public void testITEMCOMP_DUPLICATED() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMCOMP_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_ITEM_DEF ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_ITEM_DEF ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE_DROOLS_1450() {
        // DROOLS-1450
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE_DROOLS-1450.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 0 ) );
    }
    
    @Test
    public void testRELATION_DUP_COLUMN() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_DUP_COLUMN.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.DUPLICATED_RELATION_COLUMN ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELL_NOTLITERAL() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELL_NOTLITERAL.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 2 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.RELATION_CELL_NOT_LITERAL ) ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.MISSING_EXPRESSION ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELLCOUNTMISMATCH() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELLCOUNTMISMATCH.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.RELATION_CELL_COUNT_MISMATCH ) ) );
    }
        
    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCE() {
        List<DMNMessage> validate = validator.validate( getReader( "REQAUTH_NOT_KNOWLEDGESOURCE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 3 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.REQ_NOT_FOUND ) ) );
    }

    @Test
    public void testMortgageRecommender() {
        // This file has a gazillion errors. The goal of this test is simply check that the validator itself is not blowing up
        // and raising an exception. The errors in the file itself are irrelevant.
        List<DMNMessage> validate = validator.validate( getReader( "MortgageRecommender.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.isEmpty(), is( false ) );
    }

    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCEbis() {
        // DROOLS-1435
        List<DMNMessage> validate = validator.validate( getReader( "REQAUTH_NOT_KNOWLEDGESOURCEbis.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 0 ) );
    }
    
    @Test
    public void testTYPEREF_NO_FEEL_TYPE() {
        List<DMNMessage> validate = validator.validate( getReader( "TYPEREF_NO_FEEL_TYPE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.TYPE_REF_NOT_FOUND ) ) );
    }
    
    @Test
    public void testVARIABLE_LEADING_TRAILING_SPACES() {
        List<DMNMessage> validate = validator.validate( getReader( "VARIABLE_LEADING_TRAILING_SPACES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVALID_NAME ) ) );
        assertThat( validate.get(0).getSourceId(), is("_dd662d27-7896-42cb-9d14-bd74203bdbec") );
    }

    @Test
    public void testUNKNOWN_VARIABLE() {
        List<DMNMessage> validate = validator.validate( getReader( "UNKNOWN_VARIABLE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat( formatMessages( validate ), validate.size(), is( 1 ) );
        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.ERR_COMPILING_FEEL ) ) );
    }

    private String formatMessages(List<DMNMessage> messages) {
        return messages.stream().map( m -> m.toString() ).collect( Collectors.joining( "\n" ) );
    }

}
