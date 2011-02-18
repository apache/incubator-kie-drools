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

package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;

public class AfterKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgeBaseLockedEvent {

    public AfterKnowledgeBaseLockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
        // TODO Auto-generated constructor stub
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return null;
    }

}
