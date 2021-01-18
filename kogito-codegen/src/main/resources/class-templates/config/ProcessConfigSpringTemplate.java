import java.util.List;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.services.signal.DefaultSignalManagerHub;

@org.springframework.stereotype.Component
public class ProcessConfig extends org.kie.kogito.process.impl.AbstractProcessConfig {

    @org.springframework.beans.factory.annotation.Autowired
    public ProcessConfig(
            List<WorkItemHandlerConfig> workItemHandlerConfig,
            List<UnitOfWorkManager> unitOfWorkManager,
            List<JobsService> jobsService,
            List<ProcessEventListenerConfig> processEventListenerConfigs,
            List<ProcessEventListener> processEventListeners,
            List<EventPublisher> eventPublishers,
            ConfigBean configBean) {

        super(workItemHandlerConfig,
              processEventListenerConfigs,
              processEventListeners,
              unitOfWorkManager,
              jobsService,
              eventPublishers,
              configBean.getServiceUrl());
    }
}
