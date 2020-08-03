package $Package$;

@org.springframework.web.context.annotation.ApplicationScope
@org.springframework.stereotype.Component
public class Processes implements org.kie.kogito.process.Processes {

    @org.springframework.beans.factory.annotation.Autowired
    java.util.Collection<org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> processes;

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
