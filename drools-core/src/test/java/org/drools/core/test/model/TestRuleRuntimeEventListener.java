/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.test.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.event.ActivationCancelledEvent;
import org.drools.core.event.ActivationCreatedEvent;
import org.drools.core.event.AfterActivationFiredEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public class TestRuleRuntimeEventListener
    implements
    RuleRuntimeEventListener,
    Externalizable {

    private static final long serialVersionUID = 510l;
    public int                asserted;
    public int                modified;
    public int                deleted;
    public int                tested;
    public int                created;
    public int                cancelled;
    public int                fired;

    public TestRuleRuntimeEventListener() {
        // intentionally left blank
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        asserted    = in.readInt();
        modified    = in.readInt();
        deleted = in.readInt();
        tested    = in.readInt();
        created    = in.readInt();
        cancelled    = in.readInt();
        fired    = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(asserted);
        out.writeInt(modified);
        out.writeInt(deleted);
        out.writeInt(tested);
        out.writeInt(created);
        out.writeInt(cancelled);
        out.writeInt(fired);
    }

    public void objectInserted(final ObjectInsertedEvent event) {
        this.asserted++;
    }

    public void objectUpdated(final ObjectUpdatedEvent event) {
        this.modified++;
    }

    public void objectDeleted(final ObjectDeletedEvent event) {
        this.deleted++;
    }

    /*
     * public void conditionTested(ConditionTestedEvent event) { tested++; }
     */
    public void activationCreated(final ActivationCreatedEvent event) {
        this.created++;
    }

    public void activationCancelled(final ActivationCancelledEvent event) {
        this.cancelled++;
    }

    public void activationFired(final AfterActivationFiredEvent event) {
        this.fired++;
    }
}
