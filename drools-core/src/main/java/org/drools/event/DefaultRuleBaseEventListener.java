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

package org.drools.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.event.AfterFunctionRemovedEvent;
import org.drools.event.AfterRuleAddedEvent;
import org.drools.event.AfterRuleRemovedEvent;
import org.drools.event.BeforeFunctionRemovedEvent;
import org.drools.event.BeforeRuleAddedEvent;
import org.drools.event.BeforeRuleRemovedEvent;


public class DefaultRuleBaseEventListener
    implements
    RuleBaseEventListener {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
        // intentionally left blank
    }

    public void afterPackageAdded(AfterPackageAddedEvent event) {
        // intentionally left blank
    }

    public void afterPackageRemoved(AfterPackageRemovedEvent event) {
        // intentionally left blank
    }

    public void afterRuleAdded(AfterRuleAddedEvent event) {
        // intentionally left blank
    }

    public void afterRuleBaseLocked(AfterRuleBaseLockedEvent event) {
        // intentionally left blank
    }

    public void afterRuleBaseUnlocked(AfterRuleBaseUnlockedEvent event) {
        // intentionally left blank
    }

    public void afterRuleRemoved(AfterRuleRemovedEvent event) {
        // intentionally left blank
    }

    public void beforeFunctionRemoved(BeforeFunctionRemovedEvent event) {
        // intentionally left blank
    }

    public void beforePackageAdded(BeforePackageAddedEvent event) {
        // intentionally left blank
    }

    public void beforePackageRemoved(BeforePackageRemovedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleAdded(BeforeRuleAddedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleBaseLocked(BeforeRuleBaseLockedEvent event) {
        // intentionally left blank
    }

    public void beforeRuleBaseUnlocked(BeforeRuleBaseUnlockedEvent event) {
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
