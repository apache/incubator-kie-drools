@org.springframework.stereotype.Component
public class ConfigBean {

    @org.springframework.beans.factory.annotation.Value("${kogito.service.url:#{null}}")
    java.util.Optional<java.lang.String> kogitoService;

    public String getServiceUrl() {
        return kogitoService.orElse("");
    }

}
