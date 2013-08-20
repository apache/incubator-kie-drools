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
import org.drools.scorecards.ScoringStrategy;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

import java.util.List;

public class DeclaredTypesDRLEmitter extends AbstractDRLEmitter{

    protected void addDeclaredTypeContents(PMML pmmlDocument, StringBuilder stringBuilder, Scorecard scorecard) {
        Characteristics characteristics = getCharacteristicsFromScorecard(scorecard);
        for (org.dmg.pmml.pmml_4_1.descr.Characteristic c : characteristics.getCharacteristics()) {
            String field = ScorecardPMMLUtils.extractFieldNameFromCharacteristic(c);
            String dataType = ScorecardPMMLUtils.getDataType(pmmlDocument, field);
            //String dataType = ScorecardPMMLUtils.getExtensionValue(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
            if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType)) {
                dataType = "String";
            } else if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
                dataType = "int";
            } else if (XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
                dataType = "boolean";
            }
            stringBuilder.append("\t").append(field).append(" : ").append(dataType).append("\n");
        }
    }

    @Override
    protected void internalEmitDRL(PMML pmml, List<Rule> ruleList, Package aPackage) {
        //ignore
    }

    @Override
    protected void addLHSConditions(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Condition condition = new Condition();
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        stringBuilder.append(var).append(" : ").append(objectClass);

        createFieldRestriction(pmmlDocument, c, scoreAttribute, stringBuilder);

        condition.setSnippet(stringBuilder.toString());
        rule.addCondition(condition);
    }

    @Override
    protected void addAdditionalReasonCodeConsequence(Rule rule, Scorecard scorecard) {
        Consequence consequence = new Consequence();
        if (scorecard.getReasonCodeAlgorithm() != null) {
            if ("pointsAbove".equalsIgnoreCase(scorecard.getReasonCodeAlgorithm())) {
                consequence.setSnippet("$sc.sortAndSetReasonCodes(DroolsScorecard.REASON_CODE_ALGORITHM_POINTSABOVE, $partialScoresList);");
            } else if ("pointsBelow".equalsIgnoreCase(scorecard.getReasonCodeAlgorithm())) {
                consequence.setSnippet("$sc.sortAndSetReasonCodes(DroolsScorecard.REASON_CODE_ALGORITHM_POINTSBELOW, $partialScoresList);");
            }
            rule.addConsequence(consequence);
        }
    }

    @Override
    protected void addAdditionalReasonCodeCondition(Rule rule, Scorecard scorecard) {
        createEmptyScorecardCondition(rule, scorecard);
    }

    @Override
    protected void addAdditionalSummationCondition(Rule calcTotalRule, Scorecard scorecard) {
        createEmptyScorecardCondition(calcTotalRule, scorecard);
    }

    @Override
    protected void addAdditionalSummationConsequence(Rule calcTotalRule, Scorecard scorecard) {

        Consequence consequence = new Consequence();
        ScoringStrategy scoringStrategy = getScoringStrategy(scorecard);
        switch (scoringStrategy) {
            case AGGREGATE_SCORE:
            case MINIMUM_SCORE:
            case MAXIMUM_SCORE:
            case AVERAGE_SCORE:
            case WEIGHTED_AVERAGE_SCORE:
            case WEIGHTED_MAXIMUM_SCORE:
            case WEIGHTED_MINIMUM_SCORE:
            case WEIGHTED_AGGREGATE_SCORE: {
                consequence.setSnippet("double calculatedScore = $calculatedScore;");
                break;
            }
        }

        calcTotalRule.addConsequence(consequence);
        consequence = new Consequence();
        if (scorecard.getInitialScore() > 0) {
            consequence.setSnippet("$sc.setCalculatedScore(calculatedScore+$initialScore);");
        } else {
            consequence.setSnippet("$sc.setCalculatedScore(calculatedScore);");
        }
        calcTotalRule.addConsequence(consequence);

    }

    protected void createEmptyScorecardCondition(Rule rule, Scorecard scorecard) {
        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        stringBuilder.append(var).append(" : ").append(objectClass).append("()");

        Condition condition = new Condition();
        condition.setSnippet(stringBuilder.toString());
        rule.addCondition(condition);
    }

    protected Condition createInitialRuleCondition(Scorecard scorecard, String objectClass) {
        String var = "$sc";
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(var).append(" : ").append(objectClass).append("()");
        Condition condition = new Condition();
        condition.setSnippet(stringBuilder.toString());
        return condition;
    }
}
