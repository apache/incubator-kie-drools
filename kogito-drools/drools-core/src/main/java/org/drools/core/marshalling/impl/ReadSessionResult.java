package org.drools.core.marshalling.impl;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;

public class ReadSessionResult {

    private final StatefulKnowledgeSessionImpl session;
    private final ProtobufMessages.KnowledgeSession deserializedMessage;

    public ReadSessionResult(StatefulKnowledgeSessionImpl session, ProtobufMessages.KnowledgeSession deserializedMessage) {
        this.session = session;
        this.deserializedMessage = deserializedMessage;
    }

    public StatefulKnowledgeSessionImpl getSession() {
        return session;
    }

    public ProtobufMessages.KnowledgeSession getDeserializedMessage() {
        return deserializedMessage;
    }
}
