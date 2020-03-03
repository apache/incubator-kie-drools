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

public class FireAllRuleMessage extends AbstractMessage
        implements Serializable, ResultMessage<Long> {

    private long counter;

    /* Empty constructor for serialization */
    public FireAllRuleMessage(){}

    public FireAllRuleMessage(String id, long counter) {
        super(id);
        this.counter = counter;
    }

    @Override
    public Long getResult() {
        return getCounter();
    }

    public long getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "FireAllRuleMessage{" +
                "counter=" + counter +
                ", id='" + id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
