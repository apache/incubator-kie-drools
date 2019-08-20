package $Package$;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

public class ApplicationProcesses implements Processes {

    Object processes;
        
    private Map<String, Process<? extends Model>> mappedProcesses = new HashMap<>();
    
    @PostConstruct
    public void setup() {
        
        for (Process<? extends Model> process : processes) {
            mappedProcesses.put(process.id(), process);
        }
    }
    
    public Process<? extends Model> processById(String processId) {
        return mappedProcesses.get(processId);
    }
    
    public Collection<String> processIds() {
        return mappedProcesses.keySet();
    }
}
