/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.eventgenerator.example;

import org.drools.mvel.integrationtests.eventgenerator.Event;

public class FailureEvent extends Event {

    /**
     * Special constructor for a failure event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public FailureEvent(String parentId) {
        super(EventType.FAILURE, parentId);
    }

    /**
     * Special constructor for a faliure event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public FailureEvent(String parentId, long start, long end) {
        super(EventType.FAILURE, parentId, start, end);
    }

}
