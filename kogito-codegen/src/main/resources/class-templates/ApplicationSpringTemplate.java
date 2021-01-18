package $Package$;

@org.springframework.stereotype.Component
@org.springframework.web.context.annotation.ApplicationScope
public class Application extends org.kie.kogito.StaticApplication {

    @org.springframework.beans.factory.annotation.Autowired()
    public Application(
            org.kie.kogito.Config config,
            java.util.Collection<org.kie.kogito.KogitoEngine> engines) {
        super(config, engines);
    }
}
