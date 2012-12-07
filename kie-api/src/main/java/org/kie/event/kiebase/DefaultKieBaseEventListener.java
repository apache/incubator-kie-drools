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

package org.kie.event.kiebase;

public class DefaultKieBaseEventListener
    implements
    KieBaseEventListener {

    public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
        // intentionally left blank
    }

    public void afterKnowledgeBaseLocked(AfterKieBaseLockedEvent event) {
        // intentionally left blank
    }

    public void afterKnowledgeBaseUnlocked(AfterKieBaseUnlockedEvent event) {
        // intentionally left blank
    }

    public void afterKnowledgePackageAdded(AfterKnowledgePackageAddedEvent event) {
        // intentionally left blank
    }

    public void afterKnowledgePackageRemoved(AfterKnowledgePackageRemovedEvent event) {
        // intentionally left blank
    }

    public void afterRuleAdded(AfterRuleAddedEvent event) {
        // intentionally left blank
    }

    public void afterRuleRemoved(AfterRuleRemovedEvent event) {
        // intentionally left blank
    }

    public void beforeFunctionRemoved(BeforeFunctionRemovedEvent event) {
        // intentionally left blank
    }

    public void beforeKnowledgeBaseLocked(BeforeKieBaseLockedEvent event) {
        // intentionally left blank
    }

    public void beforeKnowledgeBaseUnlocked(BeforeKieBaseUnlockedEvent event) {
        // intentionally left blank
    }

    public void beforeKnowledgePackageAdded(BeforeKnowledgePackageAddedEvent event) {
        // intentionally left blank
    }

    public void beforeKnowledgePackageRemoved(BeforeKnowledgePackageRemovedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleAdded(BeforeRuleAddedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleRemoved(BeforeRuleRemovedEvent event) {
        // intentionally left blank
    }

	public void beforeProcessAdded(BeforeProcessAddedEvent event) {
        // intentionally left blank
	}

	public void afterProcessAdded(AfterProcessAddedEvent event) {
        // intentionally left blank
	}

	public void beforeProcessRemoved(BeforeProcessRemovedEvent event) {
        // intentionally left blank
	}

	public void afterProcessRemoved(AfterProcessRemovedEvent event) {
        // intentionally left blank
	}

}
