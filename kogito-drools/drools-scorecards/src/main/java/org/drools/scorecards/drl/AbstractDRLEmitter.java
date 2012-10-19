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
import org.drools.core.util.StringUtils;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.PMMLOperators;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.*;
import org.drools.template.model.Package;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDRLEmitter {

    protected String formRuleName(PMML pmmlDocument, String modelName, Characteristic c, Attribute scoreAttribute) {
        StringBuilder sb = new StringBuilder();
        sb.append(modelName).append("_").append(c.getName()).append("_");
        String dataType = ScorecardPMMLUtils.getDataType(pmmlDocument, ScorecardPMMLUtils.extractFieldNameFromCharacteristic(c));
        if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                sb.append(scoreAttribute.getSimplePredicate().getOperator()).append("_").append(scoreAttribute.getSimplePredicate().getValue());
            } else if (scoreAttribute.getCompoundPredicate() != null) {
                sb.append("between");
                for (Object obj : scoreAttribute.getCompoundPredicate().getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates()) {
                    if (obj instanceof SimplePredicate) {
                        sb.append("_").append(((SimplePredicate) obj).getValue());
                    }
                }
            }
        } else if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType) || XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                sb.append(scoreAttribute.getSimplePredicate().getOperator()).append("_").append(scoreAttribute.getSimplePredicate().getValue());
            } else if (scoreAttribute.getSimpleSetPredicate() != null) {
                SimpleSetPredicate predicate = scoreAttribute.getSimpleSetPredicate();
                Array array = predicate.getArray();
                String text = array.getContent().replace(" ", "_");
                sb.append(predicate.getBooleanOperator()).append("_").append(text);
            }
        }
        return sb.toString();
    }

    protected Characteristics getCharacteristicsFromScorecard(Scorecard scorecard) {
        for (Object obj : scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()) {
            if (obj instanceof Characteristics) {
                return (Characteristics) obj;
            }
        }
        return null;
    }

    protected void addGlobals(PMML pmml, org.drools.template.model.Package aPackage) {

    }

    public String emitDRL(PMML pmml) {
        List<Rule> ruleList = createRuleList(pmml);
        String pkgName = ScorecardPMMLUtils.getExtensionValue(pmml.getHeader().getExtensions(), PMMLExtensionNames.SCORECARD_PACKAGE);
        org.drools.template.model.Package aPackage = new org.drools.template.model.Package(pkgName);

        DRLOutput drlOutput = new DRLOutput();
        for (Rule rule : ruleList) {
            aPackage.addRule(rule);
        }

        addDeclaredTypes(pmml, aPackage);

        addImports(pmml, aPackage);

        addGlobals(pmml, aPackage);

        internalEmitDRL(pmml, ruleList, aPackage);

        aPackage.renderDRL(drlOutput);
        String drl = drlOutput.getDRL();
        return drl;
    }

    private void addImports(PMML pmml, Package aPackage) {
        String importsFromDelimitedString = ScorecardPMMLUtils.getExtensionValue(pmml.getHeader().getExtensions(), PMMLExtensionNames.SCORECARD_IMPORTS);
        for (String importStatement : importsFromDelimitedString.split(",")) {
            Import imp = new Import();
            imp.setClassName(importStatement);
            aPackage.addImport(imp);
        }
        Import defaultScorecardImport = new Import();
        defaultScorecardImport.setClassName("org.drools.scorecards.DroolsScorecard");
        aPackage.addImport(defaultScorecardImport);
        defaultScorecardImport = new Import();
        defaultScorecardImport.setClassName("org.drools.scorecards.PartialScore");
        aPackage.addImport(defaultScorecardImport);
        defaultScorecardImport = new Import();
        defaultScorecardImport.setClassName("org.drools.scorecards.InitialScore");
        aPackage.addImport(defaultScorecardImport);
        defaultScorecardImport = new Import();
        defaultScorecardImport.setClassName("org.drools.scorecards.BaselineScore");
        aPackage.addImport(defaultScorecardImport);
    }

    protected void addDeclaredTypes(PMML pmml, Package aPackage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\ndeclare DroolsScorecard\nend\n\n");

        for (Object obj : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (obj instanceof Scorecard) {
                Scorecard scorecard = (Scorecard) obj;
                stringBuilder.append("declare ").append(scorecard.getModelName().replaceAll(" ","")).append(" extends DroolsScorecard\n");

                addDeclaredTypeContents(pmml, stringBuilder, scorecard);

                stringBuilder.append("end\n");
            }
        }
        aPackage.addDeclaredType(stringBuilder.toString());
    }

    protected List<Rule> createRuleList(PMML pmmlDocument) {
        List<Rule> ruleList = new ArrayList<Rule>();
        for (Object obj : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (obj instanceof Scorecard) {
                Scorecard scorecard = (Scorecard) obj;
                Characteristics characteristics = getCharacteristicsFromScorecard(scorecard);
                createInitialRule(ruleList, scorecard);
                for (org.dmg.pmml.pmml_4_1.descr.Characteristic c : characteristics.getCharacteristics()) {
                    int attributePosition = 0;
                    for (org.dmg.pmml.pmml_4_1.descr.Attribute scoreAttribute : c.getAttributes()) {
                        String name = formRuleName(pmmlDocument, scorecard.getModelName().replaceAll(" ",""), c, scoreAttribute);
                        Rule rule = new Rule(name, 99, 1);
                        String desc = ScorecardPMMLUtils.getExtensionValue(scoreAttribute.getExtensions(), "description");
                        if (desc != null) {
                            rule.setDescription(desc);
                        }
                        attributePosition++;
                        populateLHS(rule, pmmlDocument, scorecard, c, scoreAttribute);
                        populateRHS(rule, pmmlDocument, scorecard, c, scoreAttribute, attributePosition);
                        ruleList.add(rule);
                    }
                }
                createSummationRules(ruleList, scorecard);
            }
        }
        return ruleList;
    }

    protected void createInitialRule(List<Rule> ruleList, Scorecard scorecard) {
        if (scorecard.getInitialScore() > 0 || scorecard.isUseReasonCodes()){
            String objectClass = scorecard.getModelName().replaceAll(" ", "");
            String ruleName = objectClass+"_init";
            Rule rule = new Rule(ruleName, 999, 1);
            rule.setDescription("set the initial score");

            Condition condition = createInitialRuleCondition(scorecard, objectClass);
            rule.addCondition(condition);
            if (scorecard.getInitialScore() > 0 ) {
                Consequence consequence = new Consequence();
                //consequence.setSnippet("$sc.setInitialScore(" + scorecard.getInitialScore() + ");");
                consequence.setSnippet("insertLogical(new InitialScore(\"" + objectClass+"\","+scorecard.getInitialScore() +"));");
                rule.addConsequence(consequence);
            }
            if (scorecard.isUseReasonCodes() ) {
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if (obj instanceof Characteristics){
                        Characteristics characteristics = (Characteristics)obj;
                        for (Characteristic characteristic : characteristics.getCharacteristics()){
                            String field = ScorecardPMMLUtils.extractFieldNameFromCharacteristic(characteristic);
                            Consequence consequence = new Consequence();
                            if (characteristic.getBaselineScore() == null ||  characteristic.getBaselineScore() == 0 ) {
                                consequence.setSnippet("insertLogical(new BaselineScore(\"" + objectClass+"\",\""+field + "\","+scorecard.getBaselineScore()+"));");
                                //consequence.setSnippet("$sc.setBaselineScore(\"" + field + "\","+scorecard.getBaselineScore()+");");
                            } else {
                                consequence.setSnippet("insertLogical(new BaselineScore(\"" + objectClass+"\",\""+field + "\","+characteristic.getBaselineScore()+"));");
                                //consequence.setSnippet("$sc.setBaselineScore(\"" + field + "\","+characteristic.getBaselineScore()+");");
                            }
                            rule.addConsequence(consequence);
                        }
                    }
                }
                if (scorecard.getReasonCodeAlgorithm() != null) {
                    Consequence consequence = new Consequence();
                    if ("pointsAbove".equalsIgnoreCase(scorecard.getReasonCodeAlgorithm())) {
                        //TODO: ReasonCode Algorithm
                        consequence.setSnippet("//$sc.setReasonCodeAlgorithm(DroolsScorecard.REASON_CODE_ALGORITHM_POINTSABOVE);");
                    } else if ("pointsBelow".equalsIgnoreCase(scorecard.getReasonCodeAlgorithm())) {
                        consequence.setSnippet("//$sc.setReasonCodeAlgorithm(DroolsScorecard.REASON_CODE_ALGORITHM_POINTSBELOW);");
                    }
                    rule.addConsequence(consequence);
                }
            }
            ruleList.add(rule);
        }
    }

    protected void populateLHS(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        addLHSConditions(rule, pmmlDocument, scorecard, c, scoreAttribute);
    }

    protected void createFieldRestriction(PMML pmmlDocument, Characteristic c, Attribute scoreAttribute, StringBuilder stringBuilder) {
        stringBuilder.append("(");
        //String dataType = ScorecardPMMLUtils.getExtensionValue(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
        String dataType = ScorecardPMMLUtils.getDataType(pmmlDocument, ScorecardPMMLUtils.extractFieldNameFromCharacteristic(c));
        if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" == ");
                    stringBuilder.append("\"").append(predicate.getValue()).append("\"");
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" != ");
                    stringBuilder.append("\"").append(predicate.getValue()).append("\"");
                }
            } else if (scoreAttribute.getSimpleSetPredicate() != null) {
                SimpleSetPredicate simpleSetPredicate = scoreAttribute.getSimpleSetPredicate();
                String content = simpleSetPredicate.getArray().getContent();
                content = content.replaceAll(" ", "\",\"");

                stringBuilder.append(simpleSetPredicate.getField()).append(" in ( \"").append(content).append("\" )");
            }
        } else if (XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" == ");
                    stringBuilder.append(predicate.getValue().toLowerCase());
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" != ");
                    stringBuilder.append(predicate.getValue().toLowerCase());
                }
            }
        } else if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                stringBuilder.append(predicate.getField());
                if (PMMLOperators.LESS_THAN.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" < ");
                } else if (PMMLOperators.GREATER_THAN.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" > ");
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" <> ");
                } else if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" == ");
                } else if (PMMLOperators.GREATER_OR_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" >= ");
                } else if (PMMLOperators.LESS_OR_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" <= ");
                }
                stringBuilder.append(predicate.getValue());
            } else if (scoreAttribute.getCompoundPredicate() != null) {
                CompoundPredicate predicate = scoreAttribute.getCompoundPredicate();
                String field = null;
                for (Object obj : predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates()) {
                    if (obj instanceof SimplePredicate) {
                        SimplePredicate simplePredicate = (SimplePredicate) obj;
                        String operator = simplePredicate.getOperator();
                        if (field == null) {
                            stringBuilder.append(simplePredicate.getField());
                            field = simplePredicate.getField();
                        } else {
                            stringBuilder.append(" && ");
                        }
                        if (PMMLOperators.LESS_THAN.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" < ");
                        } else if (PMMLOperators.GREATER_THAN.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" > ");
                        } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" <> ");
                        } else if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" == ");
                        } else if (PMMLOperators.GREATER_OR_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" >= ");
                        } else if (PMMLOperators.LESS_OR_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" <= ");
                        }
                        stringBuilder.append(simplePredicate.getValue());
                    }
                }
            }
        }
        stringBuilder.append(")");
    }

    protected void populateRHS(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute, int position) {
        Consequence consequence = new Consequence();
        StringBuilder stringBuilder = new StringBuilder();
        String objectClass = scorecard.getModelName().replaceAll(" ", "");

        String setter = "insertLogical(new PartialScore(\"";
        String field = ScorecardPMMLUtils.extractFieldNameFromCharacteristic(c);

        stringBuilder.append(setter).append(objectClass).append("\",\"").append(field).append("\",").append(scoreAttribute.getPartialScore());
        if (scorecard.isUseReasonCodes()){
            String reasonCode = scoreAttribute.getReasonCode();
            if (reasonCode == null || StringUtils.isEmpty(reasonCode)) {
                reasonCode = c.getReasonCode();
            }
            stringBuilder.append(",\"").append(reasonCode).append("\", ").append(position);
        }
        stringBuilder.append("));");
        consequence.setSnippet(stringBuilder.toString());
        rule.addConsequence(consequence);
    }

    protected void createSummationRules(List<Rule> ruleList, Scorecard scorecard) {
        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        Rule calcTotalRule = new Rule(objectClass+"_calculateTotalScore",1,1);
        StringBuilder stringBuilder = new StringBuilder();
        Condition condition = new Condition();
        stringBuilder.append("$calculatedScore : Double() from accumulate (PartialScore(scorecardName ==\"").append(objectClass).append("\", $partialScore:score), sum($partialScore))");
        condition.setSnippet(stringBuilder.toString());
        calcTotalRule.addCondition(condition);
        if (scorecard.getInitialScore() > 0) {
            condition = new Condition();
            stringBuilder = new StringBuilder();
            stringBuilder.append("InitialScore(scorecardName == \"").append(objectClass).append("\", $initialScore:score)");
            condition.setSnippet(stringBuilder.toString());
            calcTotalRule.addCondition(condition);
        }
        ruleList.add(calcTotalRule);

        if (scorecard.isUseReasonCodes()) {
            String ruleName = objectClass+"_collectReasonCodes";
            Rule rule = new Rule(ruleName, 1, 1);
            rule.setDescription("collect and sort the reason codes as per the specified algorithm");
            condition = new Condition();
            stringBuilder = new StringBuilder();
            stringBuilder.append("$reasons : List() from accumulate ( PartialScore(scorecardName == \"").append(objectClass).append("\", $reasonCode : reasoncode ); collectList($reasonCode) )");
            condition.setSnippet(stringBuilder.toString());
            rule.addCondition(condition);
            ruleList.add(rule);

            addAdditionalReasonCodeCondition(rule, scorecard);
            addAdditionalReasonCodeConsequence(rule, scorecard);
        }

        addAdditionalSummationCondition(calcTotalRule, scorecard);
        addAdditionalSummationConsequence(calcTotalRule, scorecard);
    }

    protected abstract void addDeclaredTypeContents(PMML pmmlDocument, StringBuilder stringBuilder, Scorecard scorecard);
    protected abstract void internalEmitDRL(PMML pmml, List<Rule> ruleList, Package aPackage);
    protected abstract void addLHSConditions(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute);
    protected abstract void addAdditionalReasonCodeConsequence(Rule rule, Scorecard scorecard);
    protected abstract void addAdditionalReasonCodeCondition(Rule rule, Scorecard scorecard);
    protected abstract void addAdditionalSummationConsequence(Rule rule, Scorecard scorecard);
    protected abstract void addAdditionalSummationCondition(Rule rule, Scorecard scorecard);
    protected abstract Condition createInitialRuleCondition(Scorecard scorecard, String objectClass);
}
