/**
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
package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class IncrementalResultsImpl implements IncrementalResults {

    private long          idGenerator = 1L;

    private List<Message> addedMessages = new ArrayList<>();
    private List<Message> removedMessages = new ArrayList<>();

    @Override
    public List<Message> getAddedMessages() {
        return addedMessages;
    }

    @Override
    public List<Message> getRemovedMessages() {
        return removedMessages;
    }

    public void addMessage(KnowledgeBuilderResult result, String kieBaseName ) {
        addedMessages.add(result.asMessage(idGenerator++).setKieBaseName(kieBaseName));
    }

    public void removeMessage(KnowledgeBuilderResult result, String kieBaseName) {
        removedMessages.add(result.asMessage(idGenerator++).setKieBaseName(kieBaseName));
    }
}
