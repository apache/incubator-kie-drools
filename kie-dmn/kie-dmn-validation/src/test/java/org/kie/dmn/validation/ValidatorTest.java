/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.DMNInputRuntimeTest;
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.core.imports.ImportsTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.v1_3.DMN13specificTest;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorTest extends AbstractValidatorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorTest.class);

    static final DMNValidator validator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));
    static final DMNValidator.ValidatorBuilder validatorBuilder = validator.validateUsing(DMNValidator.Validation.VALIDATE_SCHEMA, DMNValidator.Validation.VALIDATE_MODEL);

    @Test
    void dryRun() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", DMNInputRuntimeTest.class );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat(dmnModel).isNotNull();

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(dmnModel).isNotNull();
        
        DMNValidatorFactory.newValidator().validate(definitions);
    }

    @Test
    void macdInputDefinitions() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "MACD-enhanced_iteration.dmn", DMNInputRuntimeTest.class );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_6cfe7d88-6741-45d1-968c-b61a597d0964", "MACD-enhanced iteration" );
        assertThat(dmnModel).isNotNull();

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(dmnModel).isNotNull();

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);

        assertThat(messages).as(messages.toString()).hasSize(0);
    }

    @Test
    void macdInputReader() throws IOException {
        try (final Reader reader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/org/kie/dmn/core/MACD-enhanced_iteration.dmn")))) {
            List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(reader, VALIDATE_MODEL, VALIDATE_COMPILATION);
            assertThat(messages).withFailMessage(messages.toString()).hasSize(0);
        }
    }

    @Test
    void invalidXml() throws URISyntaxException {
        List<DMNMessage> validateXML = validator.validate( new File(this.getClass().getResource( "invalidXml.dmn" ).toURI()), DMNValidator.Validation.VALIDATE_SCHEMA);
        assertThat(validateXML).withFailMessage(ValidatorUtil.formatMessages(validateXML)).hasSize(1);
        assertThat(validateXML.get( 0 ).getMessageType()).withFailMessage(validateXML.get( 0 ).toString()).isEqualTo(DMNMessageType.FAILED_XML_VALIDATION);
        assertThat( validateXML.get(0).getPath()).contains("invalidXml.dmn");
    }

    @Test
    void invocationMissingExpr() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_MISSING_EXPR.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThan(0);
        assertThat(validate.get(0).getMessageType()).withFailMessage(validate.get(0).toString()).isEqualTo(DMNMessageType.MISSING_EXPRESSION);
    }

    @Test
    void nameIsValid() {
        List<DMNMessage> validate = validator.validate( getReader( "NAME_IS_VALID.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void name_invalidEmptyName() {
        List<DMNMessage> validate = validator.validate( getReader( "DROOLS-1447.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(5);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.FAILED_XML_VALIDATION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.VARIABLE_NAME_MISMATCH))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME) && p.getSourceId().equals("_5e43b55c-888e-443c-b1b9-80e4aa6746bd"))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME) && p.getSourceId().equals("_b1e4588e-9ce1-4474-8e4e-48dbcdb7524b"))).isTrue();
    }

    @Test
    void name_emptyEmptyModelName() {
        List<DMNMessage> validate = validator.validate( getReader( "EmptyModelName.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME) && p.getSourceId().equals("_f27bb64b-6fc7-4e1f-9848-11ba35e0df44"))).isTrue();
    }

    @Test
    void drgelemNotUnique() {
        List<DMNMessage> validate = validator.validate( getReader( "DRGELEM_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATE_NAME))).isTrue();
    }

    @Test
    void formalParamDuplicated() {
        List<DMNMessage> validate = validator.validate( getReader( "FORMAL_PARAM_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(3);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATED_PARAM))).isTrue();
    }

    @Test
    void invocationInconsistentParamNames() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_INCONSISTENT_PARAM_NAMES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThan(0);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    @Disabled("Needs to be improved as invocations can be used to invoke functions node defined in BKMs. E.g., FEEL built in functions, etc.")
    void invocationMissingTarget() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGET.dmn" );
        List<DMNMessage> validate = validator.validate(definitions);
        
//        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVOCATION_MISSING_TARGET ) ) );
    }

    @Disabled("known current limitation")
    @Test
    void invocation_missing_targetRbis() {
        Definitions definitions = utilDefinitions( "INVOCATION_MISSING_TARGETbis.dmn");
        List<DMNMessage> validate = validator.validate(definitions);
        
//        assertTrue( validate.stream().anyMatch( p -> p.getMessageType().equals( DMNMessageType.INVOCATION_MISSING_TARGET ) ) );
    }

    @Test
    void invocationWrongParamCount() {
        List<DMNMessage> validate = validator.validate( getReader( "INVOCATION_WRONG_PARAM_COUNT.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThan(0);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.PARAMETER_MISMATCH))).isTrue();
    }

    @Test
    void itemcompDuplicated() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMCOMP_DUPLICATED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATED_ITEM_DEF))).isTrue();
    }

    @Test
    void itemdefNotUnique() {
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(3);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATED_ITEM_DEF))).isTrue();
    }

    @Test
    void itemdefNotUniqueDrools1450() {
        // DROOLS-1450
        List<DMNMessage> validate = validator.validate( getReader( "ITEMDEF_NOT_UNIQUE_DROOLS-1450.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void relationDupColumn() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_DUP_COLUMN.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.DUPLICATED_RELATION_COLUMN))).isTrue();
    }

    @Test
    void relationRowCellNotliteral() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELL_NOTLITERAL.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.RELATION_CELL_NOT_LITERAL))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
    }

    @Test
    void relationRowCellcountmismatch() {
        List<DMNMessage> validate = validator.validate( getReader( "RELATION_ROW_CELLCOUNTMISMATCH.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.RELATION_CELL_COUNT_MISMATCH))).isTrue();
    }

    @Test
    void mortgageRecommender() {
        // This file has a gazillion errors. The goal of this test is simply check that the validator itself is not blowing up
        // and raising an exception. The errors in the file itself are irrelevant.
        List<DMNMessage> validate = validator.validate( getReader( "MortgageRecommender.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).isNotEmpty();
    }

    @Test
    void reqauth_not_knowledgesourcEbis() {
        // DROOLS-1435
        List<DMNMessage> validate = validator.validate( getReader( "REQAUTH_NOT_KNOWLEDGESOURCEbis.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
    }

    @Test
    void variableLeadingTrailingSpaces() {
        List<DMNMessage> validate = validator.validate( getReader( "VARIABLE_LEADING_TRAILING_SPACES.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).isNotEmpty();
        assertThat(validate).anySatisfy(p -> {
            assertThat(p.getMessageType()).isEqualTo(DMNMessageType.INVALID_NAME);
            assertThat(p.getSourceId()).isEqualTo("_dd662d27-7896-42cb-9d14-bd74203bdbec");
        });
        assertThat(validate).anySatisfy(p -> {
            assertThat(p.getMessageType()).isEqualTo(DMNMessageType.INVALID_NAME);
            assertThat(p.getSourceId()).isEqualTo("_1f54fd51-6805-4280-b576-607450f85edd");
        });
    }

    @Test
    void nameNotNormalized() {
        List<DMNMessage> validate = validator.validate( getReader( "NAME_NOT_NORMALIZED.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).isNotEmpty();
        assertThat(validate).anySatisfy(p -> {
            assertThat(p.getMessageType()).isEqualTo(DMNMessageType.INVALID_NAME);
            assertThat(p.getSourceId()).isEqualTo("_7E95B3C8-9276-46EA-87D4-22FDE87DC039");
        });
        assertThat(validate).anySatisfy(p -> {
            assertThat(p.getMessageType()).isEqualTo(DMNMessageType.INVALID_NAME);
            assertThat(p.getSourceId()).isEqualTo("_07210027-8B43-4DA0-8C0D-69D3E695D23D");
        });
    }

    @Test
    void unknownVariable() {
        List<DMNMessage> validate = validator.validate( getReader( "UNKNOWN_VARIABLE.dmn" ), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL))).isTrue();
    }

    @Test
    void unknownOperator() {
        List<DMNMessage> validate = validator.validate( getReader( "UNKNOWN_OPERATOR.dmn" ),
                                                        VALIDATE_SCHEMA,
                                                        VALIDATE_MODEL,
                                                        VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSizeGreaterThan(0);
    }

    @Test
    void validation() {
        List<DMNMessage> validate = validator.validate( getReader( "validation.dmn" ), VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(7);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.INVALID_NAME))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL))).isTrue();
        // on node DTI the `Loan Payment` is of type `tLoanPayment` hence the property is `monthlyAmount`, NOT `amount` as reported in the model FEEL expression: (Loan Payment.amount+...
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL))).isTrue();
    }

    @Test
    void usingSemanticNamespacePrefix() {
        // DROOLS-2419
        List<DMNMessage> validate = validator.validate(getReader("UsingSemanticNS.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void usingSemanticNamespacePrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = validator.validate(getReader("Hello_World_semantic_namespace_with_extensions.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void noPrefixAndExtensions() {
        // DROOLS-2447
        List<DMNMessage> validate = validator.validate(getReader("Hello_World_no_prefix_with_extensions.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void relationwithemptycell() {
        // DROOLS-2439
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("relation_with_empty_cell.dmn", DMNRuntimeTest.class);
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_99a00903-2943-47df-bab1-a32f276617ea", "Relation with empty cell");
        assertThat(dmnModel).isNotNull();

        Definitions definitions = dmnModel.getDefinitions();
        assertThat(dmnModel).isNotNull();

        List<DMNMessage> messages = DMNValidatorFactory.newValidator().validate(definitions, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(messages).withFailMessage(messages.toString()).hasSize(0);
    }

    @Test
    void relationwithemptycellJustValidator() {
        // DROOLS-2439
        List<DMNMessage> validate = validator.validate(getReader("relation_with_empty_cell.dmn", DMNRuntimeTest.class), VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void boxedInvocationMissingExpression() {
        // DROOLS-2813 DMN boxed invocation missing expression NPE and Validator issue
        List<DMNMessage> validate = validator.validate(getReader("DROOLS-2813-NPE-BoxedInvocationMissingExpression.dmn", DMNRuntimeTest.class), VALIDATE_MODEL);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) && p.getSourceId().equals("_a111c4df-c5b5-4d84-81e7-3ec735b50d06"))).isTrue();
    }

    @Test
    void dMNv12Ch11Modified() {
        // DROOLS-2832
        List<DMNMessage> validate = validator.validate(getReader("v1_2/ch11MODIFIED.dmn", DMNRuntimeTest.class),
                                                       VALIDATE_SCHEMA, 
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void dMNv12Ch11() {
        // DROOLS-2832
        List<DMNMessage> validate = validator.validate(getReader("DMNv12_ch11.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL);

        // DMN v1.2 CH11 example for Adjudication does not define decision logic nor typeRef:
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(2);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF))).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                p.getSourceId().equals("d_Adjudication"))).isTrue();
    }

    @Test
    void decisionServiceCompiler20180830() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = validator.validate(getReader("DecisionServiceABC.dmn", DMNDecisionServicesTest.class),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void decisionServiceCompiler20180830DMNV12() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        List<DMNMessage> validate = validator.validate(getReader("DecisionServiceABC_DMN12.dmn", org.kie.dmn.core.v1_2.DMNDecisionServicesTest.class),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);

        // DMNMessage{ severity=WARN, type=MISSING_TYPE_REF, message='Variable named 'Decision Service ABC' is missing its type reference on node 'Decision Service ABC'', sourceId='_63d05cff-8e3b-4dad-a355-fd88f8bcd613', exception='', feelEvent=''}
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_TYPE_REF) && p.getSourceId().equals("_63d05cff-8e3b-4dad-a355-fd88f8bcd613"))).isTrue();
    }

    @Test
    void decisionService20181008() {
        // DROOLS-3087 DMN Validation of DecisionService referencing a missing import
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_MODEL, VALIDATE_COMPILATION)
                                             .theseModels(getReader("DSWithImport20181008-ModelA.dmn"),
                                                          getReader("DSWithImport20181008-ModelB.dmn"));
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);

        List<DMNMessage> missingDMNImport = validator.validateUsing(VALIDATE_MODEL)
                                                     .theseModels(getReader("DSWithImport20181008-ModelA.dmn"),
                                                                  getReader("DSWithImport20181008-ModelB-missingDMNImport.dmn"));
        assertThat(missingDMNImport.stream().filter(p -> p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).hasSize(2); // on Decision and Decision Service missing to locate the dependency given Import is omitted.
    }

    @Test
    void invalidFunctionNameInvocation() {
        List<DMNMessage> validate = validator.validate(getReader("invalidFunctionNameInvocation.dmn"),
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING && p.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).hasSize(1);
    }

    @Test
    void decisionNoExpr() {
        // DROOLS-4765 DMN validation rule alignment for missing expression
        List<DMNMessage> validate = validator.validate(getReader("noExpr.dmn", DMNRuntimeTest.class),
                                                       VALIDATE_MODEL); // this test ensures the WARN for missing expr on the Decision node also applies when using static model validation rules (before compilation)
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().filter(p -> p.getLevel() == Level.WARNING &&
                                                 p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                                                 p.getSourceId().equals("_cdd03786-d1ab-47b5-ba05-df830458dc62"))).hasSize(1);
    }

    @Test
    void validateSchemaAndModels() {
        // DROOLS-4773 DMN Validator fluent builder schema & analysis using reader
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL)
                                             .theseModels(getReader("base join.dmn", ImportsTest.class),
                                                          getReader("use join.dmn", ImportsTest.class));
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void dMNv13Simple() {
        List<DMNMessage> validate = validator.validate(getReader("simple.dmn", DMN13specificTest.class),
                                                       VALIDATE_SCHEMA,
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void dMNv13Ch11example1() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("Financial.dmn", DMN13specificTest.class),
                                                          getReader("Chapter 11 Example.dmn", DMN13specificTest.class));
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(3);
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) &&
                p.getSourceId().equals("_4bd33d4a-741b-444a-968b-64e1841211e7"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.ERROR &&
                p.getMessageType().equals(DMNMessageType.INVALID_NAME) &&
                p.getSourceId().equals("_96b30012-a6e7-4545-89d3-068ec722469c"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
    }

    @Test
    void somethingInBetweenOC() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("somethingInBetween.dmn"));
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(1);
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.ERROR &&
                p.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL) &&
                p.getSourceId().equals("_841ed91c-db69-401e-890b-08a5bf44222d"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
    }

    @Test
    void dMNv13Ch11example2() {
        List<DMNMessage> validate = validator.validateUsing(VALIDATE_SCHEMA,
                                                            VALIDATE_MODEL,
                                                            VALIDATE_COMPILATION)
                                             .theseModels(getReader("Recommended Loan Products.dmn", DMN13specificTest.class),
                                                          getReader("Loan info.dmn", DMN13specificTest.class));
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }

    @Test
    void dttyperef() {
        List<DMNMessage> validate = validator.validate(getReader("wrongxml/dttyperef.dmn"), VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(1);
        DMNMessage v0 = validate.get(0);
        assertThat(v0.getLevel()).isEqualTo(Level.ERROR);
        assertThat(v0.getMessageType()).isEqualTo(DMNMessageType.MISSING_TYPE_REF);
        assertThat(v0.getSourceId()).isEqualTo("_99FC159F-0D94-45C3-A9BD-F1388017A5D4");
    }

    @Test
    void bkmAndBindingWarnLevel() {
        // DROOLS-4875 DMN validation message alignment to DMN XSD constraint
        List<DMNMessage> validate = validator.validate(getReader("bkmAndBinding.dmn"),
                                                       VALIDATE_SCHEMA,
                                                       VALIDATE_MODEL,
                                                       VALIDATE_COMPILATION);
        assertThat(validate.stream().allMatch(p -> p.getLevel() == Level.WARNING)).as(ValidatorUtil.formatMessages(validate)).isTrue();
        assertThat(validate).withFailMessage(ValidatorUtil.formatMessages(validate)).hasSize(4);
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                p.getSourceId() != null &&
                p.getSourceId().equals("_3ce3c41a-450a-40d1-9e9c-09180cd29879"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
        assertThat(validate.stream().anyMatch(p -> p.getLevel() == Level.WARNING &&
                ((DMNElement) ((DMNModelInstrumentedBase) p.getSourceReference()).getParent()).getId().equals("_d8b0c243-3fb6-40ec-a29c-28f8bdb92e13"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
    }

    @Test
    void informationItemMissingTypeRefSC() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_COMPILATION);
    }

    @Test
    void informationItemMissingTypeRefSM() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_MODEL);
    }

    @Test
    void informationItemMissingTypeRefSMC() {
        // DROOLS-5152 DMN align message level for missing typeRef attribute
        checkInformationItemMissingTypeRef(VALIDATE_SCHEMA, VALIDATE_MODEL, VALIDATE_COMPILATION);
    }

    @Test
    void validateAllValidSharedModels() throws IOException {
        String modelFilesPath = "valid_models/";
        URL modelFilesJarURL = Collections.list(Thread.currentThread().getContextClassLoader().getResources(
                "valid_models/"))
                .stream()
                .filter(url -> url.getProtocol().equals("jar"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve jar containing " + modelFilesPath));
        String modelFilesJarFile = modelFilesJarURL.getFile();
        String jarPath = modelFilesJarFile.substring(modelFilesJarFile.lastIndexOf(":") + 1,
                                                     modelFilesJarFile.lastIndexOf("!"));
        final JarFile jarFile = new JarFile(new File(jarPath));
        testDirectoryInJar(jarFile, modelFilesPath);
    }

    private void testDirectoryInJar(JarFile jarFile, String directory) {
        List<String> allFiles = Collections.list(jarFile.entries())
                .stream()
                .filter(entry -> entry.getName().startsWith(directory) && !entry.isDirectory())
                .map(ZipEntry::getName)
                .toList();
        testFiles(allFiles);
    }

    private void testFiles(List<String> modelFiles) {
        Resource[] resources = modelFiles.stream()
                .map(ClassPathResource::new)
                .toArray(value -> new Resource[modelFiles.size()]);
        List<DMNMessage> dmnMessages = validatorBuilder.theseModels(resources);
        assertNotNull(dmnMessages);
        dmnMessages.forEach(dmnMessage -> LOGGER.error(dmnMessage.toString()));
        assertTrue(dmnMessages.isEmpty());
    }


    private Definitions utilDefinitions(String filename) {
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        try( InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(filename))) ) {
            Definitions definitions = marshaller.unmarshal( isr );
            assertThat(definitions).isNotNull();
            return definitions;
        } catch ( IOException e ) {
            String messageToShow = e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : String.format("Unable to find file %s", filename);
            LOGGER.error(messageToShow);
            LOGGER.debug(messageToShow, e);
            fail(String.format("Unable for the test suite to locate the file %s for validation.", filename));
            return null;
        }
    }

    private void checkInformationItemMissingTypeRef(DMNValidator.Validation... options) {
        List<DMNMessage> validate = validator.validate(getReader("variableMissingTypeRef.dmn"),
                                                       options);
        assertThat(validate.stream().allMatch(p -> p.getLevel() == Level.WARNING &&
                p.getSourceId().equals("_FE47213A-2042-49DE-9A44-65831DA6AD11"))).as(ValidatorUtil.formatMessages(validate)).isTrue();
    }

}
