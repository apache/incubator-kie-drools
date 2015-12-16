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

package org.drools.compiler.integrationtests.eventgenerator.example;

import org.drools.compiler.integrationtests.eventgenerator.Event;

public class ProductionEvent extends Event {

    /**
     * Special constructor for a production event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public ProductionEvent(String parentId) {
        super(EventType.PRODUCTION, parentId);
    }

    /**
     * Special constructor for a production event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public ProductionEvent(String parentId, long start, long end) {
        super(EventType.PRODUCTION, parentId, start, end);
    }
}
