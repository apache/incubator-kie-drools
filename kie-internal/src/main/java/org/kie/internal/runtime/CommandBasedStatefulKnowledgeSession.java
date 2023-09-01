package org.kie.internal.runtime;

import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSession;

public interface CommandBasedStatefulKnowledgeSession extends StatefulKnowledgeSession {

    ExecutableRunner getRunner();

    KieSession getKieSession();
}
