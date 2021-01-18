import javax.enterprise.inject.Instance;

@javax.inject.Singleton
public class ApplicationConfig extends org.kie.kogito.StaticConfig {

    @javax.inject.Inject
    public ApplicationConfig(
            Instance<org.kie.kogito.KogitoConfig> configs) {
        super($Addons$, configs);
    }
}