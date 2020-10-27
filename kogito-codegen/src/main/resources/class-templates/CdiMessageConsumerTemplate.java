package $Package$;

import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.smallrye.mutiny.Multi;

@io.quarkus.runtime.Startup
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageConsumer.class);

    @javax.inject.Inject
    Application application;

    @javax.inject.Inject
    @javax.inject.Named("$ProcessName$") Process<$Type$> process;

    @javax.inject.Inject
    ConfigBean configBean;

    @javax.inject.Inject
    @javax.inject.Named("kogito_event_publisher") Publisher<String> eventPublisher;

    @javax.annotation.PostConstruct
    void init() {
        setParams(application,
              process,
              $DataType$.class,
              $DataEventType$.class,
              "$Trigger$",
              new DefaultEventConsumerFactory(),
              configBean.useCloudEvents());

        Multi.createFrom().publisher(eventPublisher)
                .invoke(x -> logger.info("Received: {} on thread {}", x, Thread.currentThread().getName()))
                .subscribe()
                .with(this::consume);
    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }
}
