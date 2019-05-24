package $Package$;

import org.kie.kogito.Config;

public class Module {

    private static final Config config =
            new org.kie.kogito.StaticConfig(
                    new org.kie.kogito.process.impl.StaticProcessConfig(
                            new $WorkItemHandlerConfig$(),
                            new $ProcessEventListenerConfig$()));

    public Config config() {
        return config;
    }
}
