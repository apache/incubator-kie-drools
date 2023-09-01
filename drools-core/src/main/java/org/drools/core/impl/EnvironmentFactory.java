package org.drools.core.impl;

import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

public class EnvironmentFactory {

    public static Environment newEnvironment() {
            Environment env = new EnvironmentImpl();
            env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, 
                    new ObjectMarshallingStrategy [] {
                        RuntimeComponentFactory.get().createDefaultObjectMarshallingStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
                    });
        return env;
    }

}
