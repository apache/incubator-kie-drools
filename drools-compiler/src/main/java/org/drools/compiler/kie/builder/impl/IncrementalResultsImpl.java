/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kie.builder.impl;

import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.api.builder.Message;

import java.util.ArrayList;
import java.util.List;

public class IncrementalResultsImpl implements IncrementalResults {

    private long          idGenerator = 1L;

    private List<Message> addedMessages = new ArrayList<Message>();
    private List<Message> removedMessages = new ArrayList<Message>();

    @Override
    public List<Message> getAddedMessages() {
        return addedMessages;
    }

    @Override
    public List<Message> getRemovedMessages() {
        return removedMessages;
    }

    public void addMessage(KnowledgeBuilderResult result) {
        addedMessages.add( new MessageImpl( idGenerator++, result ) );
    }

    public void removeMessage(KnowledgeBuilderResult result) {
        removedMessages.add( new MessageImpl( idGenerator++, result ) );
    }
}
