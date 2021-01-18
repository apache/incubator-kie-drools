@org.springframework.stereotype.Component
public class ConfigBean extends org.kie.kogito.conf.StaticConfigBean {

    @org.springframework.beans.factory.annotation.Value("${kogito.service.url:#{null}}")
    java.util.Optional<java.lang.String> kogitoService;

    @org.springframework.beans.factory.annotation.Value("${kogito.messaging.as-cloudevents:#{null}}")
    java.util.Optional<Boolean> useCloudEvents = java.util.Optional.of(true);

    @javax.annotation.PostConstruct
    protected void init() {
        setServiceUrl(kogitoService.orElse(""));
        setCloudEvents(useCloudEvents);
    }
}
