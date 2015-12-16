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

package org.drools.time;

/**
 * A clock interface that all engine clocks must implement
 */
public interface SessionClock {

    /**
     * Returns the current time. There is no semantics attached
     * to the long return value, so it will depend on the actual
     * implementation. For instance, for a real time clock it may be
     * milliseconds.
     * 
     * @return The current time. The unit of the time, depends on
     * the actual clock implementation.
     */
    public long getCurrentTime();

}
