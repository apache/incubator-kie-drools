package org.kie.dmn.core.api.event;

import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;

/**
 * A default implementation for the DMN Runtime Event Listener
 * that can be subclassed by application implementations to
 * listen for specific events
 */
public class DefaultDMNRuntimeEventListener implements DMNRuntimeEventListener {
    @Override
    public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
    }

    @Override
    public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
    }

    @Override
    public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
    }

    @Override
    public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
    }

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
    }

    @Override
    public void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {
    }

    @Override
    public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {
    }

    @Override
    public void beforeInvokeBKM(BeforeInvokeBKMEvent event) {
    }

    @Override
    public void afterInvokeBKM(AfterInvokeBKMEvent event) {
    }
}
