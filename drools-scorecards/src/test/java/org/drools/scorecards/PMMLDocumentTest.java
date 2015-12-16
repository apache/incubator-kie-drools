/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.scorecards;

import org.junit.Assert;
import org.dmg.pmml.pmml_4_2.descr.*;
import org.drools.pmml.pmml_4_2.extensions.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.drools.scorecards.ScorecardCompiler.DrlType.INTERNAL_DECLARED_TYPES;

public class PMMLDocumentTest {

    private static PMML pmmlDocument;
    private static ScorecardCompiler scorecardCompiler;

    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(INTERNAL_DECLARED_TYPES);
        scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_c.xls"));
        pmmlDocument = scorecardCompiler.getPMMLDocument();
    }

    @Test
    public void testPMMLDocument() throws Exception {
        Assert.assertNotNull(pmmlDocument);
        String pmml = scorecardCompiler.getPMML();
        Assert.assertNotNull(pmml);
        Assert.assertTrue(pmml.length() > 0);
    }

    @Test
    public void testHeader() throws Exception {
        Header header = pmmlDocument.getHeader();
        assertNotNull(header);
        assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), PMMLExtensionNames.MODEL_PACKAGE));
        assertNotNull(ScorecardPMMLUtils.getExtensionValue(header.getExtensions(), PMMLExtensionNames.MODEL_IMPORTS));
    }

    @Test
    public void testDataDictionary() throws Exception {
        DataDictionary dataDictionary = pmmlDocument.getDataDictionary();
        assertNotNull(dataDictionary);
        assertEquals(5, dataDictionary.getNumberOfFields().intValue());
        assertEquals("age", dataDictionary.getDataFields().get(0).getName());
        assertEquals("occupation",dataDictionary.getDataFields().get(1).getName());
        assertEquals("residenceState", dataDictionary.getDataFields().get(2).getName());
        assertEquals("validLicense", dataDictionary.getDataFields().get(3).getName());
    }

    @Test
    public void testMiningSchema() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof MiningSchema){
                        MiningSchema miningSchema = ((MiningSchema)obj);
                        assertEquals(5, miningSchema.getMiningFields().size());
                        assertEquals("age", miningSchema.getMiningFields().get(0).getName());
                        assertEquals("occupation",miningSchema.getMiningFields().get(1).getName());
                        assertEquals("residenceState", miningSchema.getMiningFields().get(2).getName());
                        assertEquals("validLicense", miningSchema.getMiningFields().get(3).getName());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testCharacteristicsAndAttributes() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        assertEquals("$B$8", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));

                        assertEquals("OccupationScore",characteristics.getCharacteristics().get(1).getName());
                        assertEquals("$B$16", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(1).getExtensions(), "cellRef"));

                        assertEquals("ResidenceStateScore",characteristics.getCharacteristics().get(2).getName());
                        assertEquals("$B$22", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(2).getExtensions(), "cellRef"));

                        assertEquals("ValidLicenseScore",characteristics.getCharacteristics().get(3).getName());
                        assertEquals("$B$28", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(3).getExtensions(), "cellRef"));
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testAgeScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());
                        assertEquals("AgeScore", characteristics.getCharacteristics().get(0).getName());
                        assertEquals("$B$8", ScorecardPMMLUtils.getExtensionValue(characteristics.getCharacteristics().get(0).getExtensions(), "cellRef"));

                        assertNotNull(characteristics.getCharacteristics().get(0).getAttributes());
                        assertEquals(4, characteristics.getCharacteristics().get(0).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(0).getAttributes().get(0);
                        assertEquals("$C$10", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(1);
                        assertEquals("$C$11", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getCompoundPredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(2);
                        assertEquals("$C$12", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getCompoundPredicate());

                        attribute = characteristics.getCharacteristics().get(0).getAttributes().get(3);
                        assertEquals("$C$13", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testOccupationScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());

                        assertNotNull(characteristics.getCharacteristics().get(1).getAttributes());
                        assertEquals(3, characteristics.getCharacteristics().get(1).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(1).getAttributes().get(0);
                        assertEquals("$C$18", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        assertEquals("skydiving is a risky occupation", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "description"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(1);
                        assertEquals("$C$19", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimpleSetPredicate());

                        attribute = characteristics.getCharacteristics().get(1).getAttributes().get(2);
                        assertEquals("$C$20", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testResidenceStateScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());

                        assertNotNull(characteristics.getCharacteristics().get(2).getAttributes());
                        assertEquals(3, characteristics.getCharacteristics().get(2).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(2).getAttributes().get(0);
                        assertEquals("$C$24", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(1);
                        assertEquals("$C$25", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(2).getAttributes().get(2);
                        assertEquals("$C$26", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }

    @Test
    public void testValidLicenseScoreCharacteristic() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                for (Object obj :((Scorecard)serializable) .getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        assertEquals(4, characteristics.getCharacteristics().size());

                        assertNotNull(characteristics.getCharacteristics().get(3).getAttributes());
                        assertEquals(2, characteristics.getCharacteristics().get(3).getAttributes().size());

                        Attribute attribute = characteristics.getCharacteristics().get(3).getAttributes().get(0);
                        assertEquals("$C$30", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());

                        attribute = characteristics.getCharacteristics().get(3).getAttributes().get(1);
                        assertEquals("$C$31", ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"));
                        assertNotNull(attribute.getSimplePredicate());
                        return;
                    }
                }
            }
        }
        fail();
    }
    @Test
    public void testScorecardWithExtensions() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                assertEquals("Sample Score",scorecard.getModelName());
//                assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), ScorecardPMMLExtensionNames.SCORECARD_OBJECT_CLASS));
//                assertNotNull(ScorecardPMMLUtils.getExtension(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), ScorecardPMMLExtensionNames.SCORECARD_BOUND_VAR_NAME));
                return;
            }
        }
        fail();
    }

    @Test
    public void testOutput() throws Exception {
        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if ( obj instanceof Output) {
                        Output output = (Output)obj;
                        assertEquals(1, output.getOutputFields().size());
                        assertNotNull(output.getOutputFields().get(0));
                        assertEquals("calculatedScore", output.getOutputFields().get(0).getName());
                        assertEquals("Final Score", output.getOutputFields().get(0).getDisplayName());
                        assertEquals("double", output.getOutputFields().get(0).getDataType().value());
                        assertEquals("predictedValue", output.getOutputFields().get(0).getFeature().value());
                        return;
                    }
                }
            }
        }
        fail();
    }
}
