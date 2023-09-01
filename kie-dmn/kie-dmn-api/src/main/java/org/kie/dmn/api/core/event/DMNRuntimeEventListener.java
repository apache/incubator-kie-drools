package org.kie.dmn.api.core.event;

public interface DMNRuntimeEventListener {

    default void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {}

    default void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {}

    default void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {}

    default void afterEvaluateBKM(AfterEvaluateBKMEvent event) {}

    default void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {}

    default void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {}

    default void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {}

    default void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {}

    default void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {}

    default void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {}

    default void beforeInvokeBKM(BeforeInvokeBKMEvent event) {}

    default void afterInvokeBKM(AfterInvokeBKMEvent event) {}

    default void beforeEvaluateAll(BeforeEvaluateAllEvent event) {}

    default void afterEvaluateAll(AfterEvaluateAllEvent event) {}
}
