package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultDMNMessagesManager
        implements DMNMessageManager {

    // should we use a sorted set instead?
    private List<DMNMessage> messages;

    public DefaultDMNMessagesManager() {
        this.messages = new ArrayList<>();
    }

    @Override
    public List<DMNMessage> getMessages() {
        return messages;
    }

    @Override
    public List<DMNMessage> getMessages(DMNMessage.Severity... sevs) {
        List<DMNMessage.Severity> severities = Arrays.asList( sevs );
        return messages.stream().filter( m -> severities.contains( m.getSeverity() ) ).collect( Collectors.toList() );
    }

    @Override
    public boolean hasErrors() {
        return messages.stream().anyMatch( m -> DMNMessage.Severity.ERROR.equals( m.getSeverity() ) );
    }

    @Override
    public void addAll(List<DMNMessage> messages) {
        for ( DMNMessage message : messages ) {
            addMessage( message );
        }
    }

    @Override
    public DMNMessage addMessage(DMNMessage newMessage) {
        for( DMNMessage existingMessage : messages ) {
            if( isDuplicate( existingMessage, newMessage ) ) {
                return existingMessage;
            }
        }
        this.messages.add( newMessage );
        return newMessage;
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source );
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, Throwable exception) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, exception );
        return addMessage( msg );
    }

    @Override
    public DMNMessage addMessage(DMNMessage.Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
        DMNMessageImpl msg = new DMNMessageImpl( severity, message, messageType, source, feelEvent );
        return addMessage( msg );
    }

    private boolean isDuplicate(DMNMessage existingMsg, DMNMessage newMessage) {
        return existingMsg.getMessageType().equals( newMessage.getMessageType() ) &&
               existingMsg.getSourceReference() == newMessage.getSourceReference();
    }
}
