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

package org.drools.jsr94.rules;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.kie.api.runtime.rule.EntryPoint;

import javax.rules.Handle;

/**
 * The Drools implementation of the <code>Handle</code> interface which provides
 * a marker interface for Drools-specific object identity mechanism. When using
 * the <code>StatefulRuleSession</code> objects that are added to rule session
 * state are identified using a Drools-supplied <code>Handle</code>
 * implementation.
 * <p/>
 * <code>Handle</code>s are used to unambigiously identify objects within the
 * rule session state and should not suffer many of the object identity issues
 * that arise when using muliple class loaders, serializing
 * <code>StatefulRuleSessions</code>, or using <code>Object.equals</code> or
 * <code>object1 == object2</code> reference equality.
 */
public class Jsr94EventFactHandle extends EventFactHandle
    implements
    Handle {

    private static final long serialVersionUID = 510l;

    /**
     * Creates a new event fact handle.
     * 
     * @param id this event fact handle ID
     * @param object the event object encapsulated in this event fact handle
     * @param recency the recency of this event fact handle
     * @param timestamp the timestamp of the occurence of this event
     * @param duration the duration of this event. May be 0 (zero) in case this is a primitive event.
     */
    public Jsr94EventFactHandle(final long id,
                                final Object object,
                                final long recency,
                                final long timestamp,
                                final long duration,
                                final EntryPoint entryPoint) {
        super( (int) id,
               object,
               recency,
               timestamp,
               duration,
               (InternalWorkingMemoryEntryPoint) entryPoint );
    }

}
