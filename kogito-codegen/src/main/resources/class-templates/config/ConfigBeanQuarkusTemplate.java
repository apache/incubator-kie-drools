@javax.inject.Singleton
public class ConfigBean extends org.kie.kogito.conf.StaticConfigBean {

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.service.url")
    java.util.Optional<java.lang.String> kogitoService;

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.messaging.as-cloudevents")
    java.util.Optional<Boolean> useCloudEvents = java.util.Optional.of(true);

    @javax.annotation.PostConstruct
    protected void init() {
        setServiceUrl(kogitoService.orElse(""));
        setCloudEvents(useCloudEvents);
    }
}
