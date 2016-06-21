/*
* Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scorecards.parser.xls;

import org.dmg.pmml.pmml_4_2.descr.Attribute;
import org.dmg.pmml.pmml_4_2.descr.Characteristic;
import org.dmg.pmml.pmml_4_2.descr.Characteristics;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.core.util.StringUtils;
import org.drools.pmml.pmml_4_2.extensions.AggregationStrategy;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.drools.scorecards.ScorecardError;
import org.drools.scorecards.StringUtil;
import org.drools.scorecards.pmml.ScorecardPMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;

import java.util.List;

class ExcelScorecardValidator {

    private Scorecard scorecard;
    private List<ScorecardError> parseErrors;

    private ExcelScorecardValidator(Scorecard scorecard, List<ScorecardError> parseErrors) {
        //to ensure this used as a pure Util class only.
        this.scorecard = scorecard;
        this.parseErrors = parseErrors;
    }

    public static void runAdditionalValidations(Scorecard scorecard, List<ScorecardError> parseErrors) {
        ExcelScorecardValidator validator = new ExcelScorecardValidator(scorecard, parseErrors);
        validator.checkForInvalidDataTypes();
        validator.checkForMissingAttributes();
        if (scorecard.getUseReasonCodes()){
            validator.validateReasonCodes();
            validator.validateBaselineScores();
        }
        if ( getScoringStrategy(scorecard).toString().startsWith("WEIGHTED")) {
            validator.validateWeights();
        }

    }

    private void validateWeights() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    for (Attribute attribute : characteristic.getAttributes()){
                        String newCellRef = createDataTypeCellRef(ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"),2);
                        String weight = ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), ScorecardPMMLExtensionNames.CHARACTERTISTIC_WEIGHT);
                        if ( StringUtils.isEmpty(weight) || !isDouble(weight)){
                            parseErrors.add(new ScorecardError(newCellRef, "Attribute is missing weight or specified weight is not a double."));
                        }
                    }
                }
            }
        }
    }

    protected boolean isDouble(String doubleStr){
        try {
            Double.parseDouble(doubleStr);
            return true;
        } catch (Exception e){
            return  false;
        }
    }

    private void validateReasonCodes() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    String charReasonCode = characteristic.getReasonCode();
                    if (charReasonCode == null || StringUtils.isEmpty(charReasonCode)){
                        for (Attribute attribute : characteristic.getAttributes()){
                            String newCellRef = createDataTypeCellRef(ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "cellRef"),3);
                            String attrReasonCode = attribute.getReasonCode();
                            if ( attrReasonCode == null || StringUtils.isEmpty(attrReasonCode)){
                                parseErrors.add(new ScorecardError(newCellRef, "Attribute is missing Reason Code"));
                            }
                        }
                    }
                }
            }
        }
    }

    private void validateBaselineScores() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            Double scorecardBaseline = scorecard.getBaselineScore();
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    Double charBaseline = characteristic.getBaselineScore();
                    if  ( (charBaseline == null || charBaseline.doubleValue() == 0)
                            && ((scorecardBaseline == null || scorecardBaseline.doubleValue() == 0)) ){
                        String newCellRef = createDataTypeCellRef(ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), "cellRef"),2);
                        parseErrors.add(new ScorecardError(newCellRef, "Characteristic is missing Baseline Score"));
                    }
                }
            }
        }
    }

    private void checkForInvalidDataTypes() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    String dataType = ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), ScorecardPMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
                    String newCellRef = createDataTypeCellRef(ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), "cellRef"),1);
                    if ( dataType == null || StringUtils.isEmpty(dataType)) {
                        parseErrors.add(new ScorecardError(newCellRef, "Missing Data Type!"));
                    }  else  if ( !XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType) && !XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)  && !XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)){
                        parseErrors.add(new ScorecardError(newCellRef, "Invalid Data Type!"));
                    }

                    if (XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)){
                        for (Attribute attribute : characteristic.getAttributes()){
                            String value = ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "predicateResolver");
                            if (!"TRUE".equalsIgnoreCase(value) && !"FALSE".equalsIgnoreCase(value)){
                                parseErrors.add(new ScorecardError(newCellRef, "Characteristic '"+characteristic.getName()+"' is Boolean and can support TRUE|FALSE only"));
                                break;
                            }
                        }
                    } else if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)){
                        for (Attribute attribute : characteristic.getAttributes()){
                            String value = ScorecardPMMLUtils.getExtensionValue(attribute.getExtensions(), "predicateResolver");
                            if (!StringUtil.isNumericWithOperators(value)){
                                parseErrors.add(new ScorecardError(newCellRef, "Characteristic '"+characteristic.getName()+"' is Number and can support numerics only"));
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkForMissingAttributes() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    String newCellRef = ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), "cellRef");
                    if ( characteristic.getAttributes().size() == 0 ) {
                        parseErrors.add(new ScorecardError(newCellRef, "Missing Attribute Bins for Characteristic '"+characteristic.getName()+"'."));
                    }
                }
            }
        }
    }

    private String createDataTypeCellRef(String cellRef, int n) {
        int col = ((int)(cellRef.charAt(1)))+n;
        return "$"+((char)col)+cellRef.substring(cellRef.indexOf('$',1));
    }

    protected static AggregationStrategy getScoringStrategy( Scorecard scorecard ) {
        String scoringStrategyName = ScorecardPMMLUtils.getExtensionValue(scorecard.getExtensionsAndCharacteristicsAndMiningSchemas(), ScorecardPMMLExtensionNames.SCORECARD_SCORING_STRATEGY);
        return PMML4Helper.resolveAggregationStrategy( scoringStrategyName );
    }
}
