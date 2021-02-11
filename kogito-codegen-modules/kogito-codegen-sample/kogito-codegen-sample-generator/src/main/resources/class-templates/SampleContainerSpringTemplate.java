import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Component
@org.springframework.web.context.annotation.ApplicationScope
public class SampleRuntime extends org.kie.kogito.codegen.sample.core.SampleRuntimeImpl {

    public SampleRuntime(org.kie.kogito.Application app) {
        super(app);
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