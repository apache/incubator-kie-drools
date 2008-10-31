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
}
