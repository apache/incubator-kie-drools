package $Package$;

@javax.inject.Singleton
public class Application extends org.kie.kogito.StaticApplication {

    @javax.inject.Inject
    public Application(
            org.kie.kogito.Config config,
            javax.enterprise.inject.Instance<org.kie.kogito.KogitoEngine> engines) {
        super(config, engines);
    }
}
