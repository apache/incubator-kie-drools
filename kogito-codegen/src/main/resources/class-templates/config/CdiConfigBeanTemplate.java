@javax.inject.Singleton
public class ConfigBean {

    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "kogito.service.url")
    java.util.Optional<java.lang.String> kogitoService;

    public String getServiceUrl() {
        return kogitoService.orElse("");
    }

}
