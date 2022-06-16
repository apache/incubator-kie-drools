package org.kie.dmn.ruleset2dmn;

import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDMNRuntimeEventListener extends DefaultDMNRuntimeEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(TestDMNRuntimeEventListener.class);
    
    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        LOG.debug("Decision Table selected: {}", event.getSelected());
    }
    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        LOG.debug("OUTPUT: {}", event.getResult().getContext());
    }
}
