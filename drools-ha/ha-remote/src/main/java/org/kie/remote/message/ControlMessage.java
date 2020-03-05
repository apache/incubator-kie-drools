/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.remote.message;

import java.io.Serializable;
import java.util.Queue;

public class ControlMessage extends AbstractMessage implements Serializable, Message {

    private long offset;
    private Queue<Serializable> sideEffects;

    /* Empty constructor for serialization */
    public ControlMessage() {}

    public ControlMessage( String id, Queue<Serializable> sideEffects) {
        super(id);
        this.sideEffects = sideEffects;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public Queue<Serializable> getSideEffects() {
        return sideEffects;
    }

    @Override
    public String toString() {
        return "ControlMessage{" +
                "offset=" + offset +
                ", sideEffects=" + sideEffects +
                ", id='" + id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
