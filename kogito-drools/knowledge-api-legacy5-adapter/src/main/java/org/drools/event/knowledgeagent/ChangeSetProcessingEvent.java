/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.event.knowledgeagent;

import java.util.EventObject;
import org.drools.ChangeSet;

public class ChangeSetProcessingEvent
        extends EventObject {

    private static final long serialVersionUID = 510l;

    public ChangeSetProcessingEvent(ChangeSet changeSet) {
        super(changeSet);
    }

    public ChangeSet getChangeSet() {
        return (ChangeSet) getSource();
    }

    @Override
    public String toString() {
        return "==>[ChangeSetProcessingEvent: " + getChangeSet() + "]";
    }
}
