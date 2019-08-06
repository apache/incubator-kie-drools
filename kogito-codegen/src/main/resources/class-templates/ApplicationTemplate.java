package $Package$;


import org.kie.kogito.Config;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWorkManager;

public class Application implements org.kie.kogito.Application {

    private static UnitOfWorkManager unitOfWorkManager;
    
    public Config config() {
        return config;
    }
    
    public UnitOfWorkManager unitOfWorkManager() {
        return unitOfWorkManager;
    }
    
    public void setup() {
        System.out.println("Starting up Kogito application...." + eventPublishers);
        
        if (eventPublishers != null) {
            eventPublishers.forEach(publisher -> 
            unitOfWorkManager().eventManager().addPublisher(publisher));
            
        }
    }
}
