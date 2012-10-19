/*
 * Copyright 2012 JBoss Inc
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

package org.drools.scorecards.drl;

import org.dmg.pmml.pmml_4_1.descr.*;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

import java.util.List;

public class ExternalModelDRLEmitter  extends AbstractDRLEmitter {

    @Override
    protected void addDeclaredTypeContents(PMML pmmlDocument, StringBuilder stringBuilder, Scorecard scorecard) {
        //empty by design
    }

    @Override
    protected void internalEmitDRL(PMML pmml, List<Rule> ruleList, Package aPackage) {
        //do nothing for now.
    }

    @Override
    protected void addLHSConditions(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Extension extension = null;
        for (Object obj : scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof MiningSchema ) {
                MiningSchema miningSchema = (MiningSchema)obj;
                String fieldName = ScorecardPMMLUtils.extractFieldNameFromCharacteristic(c);
                for (MiningField miningField : miningSchema.getMiningFields() ){
                    if ( miningField.getName().equalsIgnoreCase(fieldName)) {
                        if (miningField.getExtensions().size() > 0 ) {
                            extension = miningField.getExtensions().get(0);
                        }
                    }
                }
            }
        }
        //Extension extension =  ScorecardPMMLUtils.getExtension(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS);
        if ( extension != null ) {
            Condition condition = new Condition();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(c.getName()).append(" : ").append(extension.getValue());
            createFieldRestriction(pmmlDocument, c, scoreAttribute, stringBuilder);
            condition.setSnippet(stringBuilder.toString());
            rule.addCondition(condition);
        }
    }

    @Override
    protected void addAdditionalReasonCodeConsequence(Rule rule, Scorecard scorecard) {
        if (!scorecard.isUseReasonCodes()) {
            return;
        }
        String externalClassName =  null;
        String reasonCodesField = null;
        String fieldName =  null;

        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                fieldName = outputField.getName();
                Extension e = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_REASONCODES_FIELD);
                if (e != null) {
                    reasonCodesField = e.getValue();
                }
                break;
            }
        }
        if ( reasonCodesField != null && externalClassName != null && fieldName != null) {
            Consequence consequence = new Consequence();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(fieldName).append("Var").append(".set").append(Character.toUpperCase(reasonCodesField.charAt(0))).append(reasonCodesField.substring(1));
            stringBuilder.append("($reasons);");
            consequence.setSnippet(stringBuilder.toString());
            rule.addConsequence(consequence);
        }

    }

    @Override
    protected void addAdditionalReasonCodeCondition(Rule rule, Scorecard scorecard) {
        if (!scorecard.isUseReasonCodes()) {
            return;
        }
        String externalClassName =  null;
        String reasonCodesField = null;
        String fieldName =  null;

        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                fieldName = outputField.getName();
                Extension e = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_REASONCODES_FIELD);
                if (e != null) {
                    reasonCodesField = e.getValue();
                }
                break;
            }
        }
        if ( reasonCodesField != null && externalClassName != null && fieldName != null) {
            Condition condition = new Condition();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(fieldName).append("Var : ").append(externalClassName).append("()");
            condition.setSnippet(stringBuilder.toString());
            rule.addCondition(condition);
        }

    }

    @Override
    protected void addAdditionalSummationConsequence(Rule calcTotalRule, Scorecard scorecard) {
        String externalClassName =  null;
        String fieldName =  null;
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                fieldName = outputField.getName();
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                break;
            }
        }
        if ( fieldName != null && externalClassName != null) {
            Consequence consequence = new Consequence();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(fieldName).append("Var").append(".set").append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1));
            if (scorecard.getInitialScore() > 0 ) {
                stringBuilder.append("($calculatedScore+$initialScore);");
            } else {
                stringBuilder.append("($calculatedScore);");
            }
            consequence.setSnippet(stringBuilder.toString());
            calcTotalRule.addConsequence(consequence);
        }

    }

    @Override
    protected void addAdditionalSummationCondition(Rule calcTotalRule, Scorecard scorecard) {
        String externalClassName =  null;
        String fieldName =  null;
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                fieldName = outputField.getName();
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                break;
            }
        }
        if ( fieldName != null && externalClassName != null) {
            Condition condition = new Condition();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(fieldName).append("Var : ").append(externalClassName).append("()");
            condition.setSnippet(stringBuilder.toString());
            calcTotalRule.addCondition(condition);
        }
    }

    protected Condition createInitialRuleCondition(Scorecard scorecard, String objectClass) {
        String externalClassName =  null;
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                break;
            }
        }
        if ( externalClassName != null) {
            Condition condition = new Condition();
            condition.setSnippet(externalClassName+"()");
            return condition;
        }
        return null;
    }
}
