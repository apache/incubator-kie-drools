package $Package$;

public class Processes implements org.kie.kogito.process.Processes {

    private final Application application;
    private java.util.Map<String, org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> mappedProcesses = new java.util.concurrent.ConcurrentHashMap<>();

    public Processes(Application application) {
        this.application = application;
    }

    public org.kie.kogito.process.Process<? extends org.kie.kogito.Model> processById(String processId) {
        if ("$ProcessId".equals(processId)) {
            return mappedProcesses.computeIfAbsent("$ProcessId", k -> new $ProcessClassName$(application).configure());
        }
        return null;
    }

    public java.util.Collection<String> processIds() {
        return java.util.Arrays.asList("$ProcessId$");
    }
}
