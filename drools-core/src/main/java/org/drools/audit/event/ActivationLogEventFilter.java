/**
 * Copyright 2005 JBoss Inc
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

package org.drools.audit.event;

/**
 * An event filter that can be used to filter assertion events.
 * By default, all events are allowed.  You can filter out any of the
 * four types of assertion events by setting the allow boolean
 * for that type to false.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen </a>
 */
public class ActivationLogEventFilter
    implements
    ILogEventFilter {

    private boolean allowActivationCreatedEvents    = true;
    private boolean allowActivationCancelledEvents  = true;
    private boolean allowBeforeActivationFireEvents = true;
    private boolean allowAfterActivationFireEvents  = true;

    public ActivationLogEventFilter(final boolean allowActivationCreatedEvents,
                                    final boolean allowActivationCancelledEvents,
                                    final boolean allowBeforeActivationFireEvents,
                                    final boolean allowAfterActivationFireEvents) {
        setAllowActivationCreatedEvents( allowActivationCreatedEvents );
        setAllowActivationCancelledEvents( allowActivationCancelledEvents );
        setAllowBeforeActivationFireEvents( allowBeforeActivationFireEvents );
        setAllowAfterActivationFireEvents( allowAfterActivationFireEvents );
    }

    /**
     * @see org.drools.audit.event.ILogEventFilter
     */
    public boolean acceptEvent(final LogEvent event) {
        switch ( event.getType() ) {
            case LogEvent.ACTIVATION_CREATED :
                return this.allowActivationCreatedEvents;
            case LogEvent.ACTIVATION_CANCELLED :
                return this.allowActivationCancelledEvents;
            case LogEvent.BEFORE_ACTIVATION_FIRE :
                return this.allowBeforeActivationFireEvents;
            case LogEvent.AFTER_ACTIVATION_FIRE :
                return this.allowAfterActivationFireEvents;
            default :
                return true;
        }
    }

    public void setAllowActivationCreatedEvents(final boolean allowActivationCreatedEvents) {
        this.allowActivationCreatedEvents = allowActivationCreatedEvents;
    }

    public void setAllowActivationCancelledEvents(final boolean allowActivationCancelledEvents) {
        this.allowActivationCancelledEvents = allowActivationCancelledEvents;
    }

    public void setAllowBeforeActivationFireEvents(final boolean allowBeforeActivationFireEvents) {
        this.allowBeforeActivationFireEvents = allowBeforeActivationFireEvents;
    }

    public void setAllowAfterActivationFireEvents(final boolean allowAfterActivationFireEvents) {
        this.allowAfterActivationFireEvents = allowAfterActivationFireEvents;
    }
}
