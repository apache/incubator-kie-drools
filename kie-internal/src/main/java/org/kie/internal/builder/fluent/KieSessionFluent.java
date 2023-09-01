package org.kie.internal.builder.fluent;

public interface KieSessionFluent
    extends RuleFluent<KieSessionFluent, ExecutableBuilder>,
    ProcessFluent<KieSessionFluent, ExecutableBuilder>,
    ContextFluent<KieSessionFluent, ExecutableBuilder>,
    TimeFluent<KieSessionFluent> {

}
