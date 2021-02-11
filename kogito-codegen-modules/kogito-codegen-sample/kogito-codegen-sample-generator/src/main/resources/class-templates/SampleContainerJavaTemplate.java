import java.util.HashMap;
import java.util.Map;

public class SampleRuntime extends org.kie.kogito.codegen.sample.core.SampleRuntimeImpl {

    /**
     * The Java scenario must have a constructor with Application
     * @param app
     */
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
