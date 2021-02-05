import java.util.List;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.ProcessEventListenerConfig;
import org.kie.kogito.process.WorkItemHandlerConfig;
import org.kie.kogito.signal.SignalManagerHub;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.services.signal.DefaultSignalManagerHub;

import javax.enterprise.inject.Instance;

@javax.inject.Singleton
public class ProcessConfig extends org.kie.kogito.process.impl.AbstractProcessConfig {

    @javax.inject.Inject
    public ProcessConfig(
            Instance<WorkItemHandlerConfig> workItemHandlerConfig,
            Instance<UnitOfWorkManager> unitOfWorkManager,
            Instance<JobsService> jobsService,
            Instance<ProcessEventListenerConfig> processEventListenerConfigs,
            Instance<ProcessEventListener> processEventListeners,
            Instance<EventPublisher> eventPublishers,
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
