package org.drools.base.rule;

public interface NamedConsequenceInvoker {

    boolean invokesConsequence(String consequenceName);
}
