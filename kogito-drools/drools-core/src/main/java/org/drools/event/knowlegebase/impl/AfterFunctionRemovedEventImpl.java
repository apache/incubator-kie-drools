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
import org.drools.event.knowledgebase.AfterFunctionRemovedEvent;

public class AfterFunctionRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterFunctionRemovedEvent {
    private String function;
    
    public AfterFunctionRemovedEventImpl(KnowledgeBase knowledgeBase, String function) {
        super( knowledgeBase );
        this.function = function;
    }

    public String getFunction() {
        return this.function;
    }

	@Override
	public String toString() {
		return "==>[AfterFunctionRemovedEventImpl: getFunction()=" + getFunction() + ", getKnowledgeBase()="
				+ getKnowledgeBase() + "]";
	}
    
}
