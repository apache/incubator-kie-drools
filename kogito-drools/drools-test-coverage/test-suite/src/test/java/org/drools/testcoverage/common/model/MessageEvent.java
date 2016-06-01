/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class MessageEvent implements Serializable {

    private static final long serialVersionUID = 5700427692523132352L;

    private final Message msg;
    private final Type type;

    public enum Type {
        received, sent
    }

    public MessageEvent(Type type, Message msg) {
        this.type = type;
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("MessageEvent[type=%s, message=%s]", type.toString(), msg.toString());
    }
}
