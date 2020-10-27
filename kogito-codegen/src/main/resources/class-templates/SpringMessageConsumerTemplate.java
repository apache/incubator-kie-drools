package $Package$;

import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.impl.DefaultEventConsumerFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.event.impl.AbstractMessageConsumer;

@org.springframework.stereotype.Component()
public class $Type$MessageConsumer extends AbstractMessageConsumer<$Type$, $DataType$, $DataEventType$> {

    @org.springframework.beans.factory.annotation.Autowired()
    $Type$MessageConsumer(
            Application application,
            @org.springframework.beans.factory.annotation.Qualifier("$ProcessName$") Process<$Type$> process,
            ConfigBean configBean
            /*,  @Qualified("kogito_event_publisher") Publisher<String> eventPublisher */) {
        super(application,
              process,
              $DataType$.class,
              $DataEventType$.class,
              "$Trigger$",
              new DefaultEventConsumerFactory(),
              configBean.useCloudEvents());
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "$Trigger$")
    public void consume(String payload) {
        super.consume(payload);
    }

    protected $Type$ eventToModel($DataType$ event) {
        $Type$ model = new $Type$();
        model.set$DataType$(event);
        return model;
    }

}
