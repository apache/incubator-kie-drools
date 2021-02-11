import java.util.HashMap;
import java.util.Map;

@javax.enterprise.context.ApplicationScoped()
public class SampleRuntime extends org.kie.kogito.codegen.sample.core.SampleRuntimeImpl {

    @javax.inject.Inject
    protected org.kie.kogito.Application application;

    public SampleRuntime() {
        super();
    }

    @javax.annotation.PostConstruct
    protected void init() {
        initApplication(application);
        initContent();
    }

    private void initContent() {
        Map<String, String> content = new HashMap<>();
        loadContent(content);
        addModels(content);
    }

    private void loadContent(Map<String, String> content) {
        // populated via codegen
    }
}
