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

package org.drools.core.event.knowlegebase.impl;

import org.kie.internal.KnowledgeBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;

public class AfterKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements AfterKieBaseLockedEvent {

    public AfterKnowledgeBaseLockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return null;
    }

}
