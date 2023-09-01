package org.kie.dmn.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class DMNResultImpl implements DMNResult, DMNMessageManager {
    private DMNContext context;
    private DMNMessageManager messages;
    private Map<String, DMNDecisionResult> decisionResults;
    private final DMNModel model;

    public DMNResultImpl(DMNModel model) {
        this.model = model;
        messages = new DefaultDMNMessagesManager(model != null ? model.getResource() : null);
        decisionResults = new HashMap<>(  );
    }

    public void setContext(DMNContext context) {
        this.context = context;
    }

    @Override
    public DMNContext getContext() {
        return context;
    }

    @Override
    public List<DMNMessage> getMessages() {
        return messages.getMessages();
    }

    @Override
    public void addAll(List<? extends DMNMessage> messages) {
        this.messages.addAll( messages );
    }

    @Override
    public DMNMessage addMessage(DMNMessage msg) {
        return messages.addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        return messages.addMessage( severity, message, messageType, source );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        return messages.addMessage( severity, message, messageType, source, exception );
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        return messages.getMessages( sevs );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        return messages.addMessage( severity, message, messageType, source, feelEvent );
    }

    @Override
    public boolean hasErrors() {
        return messages.hasErrors();
    }

    public List<DMNDecisionResult> getDecisionResults() {
        return new ArrayList<>( decisionResults.values() );
    }

    public DMNDecisionResult getDecisionResultByName( String name ) {
        return decisionResults.values().stream().filter(dr -> dr.getDecisionName().equals(name)).findFirst().orElse(null);
    }

    public DMNDecisionResult getDecisionResultById( String id ) {
        return decisionResults.get( id );
    }

    public void addDecisionResult(DMNDecisionResult result) {
        this.decisionResults.put(result.getDecisionId(), result);
    }

    @Override
    public String toString() {
        return "DMNResultImpl{" +
               "context=" + context +
               ", messages=" + messages +
               '}';
    }

    /**
     * Returns the model this DMNResult belongs to.
     */
    public DMNModel getModel() {
        return model;
    }
    @Override
    public void addAllUnfiltered(List<? extends DMNMessage> messages) {
        this.messages.addAllUnfiltered(messages);
    }
}
