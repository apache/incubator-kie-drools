package $Package$;

import io.smallrye.mutiny.Multi;
import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.reactivestreams.Publisher;

@io.quarkus.runtime.Startup
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    @javax.inject.Inject
    Application application;

    @javax.inject.Inject
    @javax.inject.Named("$ProcessName$") Process<$Type$> process;

    @javax.inject.Inject
    ConfigBean configBean;

    @javax.inject.Inject
    @javax.inject.Named(KogitoEventStreams.PUBLISHER) Publisher<String> eventPublisher;

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
                .subscribe()
                .with(this::consume);
    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }
}
