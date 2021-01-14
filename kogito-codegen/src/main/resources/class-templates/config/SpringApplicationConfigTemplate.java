import java.util.Collection;

@org.springframework.stereotype.Component
public class ApplicationConfig extends org.kie.kogito.StaticConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public ApplicationConfig(
            Collection<org.kie.kogito.KogitoConfig> configs) {
        super($Addons$, configs);
    }
}
