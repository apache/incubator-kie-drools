/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.persistence.proto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Proto {

    private String syntax = "proto2";
    private String packageName;
    private String[] headers;

    private List<ProtoMessage> messages = new ArrayList<>();
    private List<ProtoEnum> enums = new ArrayList<>();

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
            this.messages.sort(Comparator.comparing(ProtoMessage::getName));
        }
    }

    public List<ProtoEnum> getEnums() {
        return enums;
    }

    public void addEnum(ProtoEnum protoEnum) {
        if(!enums.contains(protoEnum)) {
            this.enums.add(protoEnum);
            this.enums.sort(Comparator.comparing(ProtoEnum::getName));
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
        enums.forEach(e -> messagesAsString.append(e.toString()));
        
        StringBuilder builder = new StringBuilder();
        builder.append("syntax = \"" + syntax + "\"; \n");
        if (packageName != null) {
            builder.append("package " + packageName + "; \n");
        }
        builder.append(headersAsString.toString() + "\n" + messagesAsString.toString());
        
        return  builder.toString();
    }
}
