package org.kie.kogito.codegen.process.persistence.proto;

import java.util.ArrayList;
import java.util.List;

public class Proto {

    private String syntax = "proto2";
    private String packageName;
    private String[] headers;

    private List<ProtoMessage> messages = new ArrayList<ProtoMessage>();

    public Proto(String packageName, String... headers) {
        super();
        this.packageName = packageName;
        this.headers = headers;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<ProtoMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ProtoMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ProtoMessage message) {
        if (!messages.contains(message)) {
            this.messages.add(message);
            this.messages.sort((ProtoMessage m1, ProtoMessage m2) -> m1.getName().compareTo(m2.getName()));
        }
    }

    @Override
    public String toString() {
        StringBuilder headersAsString = new StringBuilder();
        
        for (String header : headers) {
            headersAsString.append(header + "\n");
        }
        StringBuilder messagesAsString = new StringBuilder();
        
        messages.forEach(m -> messagesAsString.append(m.toString()));
        
        StringBuilder builder = new StringBuilder();
        builder.append("syntax = \"" + syntax + "\"; \n");
        if (packageName != null) {
            builder.append("package " + packageName + "; \n");
        }
        builder.append(headersAsString.toString() + "\n" + messagesAsString.toString());
        
        return  builder.toString();
    }
}
