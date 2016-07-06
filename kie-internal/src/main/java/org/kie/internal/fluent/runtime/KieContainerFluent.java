package org.kie.internal.fluent.runtime;


public interface KieContainerFluent {
    KieSessionFluent newSession();
    KieSessionFluent newSession(String id);
}
