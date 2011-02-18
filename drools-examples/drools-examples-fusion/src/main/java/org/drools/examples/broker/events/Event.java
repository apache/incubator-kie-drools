/*
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
 * An event interface for the feeder framework
 * 
 * This is really a very simple and quick way of doing. In the future we may use
 * the field accessors framework that we use in drools to make this transparent
 * 
 * @author etirelli
 *
 */
public interface Event<T> {
    
    /**
     * Returns the timestamp from this event
     * 
     * @return
     */
    public long getTimestamp();
    
    /**
     * This is the same as getTimestamp, but returns a Date 
     * object instead
     * 
     * @return
     */
    public Date getDate();

    /**
     * Returns this event's actual object
     * 
     * @return
     */
    public T getObject();

}
