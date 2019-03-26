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

package org.drools.core.audit.event;

/**
 * An event filter that can be used to filter working memory events.
 * By default, all events are allowed.  You can filter out any of the
 * three types of working memory events by setting the allow boolean
 * for that type to false.
 */
public class WorkingMemoryLogEventFilter
    implements
    ILogEventFilter {

    private boolean allowInsertEvents  = true;
    private boolean allowUpdateEvents  = true;
    private boolean allowRetractEvents = true;

    public WorkingMemoryLogEventFilter(final boolean allowAssertEvents,
                                       final boolean allowModifyEvents,
                                       final boolean allowRetractEvents) {
        setAllowInsertEvents( allowAssertEvents );
        setAllowUpdateEvents( allowModifyEvents );
        setAllowRetractEvents( allowRetractEvents );
    }

    /**
     * @see org.kie.audit.event.ILogEventFilter
     */
    public boolean acceptEvent(final LogEvent event) {
        switch ( event.getType() ) {
            case LogEvent.INSERTED :
                return this.allowInsertEvents;
            case LogEvent.UPDATED :
                return this.allowUpdateEvents;
            case LogEvent.RETRACTED :
                return this.allowRetractEvents;
            default :
                return true;
        }
    }

    public void setAllowInsertEvents(final boolean allowInsertEvents) {
        this.allowInsertEvents = allowInsertEvents;
    }

    public void setAllowUpdateEvents(final boolean allowUpdateEvents) {
        this.allowUpdateEvents = allowUpdateEvents;
    }

    public void setAllowRetractEvents(final boolean allowRetractEvents) {
        this.allowRetractEvents = allowRetractEvents;
    }
}
