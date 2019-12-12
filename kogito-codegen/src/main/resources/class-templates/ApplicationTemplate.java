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
        if (config().process() != null) {
            if (eventPublishers != null) {
                eventPublishers.forEach(publisher -> 
                unitOfWorkManager().eventManager().addPublisher(publisher));
                
            }
            unitOfWorkManager().eventManager().setService(kogitoService.orElse(""));
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
    }
}
