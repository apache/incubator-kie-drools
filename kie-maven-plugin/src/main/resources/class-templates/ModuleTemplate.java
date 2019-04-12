package $Package$;

import org.kie.submarine.Config;

public class Module {

    private static final Config config =
            new org.kie.submarine.StaticConfig(
                    new org.kie.submarine.process.impl.StaticProcessConfig(
                            new $WorkItemHandlerConfig$()));

    public Config config() {
        return config;
    }
}
