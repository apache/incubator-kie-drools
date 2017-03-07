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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.stream.Location;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.DMNInputRuntimeTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.validation.Msg;
import org.kie.dmn.validation.ValidationMsg;
import org.kie.dmn.validation.DMNValidator;

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
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );
        
        DMNValidatorFactory.newValidator().validateModel(definitions);
    }
    
    private Definitions utilDefinitions(String filename, String modelName) {
        List<ValidationMsg> validateXML;
        try {
            validateXML = validator.validateSchema( new File(this.getClass().getResource(filename).toURI()) );
            assertThat( "using unit test method utilDefinitions must received a XML valid DMN file", validateXML, IsEmptyCollection.empty() );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Unable for the test suite to locate the file for XML validation.");
        }
        
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
        List<ValidationMsg> validateXML = validator.validateSchema(new File(this.getClass().getResource( "invalidXml.dmn" ).toURI()));
        assertTrue( !validateXML.isEmpty() );
        
        validateXML.forEach(System.err::println);
    }
    
    @Test
    public void testTYPEREF_NO_NS() throws URISyntaxException {
        List<ValidationMsg> validateXML = validator.validateSchema(new File(this.getClass().getResource( "TYPEREF_NO_NS.dmn" ).toURI()));
        assertTrue( !validateXML.isEmpty() );
        
        validateXML.forEach(System.err::println);
    }
    
    @Test
    public void testBKM_MISSING_EXPR() {
        Definitions definitions = utilDefinitions( "BKM_MISSING_EXPR.dmn", "BKM_MISSING_EXPR" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.BKM_MISSING_EXPR ) ) );
    }
    
    @Test
    public void testBKM_MISSING_VAR() {
        Definitions definitions = utilDefinitions( "BKM_MISSING_VAR.dmn", "BKM_MISSING_VAR" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.BKM_MISSING_VAR ) ) );
    }
    
    @Test
    public void testCONTEXT_DUP_ENTRY() {
        Definitions definitions = utilDefinitions( "CONTEXT_DUP_ENTRY.dmn", "CONTEXT_DUP_ENTRY" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.CONTEXT_DUP_ENTRY ) ) );
    }
    
    @Test
    public void testCONTEXT_ENTRY_NOTYPEREF() {
        Definitions definitions = utilDefinitions( "CONTEXT_ENTRY_NOTYPEREF.dmn", "CONTEXT_ENTRY_NOTYPEREF" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.CONTEXT_ENTRY_NOTYPEREF ) ) );
    }
    
    @Test
    public void testDECISION_MISSING_EXPR() {
        Definitions definitions = utilDefinitions( "DECISION_MISSING_EXPR.dmn", "DECISION_MISSING_EXPR" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DECISION_MISSING_EXPR ) ) );
    }

    @Test
    public void testDECISION_MISSING_VAR() {
        Definitions definitions = utilDefinitions( "DECISION_MISSING_VAR.dmn", "DECISION_MISSING_VAR" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertEquals(1, validate.stream().filter( p -> p.getMessage().equals( Msg.DECISION_MISSING_VAR )).count() );
        
        ValidationMsg msg0 = validate.stream().filter( p -> p.getMessage().equals( Msg.DECISION_MISSING_VAR )).findFirst().get();
        assertEquals(Msg.DECISION_MISSING_VAR, msg0.getMessage());
        
        DMNModelInstrumentedBase base = (DMNModelInstrumentedBase) msg0.getReference();
        Location loc0 = base.getLocation();
        assertEquals("In the DECISION_MISSING_VAR.dmn file, the element Decision faulty here is on line 24. ", 24, loc0.getLineNumber());
    }
    
    @Test
    public void testDECISION_MISSING_VARbis() {
        Definitions definitions = utilDefinitions( "DECISION_MISSING_VARbis.dmn", "DECISION_MISSING_VARbis" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DECISION_MISSING_VAR ) ) );
    }
    
    @Test
    public void testDRGELEM_NOT_UNIQUE() {
        Definitions definitions = utilDefinitions( "DRGELEM_NOT_UNIQUE.dmn", "DRGELEM_NOT_UNIQUE" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DRGELEM_NOT_UNIQUE ) ) );
    }
    
    @Test
    public void testDTABLE_MULTIPLEOUT_NAME() {
        Definitions definitions = utilDefinitions( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn", "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DTABLE_MULTIPLEOUT_NAME ) ) );
    }
    
    @Test
    public void testDTABLE_MULTIPLEOUT_TYPEREF() {
        Definitions definitions = utilDefinitions( "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT.dmn", "DTABLE_MULTIPLEOUTPUT_WRONG_OUTPUT" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DTABLE_MULTIPLEOUT_TYPEREF ) ) );
    }
    
    @Test
    public void testDTABLE_PRIORITY_MISSING_OUTVALS() {
        Definitions definitions = utilDefinitions( "DTABLE_PRIORITY_MISSING_OUTVALS.dmn", "DTABLE_PRIORITY_MISSING_OUTVALS" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DTABLE_PRIORITY_MISSING_OUTVALS ) ) );
    }
    
    @Test
    public void testDTABLE_SINGLEOUT_NONAME() {
        Definitions definitions = utilDefinitions( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn", "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DTABLE_SINGLEOUT_NONAME ) ) );
    }
    
    @Test
    public void testDTABLE_SINGLEOUT_NOTYPEREF() {
        Definitions definitions = utilDefinitions( "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT.dmn", "DTABLE_SINGLEOUTPUT_WRONG_OUTPUT" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.DTABLE_SINGLEOUT_NOTYPEREF ) ) );
    }
    
    @Test
    public void testELEMREF_MISSING_TARGET() {
        Definitions definitions = utilDefinitions( "ELEMREF_MISSING_TARGET.dmn", "ELEMREF_MISSING_TARGET" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.ELEMREF_MISSING_TARGET ) ) );
    }
    
    @Test
    public void testELEMREF_NOHASH() {
        Definitions definitions = utilDefinitions( "ELEMREF_NOHASH.dmn", "ELEMREF_NOHASH" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.ELEMREF_NOHASH ) ) );
    }
    
    @Test
    public void testFORMAL_PARAM_DUPLICATED() {
        Definitions definitions = utilDefinitions( "FORMAL_PARAM_DUPLICATED.dmn", "FORMAL_PARAM_DUPLICATED" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.FORMAL_PARAM_DUPLICATED ) ) );
    }
    
    @Test
    public void testINPUTDATA_MISSING_VAR() {
        Definitions definitions = utilDefinitions( "INPUTDATA_MISSING_VAR.dmn", "INPUTDATA_MISSING_VAR" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.INPUTDATA_MISSING_VAR ) ) );
    }
    
    @Test
    public void testINVOCATION_INCONSISTENT_PARAM_NAMES() {
        Definitions definitions = utilDefinitions( "INVOCATION_INCONSISTENT_PARAM_NAMES.dmn", "INVOCATION_INCONSISTENT_PARAM_NAMES" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.INVOCATION_INCONSISTENT_PARAM_NAMES ) ) );
    }
    
    @Test
    public void testINVOCATION_MISSING_TARGET() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGET.dmn", "INVOCATION_MISSING_TARGET" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.INVOCATION_MISSING_TARGET ) ) );
    }
    
    @Ignore("known current limitation")
    @Test
    public void testINVOCATION_MISSING_TARGETRbis() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGETbis.dmn", "INVOCATION_MISSING_TARGETbis" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.INVOCATION_MISSING_TARGET ) ) );
    }
    
    @Test
    public void testINVOCATION_WRONG_PARAM_COUNT() {
        Definitions definitions = utilDefinitions( "INVOCATION_WRONG_PARAM_COUNT.dmn", "INVOCATION_WRONG_PARAM_COUNT" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.INVOCATION_WRONG_PARAM_COUNT ) ) );
    }
    
    @Test
    public void testITEMCOMP_DUPLICATED() {
        Definitions definitions = utilDefinitions( "ITEMCOMP_DUPLICATED.dmn", "ITEMCOMP_DUPLICATED" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.ITEMCOMP_DUPLICATED ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE() {
        Definitions definitions = utilDefinitions( "ITEMDEF_NOT_UNIQUE.dmn", "ITEMDEF_NOT_UNIQUE" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.ITEMDEF_NOT_UNIQUE ) ) );
    }
    
    @Test
    public void testITEMDEF_NOT_UNIQUE_DROOLS_1450() {
        // DROOLS-1450
        Definitions definitions = utilDefinitions( "ITEMDEF_NOT_UNIQUE_DROOLS-1450.dmn", "ITEMDEF_NOT_UNIQUE" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertFalse( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.ITEMDEF_NOT_UNIQUE ) ) );
    }
    
    @Test
    public void testNAME_INVALID() {
        Definitions definitions = utilDefinitions( "NAME_INVALID.dmn", "NAME_INVALID" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.NAME_INVALID ) ) );
    }
    
    @Test
    public void testNAME_INVALID_bis() {
        Definitions definitions = utilDefinitions( "NAME_INVALID_bis.dmn", "code in list of codes" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.NAME_INVALID ) ) );
        
        /* in the file NAME_INVALID_bis.dmn there are 3 invalid "names" but only the one for the Decision node should be reported.
         * <definitions id="NAME_INVALID" name="code in list of codes" ...
            <decision name="code in list of codes" id="d_GreetingMessage">
             <variable name="code in list of codes" typeRef="feel:string"/>
         */
        assertEquals("functional optimization of valid names to report ", 1, validate.stream().filter( p -> p.getMessage().equals( Msg.NAME_INVALID ) ).count() );
    }
    
    @Test
    public void testRELATION_DUP_COLUMN() {
        Definitions definitions = utilDefinitions( "RELATION_DUP_COLUMN.dmn", "RELATION_DUP_COLUMN" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.RELATION_DUP_COLUMN ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELL_NOTLITERAL() {
        Definitions definitions = utilDefinitions( "RELATION_ROW_CELL_NOTLITERAL.dmn", "RELATION_ROW_CELL_NOTLITERAL" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.RELATION_ROW_CELL_NOTLITERAL ) ) );
    }
    
    @Test
    public void testRELATION_ROW_CELLCOUNTMISMATCH() {
        Definitions definitions = utilDefinitions( "RELATION_ROW_CELLCOUNTMISMATCH.dmn", "RELATION_ROW_CELLCOUNTMISMATCH" );
        List<ValidationMsg> validate = validator.validateModel(definitions);
        
        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.RELATION_ROW_CELLCOUNTMISMATCH ) ) );
    }
        
    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCE() {
        Definitions definitions = utilDefinitions( "REQAUTH_NOT_KNOWLEDGESOURCE.dmn", "REQAUTH_NOT_KNOWLEDGESOURCE" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.REQAUTH_NOT_KNOWLEDGESOURCE ) ) );
    }
    
    @Test
    public void testREQAUTH_NOT_KNOWLEDGESOURCEbis() {
        // DROOLS-1435
        Definitions definitions = utilDefinitions( "REQAUTH_NOT_KNOWLEDGESOURCEbis.dmn", "REQAUTH_NOT_KNOWLEDGESOURCEbis" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        // this test should just pass without any NPE:
        assertTrue( validate.size() > 0 );
    }
    
    @Test
    public void testTYPEREF_NO_FEEL_TYPE() {
        Definitions definitions = utilDefinitions( "TYPEREF_NO_FEEL_TYPE.dmn", "TYPEREF_NO_FEEL_TYPE" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.TYPEREF_NO_FEEL_TYPE ) ) );
    }
    
    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF() {
        Definitions definitions = utilDefinitions( "TYPEREF_NOT_FEEL_NOT_DEF.dmn", "TYPEREF_NOT_FEEL_NOT_DEF" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.TYPEREF_NOT_FEEL_NOT_DEF ) ) );
    }
    
    @Test
    public void testTYPEREF_NOT_FEEL_NOT_DEF_valid() {
        // DROOLS-1433
        // the assumption is that the following document TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn should NOT contain any Msg.TYPEREF_NOT_FEEL_NOT_DEF at all
        // the test also highlight typically in a DMN model many nodes would not define a typeRef, resulting in a large number of false negative
        Definitions definitions = utilDefinitions( "TYPEREF_NOT_FEEL_NOT_DEF_valid.dmn", "TYPEREF_NOT_FEEL_NOT_DEF_valid" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        validate.forEach(m -> System.out.println( m.getMessage()) );
        
        assertTrue( validate.stream().noneMatch( p -> p.getMessage().equals( Msg.TYPEREF_NOT_FEEL_NOT_DEF ) ) );
    }
    
    @Test
    public void testNAME_INVALID_empty_name() {
        assertFalse( FEELParser.isVariableNameValid(null) );
        
        // DROOLS-1447
        assertFalse( FEELParser.isVariableNameValid("")   );
        
        Definitions definitions = utilDefinitions( "DROOLS-1447.dmn", "DROOLS-1447" );
        List<ValidationMsg> validate = validator.validateModel(definitions);

        assertTrue( validate.stream().anyMatch( p -> p.getMessage().equals( Msg.NAME_INVALID ) ) );
    }
}
