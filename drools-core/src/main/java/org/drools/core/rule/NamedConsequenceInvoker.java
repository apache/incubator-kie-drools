package org.drools.core.rule;

public interface NamedConsequenceInvoker {

    boolean invokesConsequence(String consequenceName);
}
