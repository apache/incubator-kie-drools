/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Oct 17, 2007
 */
package org.drools.temporal;

/**
 * A clock interface that all engine clocks must implement
 * 
 * @author etirelli
 */
public interface SessionClock {
    
    /**
     * Returns the current time. There is no semantics attached
     * to the long return value, so it will depend on the actual
     * implementation. For isntance, for a real clock it may be
     * milliseconds.
     * 
     * @return The current time. The unit of the time, depends on
     * the actual clock implementation.
     * 
     */
    public long getCurrentTime();
    
}
