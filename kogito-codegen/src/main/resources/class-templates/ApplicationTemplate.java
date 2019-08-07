package $Package$;


import org.kie.kogito.Config;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWorkManager;

public class Application implements org.kie.kogito.Application {

   
    public Config config() {
        return config;
    }
    
    public UnitOfWorkManager unitOfWorkManager() {
        return config().process().unitOfWorkManager();
    }
    
    public void setup() {
        
        if (eventPublishers != null) {
            eventPublishers.forEach(publisher -> 
            unitOfWorkManager().eventManager().addPublisher(publisher));
            
        }
    }
}
