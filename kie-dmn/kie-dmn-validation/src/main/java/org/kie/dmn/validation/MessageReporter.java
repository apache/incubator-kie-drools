package org.kie.dmn.validation;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.kie.dmn.core.util.*;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A helper class to report messages
 */
public class MessageReporter {
    private List<DMNMessage> messages;

    public MessageReporter() {
        messages = new ArrayList<>(  );
    }

    public List<DMNMessage> getMessages() {
        return messages;
    }

    public void report(DMNMessage.Severity severity, DMNModelInstrumentedBase source, Msg.Message0 message, Objects... params ) {
        messages.add( new DMNMessageImpl( severity, MsgUtil.createMessage( message ), message.getType(), source ) );
    }
}
