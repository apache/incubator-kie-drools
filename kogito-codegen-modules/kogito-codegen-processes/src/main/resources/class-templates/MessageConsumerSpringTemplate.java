package $Package$;

import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@org.springframework.stereotype.Component()
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    @org.springframework.beans.factory.annotation.Autowired()
    $Type$MessageConsumer(
            Application application,
            @org.springframework.beans.factory.annotation.Qualifier("$ProcessName$") Process<$Type$> process,
            ConfigBean configBean,
            @org.springframework.beans.factory.annotation.Qualifier(KogitoEventStreams.PUBLISHER) Publisher<String> eventPublisher) {
        super(application,
              process,
              $DataType$.class,
              $DataEventType$.class,
              "$Trigger$",
              new DefaultEventConsumerFactory(),
              configBean.useCloudEvents());

        Flux.from(eventPublisher)
                .subscribe(this::consume);
    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }
}
