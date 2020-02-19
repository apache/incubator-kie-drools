/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.kie.dmn.api.core.DMNMessageType;

public final class AnalyticsMsg {

    public static final AnalyticsMsg INSTANCE = new AnalyticsMsg(new Locale("en"));

    private final ResourceBundle i18n;

    private AnalyticsMsg(Locale locale) {
        i18n = ResourceBundle.getBundle("i18n/AnalyticsMsg", locale);
    }

    public static AnalyticsMsg create(String locale) {
        try {
            return new AnalyticsMsg(new Locale(locale));
        } catch (Exception e) {
            return INSTANCE;
        }
    }

    public Msg.Message0 FAILED_VALIDATOR() {
        return new Msg.Message0(DMNMessageType.FAILED_VALIDATOR, i18n.getString("TheValidatorWasUnableToCompileTheEmbeddedDMNValidationRulesValidationOfTheDMNModelCannotBePerformed"));
    }

    public Msg.Message1 VALIDATION_RUNTIME_PROBLEM() {
        return new Msg.Message1(DMNMessageType.FAILED_VALIDATION, i18n.getString("ValidationOfTheDMNModelCannotBePerformedBecauseOfSomeRuntimeException0"));
    }

    public Msg.Message0 VALIDATION_STOPPED() {
        return new Msg.Message0(DMNMessageType.FAILED_VALIDATION, i18n.getString("OneOfTheSuppliedDMNModelsHasFailedValidationCannotProceedToValidationOfTheRemainingDMNModels"));
    }

    public Msg.Message0 FAILED_NO_XML_SOURCE() {
        return new Msg.Message0(DMNMessageType.FAILED_VALIDATOR, i18n.getString("SchemaValidationNotSupportedForInMemoryObjectPleaseUseTheValidateMethodWithTheFileOrReaderSignature"));
    }

    public Msg.Message1 FAILED_XML_VALIDATION() {
        return new Msg.Message1(DMNMessageType.FAILED_XML_VALIDATION, i18n.getString("FailedXMLValidationOfDMNFile0"));
    }

    public Msg.Message1 DTANALYSIS_EMPTY() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_ANALYSIS, i18n.getString("DecisionTableAnalysisOfTable0FinishedWithNoMessagesToBeReported"));
    }

    public Msg.Message2 DTANALYSIS_ERROR_ANALYSIS_SKIPPED() {
        return new Msg.Message2(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, i18n.getString("SkippedDecisionTableAnalysisOfTable0Because1"));
    }

    public Msg.Message3 DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED() {
        return new Msg.Message3(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, i18n.getString("SkippedDecisionTableAnalysisOfTable0HitPolicyPriorityMaskRulesForRules01AsTheyDefineMultipleInputentries"));
    }

    public Msg.Message4 DTANALYSIS_ERROR_RULE_OUTSIDE_DOMAIN() {
        return new Msg.Message4(DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR, i18n.getString("Rule0Defines1WhichIsOutsideTheDomainMinMax2OfColumn3"));
    }

    public Msg.Message1 DTANALYSIS_GAP() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_GAP, i18n.getString("GapDetected0"));
    }

    public Msg.Message1 DTANALYSIS_GAP_SKIPPED_BECAUSE_FREE_STRING() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_GAP, i18n.getString("ColumnsoRelateToFEELStringValuesWhichCanBeEnumeratedForTheInputsGapAnalysisSkipped"));
    }

    public Msg.Message1 DTANALYSIS_OVERLAP() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_OVERLAP, i18n.getString("OverlapObserved0"));
    }

    public Msg.Message1 DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_UNIQUE, i18n.getString("OverlapDetected0UNIQUEHitPolicyDecisionTablesCanOnlyHaveOneMatchingRule"));
    }

    public Msg.Message1 DTANALYSIS_OVERLAP_HITPOLICY_ANY() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_OVERLAP_HITPOLICY_ANY, i18n.getString("OverlapDetected0ANYHitPolicyDecisionTablesAllowsMultipleRulesToMatchButTheyMustAllHaveTheSameOutput"));
    }

    public Msg.Message1 DTANALYSIS_HITPOLICY_FIRST() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_HITPOLICY_FIRST, i18n.getString("DecisionTable0UsesFIRSTHitPolicyFIRSTHitPolicyDecisionTablesAreNotConsideredGoodPracticeBecauseTheyDoNotOfferAClearOverviewOfTheDecisionLogicQuotedFromDecisionModelAndNotationSpecification"));
    }

    public Msg.Message2 DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE() {
        return new Msg.Message2(DMNMessageType.DECISION_TABLE_MASKED_RULE, i18n.getString("Rul0IsMaskedByRule1"));
    }

    public Msg.Message2 DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE() {
        return new Msg.Message2(DMNMessageType.DECISION_TABLE_MISLEADING_RULE, i18n.getString("Rule0IsAMisleadingRuleItCouldBeMisleadingOverOtherRulesSuchAsRule0"));
    }

    public Msg.Message4 DTANALYSIS_SUBSUMPTION_RULE() {
        return new Msg.Message4(DMNMessageType.DECISION_TABLE_SUBSUMPTION_RULE, i18n.getString("SubsumptionRule0ContainsRule0RulesCanBeContractedByKeepingRule1AndErasingRule2"));
    }

    public Msg.Message2 DTANALYSIS_CONTRACTION_RULE() {
        return new Msg.Message2(DMNMessageType.DECISION_TABLE_CONTRACTION_RULE, i18n.getString("ContractionRules0CanBeCombinedForContractionByJoiningOnInput1"));
    }

    public Msg.Message0 DTANALYSIS_1STNFVIOLATION_FIRST() {
        return new Msg.Message0(DMNMessageType.DECISION_TABLE_1STNFVIOLATION, i18n.getString("FirstNormalFormViolationHitPolicyFirstIsAViolationOfFirstNormalFormConsiderChangingForExampleToPriority"));
    }

    public Msg.Message0 DTANALYSIS_1STNFVIOLATION_RULE_ORDER() {
        return new Msg.Message0(DMNMessageType.DECISION_TABLE_1STNFVIOLATION, i18n.getString("FirstNormalFormViolationHitPolicyRuleOrderIsAViolationOfFirstNormalFormConsiderChangingForExampleToOutputOrderOrCollect"));
    }

    public Msg.Message1 DTANALYSIS_1STNFVIOLATION_DUPLICATE_RULES() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_1STNFVIOLATION, i18n.getString("FirstNormalFormViolationRules0AreDuplicates"));
    }

    public Msg.Message2 DTANALYSIS_2NDNFVIOLATION() {
        return new Msg.Message2(DMNMessageType.DECISION_TABLE_2NDNFVIOLATION, i18n.getString("SecondNormalFormViolationInput0IsIrrelevantForRules0ConsiderCombiningTheseRulesOverTheIrrelevantInput"));
    }

    public Msg.Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_UNIQUE() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, i18n.getString("TheHitPolicyForDecisionTable0ShouldBeUNIQUE"));
    }

    public Msg.Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_ANY() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, i18n.getString("OverlappingRulesHaveTheSameOutputValueSoTheHitPolicyForDecisionTable0ShouldBeANY"));
    }

    public Msg.Message1 DTANALYSIS_HITPOLICY_RECOMMENDER_PRIORITY() {
        return new Msg.Message1(DMNMessageType.DECISION_TABLE_HITPOLICY_RECOMMENDER, i18n.getString("OverlappingRulesHaveDifferentOutputValueSoTheHitPolicyForDecisionTable0ShouldBePRIORITY"));
    }
}
