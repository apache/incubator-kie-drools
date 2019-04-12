public class ModuleWorkItemHandlerConfig implements org.kie.submarine.process.WorkItemHandlerConfig {

    private static final java.util.List<String> handlers = $values$;

    public org.kie.api.runtime.process.WorkItemHandler forName(String name) {
        switch (name) {
        }
        throw new java.util.NoSuchElementException(name);
    }

    public java.util.Collection<String> handlers() {
        return handlers;
    }
}
