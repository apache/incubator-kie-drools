package $Package$;

@javax.enterprise.context.ApplicationScoped
public class Processes implements org.kie.kogito.process.Processes {

    @javax.inject.Inject
    javax.enterprise.inject.Instance<org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> processes;

    private java.util.Map<String, org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> mappedProcesses = new java.util.HashMap<>();

    @javax.annotation.PostConstruct
    public void setup() {
        for (org.kie.kogito.process.Process<? extends org.kie.kogito.Model> process : processes) {
            mappedProcesses.put(process.id(), process);
        }
    }

    public org.kie.kogito.process.Process<? extends org.kie.kogito.Model> processById(String processId) {
        return mappedProcesses.get(processId);
    }

    public java.util.Collection<String> processIds() {
        return mappedProcesses.keySet();
    }
}
