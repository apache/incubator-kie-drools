package org.kie.internal.fluent.runtime;

import org.kie.internal.fluent.ContextFluent;
import org.kie.internal.fluent.runtime.process.ProcessFluent;
import org.kie.internal.fluent.runtime.rule.RuleFluent;

public interface KieSessionFluent<T> 
    extends RuleFluent<T>,
    ProcessFluent<T>,
    ContextFluent<T> {

}
