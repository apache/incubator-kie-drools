package org.kie.internal.builder.fluent;

import java.util.function.BiFunction;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;

public interface KieContainerFluent {

    KieSessionFluent newSession();

    KieSessionFluent newSession(String sessionName);

    KieSessionFluent newSessionCustomized(String sessionName, BiFunction<String, KieContainer, KieSessionConfiguration> kieSessionConfigurationCustomizer);
}
