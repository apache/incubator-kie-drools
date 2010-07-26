/**
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.broker.events;

import java.util.Date;

/**
 * A default implementation for Event
 * 
 * @author etirelli
 */
public class EventImpl<T> implements Event<T> {
    private final long timestamp;
    private final T object;
    
    public EventImpl(long timestamp,
                     T object) {
        super();
        this.timestamp = timestamp;
        this.object = object;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getObject() {
        return object;
    }
    
    public Date getDate() {
        return new Date( this.timestamp );
    }

    public String toString() {
        return "Event( "+timestamp+", "+object+" )";
    }

    
}
