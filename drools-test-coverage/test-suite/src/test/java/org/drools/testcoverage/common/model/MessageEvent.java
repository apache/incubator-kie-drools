/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class MessageEvent implements Serializable {

    private static final long serialVersionUID = 5700427692523132353L;

    private final Message msg;
    private final Type type;
    private final long duration;

    public enum Type {
        received, sent
    }

    public MessageEvent(final Type type, final Message msg) {
        this(type, msg, 0);
    }

    public MessageEvent(final Type type, final Message msg, final long duration) {
        this.type = type;
        this.msg = msg;
        this.duration = duration;
    }

    public Message getMsg() {
        return msg;
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return String.format("MessageEvent[type=%s, message=%s, duration=%d]", type.toString(), msg.toString(), duration);
    }
}
