package org.kie.dmn.validation;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.core.util.DefaultDMNMessagesManager;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

/**
 * A helper class to report messages
 */
public class MessageReporter {
    DMNMessageManager messages = new DefaultDMNMessagesManager();

    public DMNMessageManager getMessages() {
        return messages;
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message0 message) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message ), message.getType(), source ) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message1 message, Object p1) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1 ), message.getType(), source ) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message2 message, Object p1, Object p2) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2 ), message.getType(), source ) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message3 message, Object p1, Object p2, Object p3) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2, p3 ), message.getType(), source ) );
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message4 message, Object p1, Object p2, Object p3, Object p4) {
        messages.addMessage( new DMNMessageImpl( severity, MsgUtil.createMessage( message, p1, p2, p3, p4 ), message.getType(), source ) );
    }
}
