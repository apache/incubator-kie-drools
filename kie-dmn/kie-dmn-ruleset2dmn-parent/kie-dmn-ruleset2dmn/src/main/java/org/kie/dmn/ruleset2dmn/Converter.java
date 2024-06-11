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
package org.kie.dmn.ruleset2dmn;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimplePredicate.Operator;
import org.dmg.pmml.Value;
import org.dmg.pmml.rule_set.RuleSelectionMethod;
import org.dmg.pmml.rule_set.RuleSelectionMethod.Criterion;
import org.dmg.pmml.rule_set.RuleSet;
import org.dmg.pmml.rule_set.RuleSetModel;
import org.dmg.pmml.rule_set.SimpleRule;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.RuleAnnotationClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TDMNElementReference;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TDecisionRule;
import org.kie.dmn.model.v1_2.TDecisionTable;
import org.kie.dmn.model.v1_2.TDefinitions;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TInformationRequirement;
import org.kie.dmn.model.v1_2.TInputClause;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.dmn.model.v1_2.TItemDefinition;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.dmn.model.v1_2.TOutputClause;
import org.kie.dmn.model.v1_2.TRuleAnnotation;
import org.kie.dmn.model.v1_2.TRuleAnnotationClause;
import org.kie.dmn.model.v1_2.TUnaryTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter {

    private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

    public static String parse(String dmnModelName, InputStream is) throws Exception {
        final PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
        if (pmml.getModels().size() != 1) {
            throw new UnsupportedOperationException("Only single model supported for Decision Table conversion");
        }
        Model model0 = pmml.getModels().get(0);
        if (!(model0 instanceof RuleSetModel)) {
            throw new UnsupportedOperationException("Only single RuleSetModel supported for Decision Table conversion");
        }
        RuleSetModel rsModel = (RuleSetModel) model0;
        RuleSet rs = rsModel.getRuleSet();
        if (rs.getRuleSelectionMethods().size() != 1) {
            throw new UnsupportedOperationException("Only single RuleSelectionMethods supported for Decision Table conversion");
        }
        RuleSelectionMethod rssMethod0 = rs.getRuleSelectionMethods().get(0);

        Stream<SimpleRule> s0 = rs.getRules().stream().map(SimpleRule.class::cast);
        if (rssMethod0.getCriterion() == Criterion.WEIGHTED_MAX) { // if WEIGHTED_MAX then sort by weight desc
            s0 = s0.sorted(new WeightComparator().reversed());
        }
        List<SimpleRuleRow> rsRules = s0.map(SimpleRuleRow::new).collect(Collectors.toList());
        Set<String> usedPredictors = new LinkedHashSet<>();
        for (SimpleRuleRow rr : rsRules) {
            usedPredictors.addAll(rr.map.keySet());
            LOG.debug("{}", rr);
        }
        LOG.debug("{}", usedPredictors);

        Map<String, Set<String>> predictorsLoVs = new HashMap<>();

        Definitions definitions = new TDefinitions();
        setDefaultNSContext(definitions);
        definitions.setId("dmnid_" + dmnModelName);
        definitions.setName(dmnModelName);
        String namespace = "ri2dmn_" + UUID.randomUUID();
        definitions.setNamespace(namespace);
        definitions.getNsContext().put(XMLConstants.DEFAULT_NS_PREFIX, namespace);
        definitions.setExporter("kie-dmn-ri");
        appendInputData(definitions, pmml, usedPredictors);
        final String dtName = rssMethod0.getCriterion() == Criterion.WEIGHTED_SUM ? "dt" : null;
        DecisionTable dt = appendDecisionDT(definitions, dtName, pmml, usedPredictors);
        if (rssMethod0.getCriterion() == Criterion.WEIGHTED_SUM) {
            dt.setHitPolicy(HitPolicy.COLLECT);
        }
        if (rs.getDefaultScore() != null) {
            LiteralExpression le = leFromNumberOrString(rs.getDefaultScore());
            dt.getOutput().get(0).setDefaultOutputEntry(le);
        }
        for (SimpleRuleRow r : rsRules) {
            DecisionRule dr = new TDecisionRule();
            for (String input : usedPredictors) {
                List<SimplePredicate> predicatesForInput = r.map.get(input);
                if (predicatesForInput != null && !predicatesForInput.isEmpty())  {
                     String fnLookup =input;
                    Optional<DataField> df = pmml.getDataDictionary().getDataFields().stream().filter(x-> x.getName().equals(fnLookup)).findFirst();
                    UnaryTests ut = processSimplePredicateUnaryOrBinary(predicatesForInput, df);
                    if (ut.getText().startsWith("\"") && ut.getText().endsWith("\"")) {
                        predictorsLoVs.computeIfAbsent(input, k -> new LinkedHashSet<String>()).add(ut.getText());
                    }
                    dr.getInputEntry().add(ut);
                } else {
                    UnaryTests ut = new TUnaryTests();
                    ut.setText("-");
                    dr.getInputEntry().add(ut);
                }
            }
            if (rssMethod0.getCriterion() != Criterion.WEIGHTED_SUM) {
                dr.getOutputEntry().add(leFromNumberOrString(r.r.getScore()));
            } else {
                String output = "{score: "+ feelLiteralValue(r.r.getScore(), Optional.empty()) + " , weight: " + r.r.getWeight() + " }";
                LiteralExpression le = new TLiteralExpression();
                le.setText(output);
                dr.getOutputEntry().add(le);
            }
            RuleAnnotation comment = new TRuleAnnotation();
            String commentText = "recordCount="+r.r.getRecordCount()
            + " nbCorrect=" + r.r.getNbCorrect()
            + " confidence=" + r.r.getConfidence()
            + " weight" + r.r.getWeight();
            comment.setText(commentText);
            dr.getAnnotationEntry().add(comment);
            dt.getRule().add(dr);
        }

        if (rssMethod0.getCriterion() == Criterion.WEIGHTED_SUM) {
            decisionAggregated(definitions, dtName);
            decisionMax(definitions);
            Decision decision = new TDecision();
            String decisionName = definitions.getName();
            decision.setName(decisionName);
            decision.setId("d_" + CodegenStringUtil.escapeIdentifier(decisionName));
            InformationItem variable = new TInformationItem();
            variable.setName(decisionName);
            variable.setId("dvar_" + CodegenStringUtil.escapeIdentifier(decisionName));
            variable.setTypeRef(new QName("Any"));
            decision.setVariable(variable);
            addRequiredDecisionByName(decision, "aggregated");
            addRequiredDecisionByName(decision, "max");
            LiteralExpression le = new TLiteralExpression();
            le.setText("aggregated[total=max][1].score");
            decision.setExpression(le);
            definitions.getDrgElement().add(decision);
        }

        for (DataField df : pmml.getDataDictionary().getDataFields()) {
            if (df.getDataType() == DataType.STRING && predictorsLoVs.containsKey(df.getName())) {
                for (Value value : df.getValues()) {
                    predictorsLoVs.get(df.getName()).add("\""+value.getValue().toString()+"\"");
                }
            }
        }
        for (Set<String> v : predictorsLoVs.values()) {
            v.add("\"<unknown>\"");
        }
        for (Entry<String, Set<String>> kv : predictorsLoVs.entrySet()) {
            ItemDefinition idd = new TItemDefinition();
            idd.setName(kv.getKey());
            idd.setTypeRef(new QName("string"));
            UnaryTests lov = new TUnaryTests();
            String lovText = kv.getValue().stream().collect(Collectors.joining(", "));
            lov.setText(lovText);
            idd.setAllowedValues(lov);
            definitions.getItemDefinition().add(idd);
            Optional<InputData> optInputData = definitions.getDrgElement().stream().filter(InputData.class::isInstance).map(InputData.class::cast).filter(drg-> drg.getName().equals(kv.getKey())).findFirst();
            if (optInputData.isPresent()) {
                optInputData.get().getVariable().setTypeRef(new QName(kv.getKey()));
            } else {
                throw new IllegalStateException();
            }
            Optional<InputClause> optInputClause = dt.getInput().stream().filter(ic -> ic.getInputExpression().getText().equals(kv.getKey())).findFirst();
            if (optInputClause.isPresent()) {
                UnaryTests icLov = new TUnaryTests();
                icLov.setText(lovText);
                InputClause ic = optInputClause.get();
                ic.setInputValues(icLov);
                ic.getInputExpression().setTypeRef(new QName(kv.getKey()));
            } else {
                throw new IllegalStateException();
            }
        }

        DMNMarshaller dmnMarshaller = DMNMarshallerFactory.newDefaultMarshaller();
        String xml = dmnMarshaller.marshal(definitions);
        LOG.debug("{}", predictorsLoVs);
        return xml;
    }

    private static void addRequiredDecisionByName(Decision decision, String partOfIdentifier) {
        InformationRequirement ir = new TInformationRequirement();
        DMNElementReference er = new TDMNElementReference();
        er.setHref("#d_" + CodegenStringUtil.escapeIdentifier(partOfIdentifier));
        ir.setRequiredDecision(er);
        decision.getInformationRequirement().add(ir);
    }

    private static void decisionMax(Definitions definitions) {
        Decision decision = new TDecision();
        String decisionName = "max";
        decision.setName(decisionName);
        decision.setId("d_" + CodegenStringUtil.escapeIdentifier(decisionName));
        InformationItem variable = new TInformationItem();
        variable.setName(decisionName);
        variable.setId("dvar_" + CodegenStringUtil.escapeIdentifier(decisionName));
        variable.setTypeRef(new QName("Any"));
        decision.setVariable(variable);

        addRequiredDecisionByName(decision, "aggregated");

        LiteralExpression le = new TLiteralExpression();
        le.setText("max(aggregated.total)");

        decision.setExpression(le);
        definitions.getDrgElement().add(decision);
    }

    private static void decisionAggregated(Definitions definitions, final String dtName) {
        Decision decision = new TDecision();
        String decisionName = "aggregated";
        decision.setName(decisionName);
        decision.setId("d_" + CodegenStringUtil.escapeIdentifier(decisionName));
        InformationItem variable = new TInformationItem();
        variable.setName(decisionName);
        variable.setId("dvar_" + CodegenStringUtil.escapeIdentifier(decisionName));
        variable.setTypeRef(new QName("Any"));
        decision.setVariable(variable);

        addRequiredDecisionByName(decision, dtName);

        LiteralExpression le = new TLiteralExpression();
        le.setText("for s in distinct values(dt.score) return {score: s, total: sum(dt[score=s].weight)}");

        decision.setExpression(le);
        definitions.getDrgElement().add(decision);
    }

    private static UnaryTests processSimplePredicateUnaryOrBinary(List<SimplePredicate> predicatesForInput, Optional<DataField> df) {
        UnaryTests ut = new TUnaryTests();
        if (predicatesForInput.size() == 1) {
            SimplePredicate p0 = predicatesForInput.get(0);
            String text = feelUTofOp(p0.getOperator()) + feelLiteralValue(p0.getValue(), df);
            ut.setText(text);
        } else if (predicatesForInput.size() == 2) {
            List<SimplePredicate> sortedList = predicatesForInput.stream().sorted(Comparator.comparing(o -> o.getOperator().name())).collect(Collectors.toList());
            SimplePredicate p0 = sortedList.get(0);
            SimplePredicate p1 = sortedList.get(1);

            if (canCollapseBinaryPredicate(p0, p1)) {
                ut.setText(feelLiteralValue(p0.getValue(), df));
            } else {
                ut.setText(convertBinaryPredicate(p0, p1, df));
            }
        } else {
            ut.setText("\"?\"");
        }
        return ut;
    }

    /**
     * Checks if a binary predicate can be collapsed to an unary one.
     *
     * @param predicates The contents of the binary predicate to check.
     * @return True, if the contents of the binary predicate have the same value, lower bound is greater or equal and upper bound is less or equal (case of [x..x]).
     * Otherwise returns false.
     */
    private static boolean canCollapseBinaryPredicate(final SimplePredicate first, final SimplePredicate second) {
        final Object firstValue = first.getValue();
        final Object secondValue = second.getValue();

        final boolean haveCorrectOperators = (first.getOperator() == Operator.GREATER_OR_EQUAL)
            && (second.getOperator() == Operator.LESS_OR_EQUAL);

        if (firstValue instanceof BigDecimal && secondValue instanceof BigDecimal) {
            return haveCorrectOperators && (((BigDecimal) firstValue).compareTo((BigDecimal) secondValue) == 0);
        } else {
            return haveCorrectOperators && firstValue.equals(secondValue);
        }
    }

    private static String convertBinaryPredicate(final SimplePredicate firstPart, final SimplePredicate secondPart, final Optional<DataField> df) {
        StringBuilder sb = new StringBuilder();
        sb.append(getOperatorText(firstPart.getOperator(), true));
        sb.append(feelLiteralValue(firstPart.getValue(), df));
        sb.append(" .. ");
        sb.append(feelLiteralValue(secondPart.getValue(), df));
        sb.append(getOperatorText(secondPart.getOperator(), false));
        return sb.toString();
    }

    private static String getOperatorText(final Operator operator, final boolean lowerBound) {
        if (lowerBound) {
            if (operator == Operator.GREATER_OR_EQUAL) {
                return "[";
            } else if (operator == Operator.GREATER_THAN) {
                return "(";
            } else {
                throw new UnsupportedOperationException("Unsupported operator in lowerbound: " + operator);
            }
        } else {
            if (operator == Operator.LESS_THAN) {
                return ")";
            } else if (operator == Operator.LESS_OR_EQUAL) {
                return "]";
            } else {
                throw new UnsupportedOperationException("Unsupported operator in upperbound: " + operator);
            }
        }
    }

    private static String feelUTofOp(Operator operator) {
        switch (operator) {
            case EQUAL:
                return ""; // equal op is implicit
            case GREATER_OR_EQUAL:
                return ">=";
            case GREATER_THAN:
                return ">";
            case IS_MISSING:
            case IS_NOT_MISSING:
                throw new UnsupportedOperationException("Unsupported operator for FEEL conversion");
            case LESS_OR_EQUAL:
                return "<=";
            case LESS_THAN:
                return "<";
            case NOT_EQUAL:
                return "? !="; // eventually, this could be simplified to use the not() operator.
        }
        throw new IllegalStateException();
    }

    private static LiteralExpression leFromNumberOrString(Object rs) {
        LiteralExpression le = new TLiteralExpression();
        le.setText(feelLiteralValue(rs, Optional.empty())); // we don't have DD for the score
        return le;
    }

    private static String feelLiteralValue(Object input, Optional<DataField> df) {
        if (df.isPresent()) {
            final DataType dt = df.get().getDataType();
            switch (dt) {
                case BOOLEAN:
                    String trimmed = input.toString().trim().toLowerCase();
                    switch (trimmed) {
                        case "true": return "true";
                        case "false": return "false";
                        default: throw new UnsupportedOperationException("Was expecting a FEEL:boolean but the pmml serialization was: "+input);
                    }
                case DOUBLE:
                case FLOAT:
                case INTEGER:
                    BigDecimal bdOrNull = NumberEvalHelper.getBigDecimalOrNull(input);
                    if (bdOrNull != null) {
                        return bdOrNull.toPlainString();
                    } else {
                        throw new UnsupportedOperationException("Was expecting a FEEL:number but the pmml serialization was: "+input);
                    }
                case STRING:
                    return "\"" + input + "\"";
                default:
                    throw new UnsupportedOperationException("Unhandled pmml serialization for FEEL conversion: "+input);
            }
        }
        LOG.debug("feelLiteralValue for {} and DD not available", input);
        BigDecimal bdOrNull = NumberEvalHelper.getBigDecimalOrNull(input);
        if (bdOrNull != null) {
            return bdOrNull.toPlainString();
        } else {
            return "\"" + input + "\"";
        }
    }

    private static DecisionTable appendDecisionDT(Definitions definitions, String name, PMML pmml, Set<String> usedPredictors) {
        Decision decision = new TDecision();
        String dtName = name == null ? definitions.getName() : name;
        decision.setName(dtName);
        decision.setId("d_" + CodegenStringUtil.escapeIdentifier(dtName));
        InformationItem variable = new TInformationItem();
        variable.setName(dtName);
        variable.setId("dvar_" + CodegenStringUtil.escapeIdentifier(dtName));
        variable.setTypeRef(new QName("Any"));
        decision.setVariable(variable);
        for (String ri : usedPredictors) {
            InformationRequirement ir = new TInformationRequirement();
            DMNElementReference er = new TDMNElementReference();
            er.setHref("#id_" + CodegenStringUtil.escapeIdentifier(ri));
            ir.setRequiredInput(er);
            decision.getInformationRequirement().add(ir);
        }
        DecisionTable dt = new TDecisionTable();
        dt.setOutputLabel(dtName);
        dt.setId("ddt_" + CodegenStringUtil.escapeIdentifier(dtName));
        dt.setHitPolicy(HitPolicy.FIRST);
        for (String ri : usedPredictors) {
            InputClause ic = new TInputClause();
            ic.setLabel(ri);
            LiteralExpression le = new TLiteralExpression();
            le.setText(ri);
            le.setTypeRef(new QName(feelTypeFromDD(pmml.getDataDictionary(), ri)));
            ic.setInputExpression(le);
            dt.getInput().add(ic);
        }
        OutputClause oc = new TOutputClause();
        dt.getOutput().add(oc);
        RuleAnnotationClause comment = new TRuleAnnotationClause();
        comment.setName("comments");
        dt.getAnnotation().add(comment);
        decision.setExpression(dt);
        definitions.getDrgElement().add(decision);
        return dt;
    }

    private static void appendInputData(Definitions definitions, PMML pmml, Set<String> usedPredictors) {
        DataDictionary dd = pmml.getDataDictionary();
        for(String ri : usedPredictors) {
            InputData id = new TInputData();
            id.setName(ri);
            id.setId("id_"+CodegenStringUtil.escapeIdentifier(ri));
            InformationItem variable = new TInformationItem();
            variable.setName(ri);
            variable.setId("idvar_"+CodegenStringUtil.escapeIdentifier(ri));
            variable.setTypeRef(new QName(feelTypeFromDD(dd, ri)));
            id.setVariable(variable);
            definitions.getDrgElement().add(id);
        }
    }

    private static String feelTypeFromDD(DataDictionary dd, String id) {
         String lookup =id;
        Optional<DataField> opt = dd.getDataFields().stream().filter(df -> df.getName().equals(lookup)).findFirst();
        if (opt.isEmpty()) {
            return "Any";
        }
        DataType dataType = opt.map(DataField::getDataType).get();
        switch (dataType) {
            case BOOLEAN:
                return "boolean";
            case DOUBLE:
            case FLOAT:
            case INTEGER:
                return "number";
            case STRING:
                return "string";
            default:
                return "Any";
        }
    }

    private static void setDefaultNSContext(Definitions definitions) {
        Map<String, String> nsContext = definitions.getNsContext();
        nsContext.put("feel", KieDMNModelInstrumentedBase.URI_FEEL);
        nsContext.put("dmn", KieDMNModelInstrumentedBase.URI_DMN);
        nsContext.put("dmndi", KieDMNModelInstrumentedBase.URI_DMNDI);
        nsContext.put("di", KieDMNModelInstrumentedBase.URI_DI);
        nsContext.put("dc", KieDMNModelInstrumentedBase.URI_DC);
    }
}
