/*
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

package org.drools.core.event;

import org.drools.core.event.rule.impl.ObjectInsertedEventImpl;
import org.kie.api.event.rule.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugRuleRuntimeEventListener
    implements
    RuleRuntimeEventListener {

    protected static transient Logger logger = LoggerFactory.getLogger(org.kie.api.event.rule.DebugRuleRuntimeEventListener.class);

    public DebugRuleRuntimeEventListener() {
        // intentionally left blank
    }

    public void objectInserted(ObjectInsertedEvent event) {
        logger.info( event.toString() );
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        logger.info( event.toString() );
    }

    public void objectDeleted(ObjectDeletedEvent event) {
        logger.info( event.toString() );
    }

}
