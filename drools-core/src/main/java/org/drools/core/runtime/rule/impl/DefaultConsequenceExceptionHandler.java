package org.drools.core.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleRuntime;

public class DefaultConsequenceExceptionHandler implements ConsequenceExceptionHandler, Externalizable {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void handleException(Match activation,
                                RuleRuntime workingMemory,
                                Exception exception) {
        throw new org.kie.api.runtime.rule.ConsequenceException(exception, workingMemory, activation );
    }

}
